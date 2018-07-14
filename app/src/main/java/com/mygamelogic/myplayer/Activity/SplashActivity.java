package com.mygamelogic.myplayer.Activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mygamelogic.myplayer.Adapter.MusicArrayAdapter;
import com.mygamelogic.myplayer.Interfaces.MusicArrayInterface;
import com.mygamelogic.myplayer.R;
import com.mygamelogic.myplayer.Response.MusicResponseRow;
import com.mygamelogic.myplayer.Response.SearchMusicResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by admin on 12/07/18.
 */

public class SplashActivity extends Activity implements View.OnClickListener, MusicArrayInterface,View.OnTouchListener{
    private EditText editext_search;
    private SwipeRefreshLayout swiperefreshparent_layout;
    private OkHttpClient client = new OkHttpClient();
    private SearchMusicResponse currentResponse;
    private MusicArrayAdapter musicArrayAdapter;
    private RecyclerView recyclerView;
    private List<String> searchStrArray=new ArrayList<>();
    private int currentSearcElement=0;
    private String previousSearchStr="";
    private int musicRowLimit=4;
    private Boolean doubleBackToExitPressedOnce=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
        setClickListener();
        animateAndShowLogo(findViewById(R.id.layout_logo));
        setSearchBarAppearAnim(findViewById(R.id.layout_maintobbar));
        setSearchBarPosition();
    }
    private void initViews(){
        editext_search=(EditText) findViewById(R.id.editext_search);
        swiperefreshparent_layout=(SwipeRefreshLayout) findViewById(R.id.swiperefreshparent_layout);
        recyclerView=(RecyclerView) findViewById(R.id.recycler_view);
    }
    private void setClickListener(){
        editext_search.setOnClickListener(this);
        findViewById(R.id.imageview_gosearch).setOnClickListener(this);
        findViewById(R.id.imageview_favourite).setOnClickListener(this);
        findViewById(R.id.layout_swipe).setOnTouchListener(this);
    }
//show logo with with animation
    private void animateAndShowLogo(View view){
        view.setAlpha(0);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1.0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0, 1.0f);
        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.play(scaleX).with(scaleY).with(alpha);
        scaleUp.setStartDelay(300);
        scaleUp.setInterpolator(new DecelerateInterpolator());
        scaleUp.setDuration(500);
        scaleUp.start();
    }
 //set searchbar initial position and animate after 2 sec
 //hide logo and other thing at same time.
    private void setSearchBarAppearAnim(View view){
        view.setAlpha(0);
        findViewById(R.id.layout_search).setVisibility(View.VISIBLE);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0, 1.0f);
        alpha.setStartDelay(1800);
        alpha.setDuration(500);
        alpha.start();
    }
    private void setSearchBarPosition(){
        LinearLayout layout_maintobbar=(LinearLayout) findViewById(R.id.layout_maintobbar);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int scrHeight = metrics.heightPixels;
        layout_maintobbar.setY(scrHeight/2);
    }
    private void showSearchBardToTop(){
        setEditextValueChangeListener();
        hideLogowithAnim(findViewById(R.id.layout_logo));
        setSearchBarToTopAnim();
        findViewById(R.id.textview_listenlabel).setVisibility(View.GONE);
        findViewById(R.id.topbar).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_listviewmain).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_main).setBackgroundColor(getColorBack(R.color.white_light));
    }

    private void setSearchBarToTopAnim(){
        LinearLayout layout_maintobbar=(LinearLayout)findViewById(R.id.layout_maintobbar);
        layout_maintobbar.animate().translationY(0).setDuration(250).setInterpolator(new DecelerateInterpolator()).start();
    }
    private void hideLogowithAnim(View view){
        view.setAlpha(1);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX",  1.0f,0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f,0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1.0f,0);
        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.play(scaleX).with(scaleY).with(alpha);
        scaleUp.setInterpolator(new DecelerateInterpolator());
        scaleUp.setDuration(250);
        scaleUp.start();
    }
