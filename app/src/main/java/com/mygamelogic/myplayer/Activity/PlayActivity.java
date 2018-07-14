package com.mygamelogic.myplayer.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.mygamelogic.myplayer.Database.DatabaseHelper;
import com.mygamelogic.myplayer.R;
import com.mygamelogic.myplayer.Response.MusicResponseRow;

/**
 * Created by admin on 13/07/18.
 */

public class PlayActivity extends AppCompatActivity implements View.OnClickListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener{
    private MusicResponseRow musicResponseRow;
    private ProgressBar progressbar_play;
    private TextView textview_startplaytime,textview_endplaytime;
    private MediaPlayer mediaPlayer;
    private CounterClass timer;
    private ImageView imageview_play,imageview_favbutton;
    private boolean audioStartedPlaying=false;
    private SwipeRefreshLayout swiperefreshparent_layout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent callingIntent=getIntent();
        Gson gson=new Gson();
        musicResponseRow=gson.fromJson(callingIntent.getStringExtra("rowdata"),MusicResponseRow.class);
        initView();
        setToolbar();
        setTexualData();
        startPlayingAudio();
        startTimer();
        setFavourite();
        showLoader();
    }
 //---------------------------------------
 //set top toolbar
    private void setToolbar() {
        try {
            Toolbar toolbar=findViewById(R.id.toolbar_playlayout);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
 //----------------------------------
 //overrided methode to handel audio play
    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(audioStartedPlaying){
            playAudio();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAudio();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(timer!=null){
            timer.cancel();
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
//----------------------------------------------
    private void initView(){
        progressbar_play=(ProgressBar) findViewById(R.id.progressbar_play);
        textview_startplaytime=(TextView) findViewById(R.id.textview_startplaytime);
        textview_endplaytime=(TextView) findViewById(R.id.textview_endplaytime);
        imageview_play=(ImageView) findViewById(R.id.imageview_play);
        imageview_favbutton=(ImageView) findViewById(R.id.imageview_favbutton);
        swiperefreshparent_layout=(SwipeRefreshLayout) findViewById(R.id.refreshlayout_playScreen);

        findViewById(R.id.layout_playbutton).setOnClickListener(this);
        findViewById(R.id.layout_listbutton).setOnClickListener(this);
        findViewById(R.id.layout_favouritebutton).setOnClickListener(this);
    }

    private void setTexualData(){
        if((musicResponseRow.getArtworkUrl100()!=null)&&(!musicResponseRow.getArtworkUrl100().isEmpty())){
            ImageView imageview_playmain=(ImageView) findViewById(R.id.imageview_playmain);
            Glide.with(this).load(musicResponseRow.getArtworkUrl100())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageview_playmain);
        }
        if(musicResponseRow.getTrackName()!=null){
            TextView textview_musictitle=(TextView) findViewById(R.id.textview_musictitle);
            textview_musictitle.setText(musicResponseRow.getTrackName());
        }
        TextView textview_musiccontent=(TextView) findViewById(R.id.textview_musiccontent);
        String content="";
        if((musicResponseRow.getArtistName()!=null)&&(!musicResponseRow.getArtistName().isEmpty())){
            content+=musicResponseRow.getArtistName();
            if((musicResponseRow.getPrimaryGenreName()!=null)&&(!musicResponseRow.getPrimaryGenreName().isEmpty())){
                content=content+"  |  "+musicResponseRow.getPrimaryGenreName();
            }
        }else{
            if((musicResponseRow.getPrimaryGenreName()!=null)&&(!musicResponseRow.getPrimaryGenreName().isEmpty())){
                content=content+musicResponseRow.getPrimaryGenreName();
            }
        }
        textview_musiccontent.setText(content);
    }
//---------------------------------------------------------------------------------------------------
//audio play related methodes
    private void pauseAudio(){
        if(mediaPlayer!=null) {
            mediaPlayer.pause();
            imageview_play.setImageDrawable(getResources().getDrawable(R.drawable.triangle));
        }
    }
    private void playAudio(){
        if(mediaPlayer!=null){
            mediaPlayer.start();
            imageview_play.setImageDrawable(getResources().getDrawable(R.drawable.combined_shape_2));
        }else {
            startPlayingAudio();
            imageview_play.setImageDrawable(getResources().getDrawable(R.drawable.combined_shape_2));
        }
    }
    private void startPlayingAudio(){
        try {
            audioStartedPlaying=true;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(musicResponseRow.getPreviewUrl());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnPreparedListener(this);
        }catch (Exception e){}
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(mediaPlayer!=null){
            mediaPlayer.start();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
//----------------------------------------------------------------------------
//timer to set progress bar status
    private void startTimer(){
        timer = new CounterClass(1000000,500);
        timer.start();
    }
    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if(mediaPlayer!=null) {
                progressbar_play.setMax(mediaPlayer.getDuration());
                progressbar_play.setProgress(mediaPlayer.getDuration());
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
            if(mediaPlayer!=null) {
                if(mediaPlayer.isPlaying()){
                    hideLoader();
                }
                if(mediaPlayer.getDuration()<(1000000)) {
                    if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration()) {
                        progressbar_play.setMax(mediaPlayer.getDuration());
                        progressbar_play.setProgress(mediaPlayer.getDuration());
                        imageview_play.setImageDrawable(getResources().getDrawable(R.drawable.triangle));
                        timer.cancel();
                    } else {
                        progressbar_play.setMax(mediaPlayer.getDuration());
                        progressbar_play.setProgress(mediaPlayer.getCurrentPosition());
                    }
                    setStartTime();
                    setendTime();
                }
            }
        }
    }
    private void setStartTime(){
        String str=String.format("%02d:%02d",mediaPlayer.getCurrentPosition()/60000,(mediaPlayer.getCurrentPosition()%60000)/1000);
        textview_startplaytime.setText(str);
    }
    private void setendTime(){
        String str=String.format("%02d:%02d",mediaPlayer.getDuration()/60000,(mediaPlayer.getDuration()%60000)/1000);
        textview_endplaytime.setText(str);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_playbutton:
                if(mediaPlayer!=null){
                    if(!mediaPlayer.isPlaying()){
                        playAudio();
                    }else {
                        pauseAudio();
                    }
                }
                break;
            case R.id.layout_favouritebutton:
                toggleFavourite();
                break;
            case R.id.layout_listbutton:
                onBackPressed();
                break;
        }
    }
 //show loader as it ll take time to load data from server
    private void showLoader() {
        try {
            swiperefreshparent_layout.bringToFront();
            swiperefreshparent_layout.setVisibility(View.VISIBLE);
            swiperefreshparent_layout.setColorSchemeColors(getColorBack(R.color.Orange), getColorBack(R.color.LiteGreen), getColorBack(R.color.Orange), getColorBack(R.color.LiteGreen));
            swiperefreshparent_layout.post(new Runnable() {
                @Override
                public void run() {
                    swiperefreshparent_layout.setRefreshing(true);
                }
            });
            swiperefreshparent_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swiperefreshparent_layout.setRefreshing(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideLoader() {
        findViewById(R.id.bottom_buttonscreen).setVisibility(View.VISIBLE);
        swiperefreshparent_layout.setRefreshing(false);
        swiperefreshparent_layout.setVisibility(View.GONE);
    }

    //get color based on sdk version
    public int getColorBack(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(id);
        } else {
            return getResources().getColor(id);
        }
    }

//handle favourite and vice versa from DB and image state
    private void toggleFavourite(){
        DatabaseHelper databaseHelper=new DatabaseHelper(this);
        if(!databaseHelper.checkForDataExist(musicResponseRow)){
            imageview_favbutton.setImageDrawable(getResources().getDrawable(R.drawable.shape_heart_red));
            databaseHelper.insertNewData(musicResponseRow);
        }else{
            imageview_favbutton.setImageDrawable(getResources().getDrawable(R.drawable.shape_heart));
            databaseHelper.deleteRow(musicResponseRow);
        }
    }
    private void setFavourite(){
        DatabaseHelper databaseHelper=new DatabaseHelper(this);
        if(databaseHelper.checkForDataExist(musicResponseRow)){
            imageview_favbutton.setImageDrawable(getResources().getDrawable(R.drawable.shape_heart_red));
        }else{
            imageview_favbutton.setImageDrawable(getResources().getDrawable(R.drawable.shape_heart));
        }
    }

}
