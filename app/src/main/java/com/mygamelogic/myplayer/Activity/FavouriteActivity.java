package com.mygamelogic.myplayer.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mygamelogic.myplayer.Adapter.MusicArrayAdapter;
import com.mygamelogic.myplayer.CustomView.RecyclerItemTouchHelper;
import com.mygamelogic.myplayer.Database.DatabaseHelper;
import com.mygamelogic.myplayer.Interfaces.MusicArrayInterface;
import com.mygamelogic.myplayer.Interfaces.RecyclerItemTouchHelperListener;
import com.mygamelogic.myplayer.R;
import com.mygamelogic.myplayer.Response.MusicResponseRow;

import java.util.List;

/**
 * Created by admin on 12/07/18.
 */

public class FavouriteActivity extends AppCompatActivity implements MusicArrayInterface,RecyclerItemTouchHelperListener {
    private List<MusicResponseRow> musicResponseRowList;
    private SwipeRefreshLayout swiperefresh_favlayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        swiperefresh_favlayout=(SwipeRefreshLayout)findViewById(R.id.swiperefresh_favlayout);
        setToolbar();
        showLoader();
    }
 //set top toolbar
    private void setToolbar() {
        try {
            Toolbar toolbar=findViewById(R.id.toolbar_favlayout);
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


    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDB();
    }
//----------------------------------------
//get data from DB and show list
    private void getDataFromDB(){
        DatabaseHelper databaseHelper=new DatabaseHelper(this);
        musicResponseRowList=databaseHelper.getAllFavouriteList();
        setRecyclerView();
    }

    private MusicArrayAdapter musicArrayAdapter;
    private void setRecyclerView(){
        hideLoader();
        if((musicResponseRowList!=null)&&(musicResponseRowList.size()>0)) {
            setCountText();
            if(musicArrayAdapter!=null){
                musicArrayAdapter.refreshList(musicResponseRowList);
            }else {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_favview);
                ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, FavouriteActivity.this);
                new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
                findViewById(R.id.textview_favnodata).setVisibility(View.GONE);
                swiperefresh_favlayout.setVisibility(View.VISIBLE);
                musicArrayAdapter = new MusicArrayAdapter(getApplicationContext(), musicResponseRowList);
                musicArrayAdapter.setMusicArrayInterface(FavouriteActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FavouriteActivity.this);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(musicArrayAdapter);
            }
        } else {
            SwipeRefreshLayout swiperefresh_favlayout=(SwipeRefreshLayout) findViewById(R.id.swiperefresh_favlayout);
            swiperefresh_favlayout.setVisibility(View.GONE);
            TextView textView=(TextView) findViewById(R.id.textview_favnodata);
            textView.setVisibility(View.VISIBLE);
        }
    }
   //set top count text
    private void setCountText(){
        TextView textview_totalfavsong=(TextView) findViewById(R.id.textview_totalfavsong);
        textview_totalfavsong.setText("All Songs: "+musicResponseRowList.size());
    }
//--------------------
//swipe to delete callback
    @Override
    public void onSwiped(int direction, int position) {
        if(musicArrayAdapter!=null){
            DatabaseHelper databaseHelper=new DatabaseHelper(this);
            databaseHelper.deleteRow(musicResponseRowList.get(position));
            musicResponseRowList.remove(position);
            setRecyclerView();
        }
    }
//-----------------------------------------------------------------
    private void showLoader() {
        try {
            swiperefresh_favlayout.bringToFront();
            swiperefresh_favlayout.setVisibility(View.VISIBLE);
            swiperefresh_favlayout.setColorSchemeColors(getColorBack(R.color.Orange), getColorBack(R.color.LiteGreen), getColorBack(R.color.Orange), getColorBack(R.color.LiteGreen));
            swiperefresh_favlayout.post(new Runnable() {
                @Override
                public void run() {
                    swiperefresh_favlayout.setRefreshing(false);
                }
            });
            swiperefresh_favlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swiperefresh_favlayout.setRefreshing(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideLoader() {
        swiperefresh_favlayout.setRefreshing(false);
    }
//--------------------------------------------------
//recycler view call back
    @Override
    public void tappedRow(int position) {
        Intent intent=new Intent(this, PlayActivity.class);
        Gson gson=new Gson();
        intent.putExtra("rowdata",gson.toJson(musicResponseRowList.get(position)));
        startActivity(intent);
    }

//--------------------------
//get color based on sdk version
    public int getColorBack(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(id);
        } else {
            return getResources().getColor(id);
        }
    }
}