//---------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editext_search:
                if(v.getId()==R.id.editext_search){
                    if(findViewById(R.id.layout_maintobbar).getY()>100) {
                        showSearchBardToTop();
                    }
                }
                break;
            case R.id.imageview_gosearch:
                searchButtonTapped(false);
                break;
            case R.id.imageview_favourite:
                Intent intent=new Intent(this, FavouriteActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.finish();
            System.exit(0);
            return;
        }
        doubleBackToExitPressedOnce = true;
        showToast(getString(R.string.backbtn_text));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public int getColorBack(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(id);
        } else {
            return getResources().getColor(id);
        }
    }
    //---------------------------------------------------------------------------------------------------
    //loader related methode
    private void showLoader() {
        try {
            if(swiperefreshparent_layout.getVisibility()==View.GONE) {
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
            }else {
                swiperefreshparent_layout.setRefreshing(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideLoader() {
        swiperefreshparent_layout.setRefreshing(false);
    }

    //--------------------------------------------------------------------------------------------------------
//search methode
    private void setEditextValueChangeListener(){
        editext_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if search text is not empty show go button
                if(editext_search.getText().toString().isEmpty()){
                    findViewById(R.id.imageview_gosearch).setVisibility(View.GONE);
                }else{
                    findViewById(R.id.imageview_gosearch).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        editext_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    searchButtonTapped(false);
                    return true;
                }
                return false;
            }
        });
    }
    private void searchButtonTapped(boolean isSwiped){
        if((!editext_search.getText().toString().isEmpty())){
            if((!previousSearchStr.equals(editext_search.getText().toString()))) {
                previousSearchStr=editext_search.getText().toString();
                TextView textview_allsong=(TextView) findViewById(R.id.textview_allsong);
                textview_allsong.setText("");
                findViewById(R.id.imageview_gosearch).setVisibility(View.GONE);
                if (musicArrayAdapter != null) {
                    musicArrayAdapter.refreshList(new ArrayList<MusicResponseRow>());
                }
                findViewById(R.id.textview_nodata).setVisibility(View.GONE);
                hideKeyboard(editext_search);
                showLoader();
                OkHttpHandler okHttpHandler = new OkHttpHandler();
                okHttpHandler.execute(editext_search.getText().toString());
                if (!isSwiped) {
                    handleSearchArr(editext_search.getText().toString());
                }
            }else {
                showToast("choose different search text.");
            }
        }else{
            showToast(getString(R.string.nosearch_text));
        }
    }
    public void hideKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
        }
    }
    //--------------------------------------------------------------------------------------------------------------------------
    //custom swipe listenr to back and forth search position
    private void handleSearchArr(String searchStr){
        if(searchStrArray.size()==4){
            searchStrArray.remove(0);
            searchStrArray.add(searchStr);
            currentSearcElement=3;
        }else {
            searchStrArray.add(searchStr);
            currentSearcElement=(searchStrArray.size()-1);
        }
        setIndicator();
    }
    private float x1, x2;
    private float y1, y2;
    private final int MIN_DISTANCE = 20;
    private boolean touchedOnce = false;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int motionEvent = event.getAction();
        if (motionEvent == MotionEvent.ACTION_DOWN) {
            x1 = event.getX();
            y1 = event.getY();
            touchedOnce = true;
        } else if (motionEvent == MotionEvent.ACTION_MOVE) {
            if (touchedOnce) {
                x2 = event.getX();
                y2 = event.getY();
                float deltaX = x1 - x2;
                float deltaY = y1 - y2;
                if (deltaX > 0 && deltaY > 0) {
                    if (deltaX > deltaY) {
                        if (deltaX > MIN_DISTANCE) {
                            touchedOnce = false;
                            //left Swipe
                            leftSwipe();
                            return true;
                        }
                    }
                } else if (deltaX < 0 && deltaY > 0) {
                    if ((-deltaX) > deltaY) {
                        if ((-deltaX) > MIN_DISTANCE) {
                            touchedOnce = false;
                            //right swipe
                            rightSwipe();
                            return true;
                        }
                    }

                } else if (deltaX < 0 && deltaY < 0) {
                    if (deltaX < deltaY) {
                        if ((-deltaX) > MIN_DISTANCE) {
                            touchedOnce = false;
                            //right swipe
                            rightSwipe();
                            return true;
                        }
                    }
                } else if (deltaX > 0 && deltaY < 0) {
                    if (deltaX > (-deltaY)) {
                        if (deltaX > MIN_DISTANCE) {
                            touchedOnce = false;
                            //left swipe
                            leftSwipe();
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void swipe(int direction) {
        if(direction==1){
            leftSwipe();
        }else if(direction==2){
            rightSwipe();
        }
    }

    private void leftSwipe(){
        if(currentSearcElement<3){
            if(searchStrArray.size()>(currentSearcElement+1)) {
                currentSearcElement++;
                editext_search.setText(searchStrArray.get(currentSearcElement));
                editext_search.setSelection(searchStrArray.get(currentSearcElement).length());
                searchButtonTapped(true);
                setIndicator();
            }
        }
    }
    private void rightSwipe(){
        if(currentSearcElement>0){
            if(searchStrArray.size()>currentSearcElement) {
                currentSearcElement--;
                editext_search.setText(searchStrArray.get(currentSearcElement));
                editext_search.setSelection(searchStrArray.get(currentSearcElement).length());
                searchButtonTapped(true);
                setIndicator();
            }
        }
    }
 // we have four layout indiactor depend on search handle those
    private void setIndicator(){
        switch (searchStrArray.size()){
            case 1:
                findViewById(R.id.indicatora).setVisibility(View.VISIBLE);
                break;
            case 2:
                findViewById(R.id.indicatorb).setVisibility(View.VISIBLE);
                break;
            case 3:
                findViewById(R.id.indicatorc).setVisibility(View.VISIBLE);
                break;
            case 4:
                findViewById(R.id.indicatord).setVisibility(View.VISIBLE);
                break;
        }
        findViewById(R.id.indicatora).setBackgroundColor(getColorBack(R.color.gray));
        findViewById(R.id.indicatorb).setBackgroundColor(getColorBack(R.color.gray));
        findViewById(R.id.indicatorc).setBackgroundColor(getColorBack(R.color.gray));
        findViewById(R.id.indicatord).setBackgroundColor(getColorBack(R.color.gray));
        switch (currentSearcElement){
            case 0:
                findViewById(R.id.indicatora).setBackgroundColor(getColorBack(R.color.LiteGreen_transparenta));
                break;
            case 1:
                findViewById(R.id.indicatorb).setBackgroundColor(getColorBack(R.color.LiteGreen_transparenta));
                break;
            case 2:
                findViewById(R.id.indicatorc).setBackgroundColor(getColorBack(R.color.LiteGreen_transparenta));
                break;
            case 3:
                findViewById(R.id.indicatord).setBackgroundColor(getColorBack(R.color.LiteGreen_transparenta));
                break;
        }

    }

    //----------------------------------------------------------------------------------------------------------------------------------------
    private void setRecyclerView(){
        if((currentResponse!=null)) {
            if (currentResponse.getResults().size() > 0) {
                if (musicArrayAdapter == null) {
                    musicArrayAdapter = new MusicArrayAdapter(getApplicationContext(), currentResponse.getResults());
                    musicArrayAdapter.setMusicArrayInterface(SplashActivity.this);
                    musicArrayAdapter.setSearchArray(true);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SplashActivity.this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(musicArrayAdapter);
                } else {
                    musicArrayAdapter.refreshList(currentResponse.getResults());
                }
            } else {
                swiperefreshparent_layout.setVisibility(View.GONE);
                TextView textView=(TextView) findViewById(R.id.textview_nodata);
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.emptysearch_text));
            }
        }else{
            swiperefreshparent_layout.setVisibility(View.GONE);
            TextView textView=(TextView) findViewById(R.id.textview_nodata);
            textView.setVisibility(View.VISIBLE);
            textView.setText(getString(R.string.nointernet_text));
        }
    }

    @Override
    public void tappedRow(int position) {
        Intent intent=new Intent(this, PlayActivity.class);
        Gson gson=new Gson();
        intent.putExtra("rowdata",gson.toJson(currentResponse.getResults().get(position)));
        startActivity(intent);
    }

    private void showToast(String message){
        Toast toast1 = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast1.show();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------
    //get search data from itunes server
    public class OkHttpHandler extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            Request.Builder builder = new Request.Builder();
            String mainUrlStr="http://itunes.apple.com/search?term={searchText}&limit=5";
            mainUrlStr=mainUrlStr.replace("{searchText}",(String)objects[0]);
            builder.url(mainUrlStr);
            Request request = builder.build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            hideLoader();
            Gson gson=new Gson();
            currentResponse=gson.fromJson(((String)o),SearchMusicResponse.class);
            if(currentResponse!=null) {
                TextView textview_allsong = (TextView) findViewById(R.id.textview_allsong);
                textview_allsong.setText("All songs: " + currentResponse.getResultCount());
            }
            setRecyclerView();
        }
    }
}
