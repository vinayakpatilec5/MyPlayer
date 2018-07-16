package com.mygamelogic.myplayer.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mygamelogic.myplayer.Activity.PlayActivity;
import com.mygamelogic.myplayer.Adapter.MusicArrayAdapter;
import com.mygamelogic.myplayer.CustomTools.AppData;
import com.mygamelogic.myplayer.CustomTools.MyTools;
import com.mygamelogic.myplayer.Interfaces.MusicArrayInterface;
import com.mygamelogic.myplayer.R;
import com.mygamelogic.myplayer.Response.MusicResponseRow;
import com.mygamelogic.myplayer.Response.SearchMusicResponse;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by admin on 16/07/18.
 */

public class MusicFragment extends android.support.v4.app.Fragment implements MusicArrayInterface {
    private SwipeRefreshLayout swiperefreshparent_layout;
    private RecyclerView recycler_view;
    private TextView textview_allsong,textview_nodata;
    private SearchMusicResponse currentResponse;
    private String currentSearcText;
    private MusicArrayAdapter musicArrayAdapter;
    private OkHttpClient client = new OkHttpClient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_music, container, false);
        initViews(view);
        startSearchingData();
        createLoader();
        return view;
    }
    public void setSearcText(String currentSearchStr){
        this.currentSearcText=currentSearchStr;
    }



    private void startSearchingData(){
        if(AppData.getInstance().getResponseMap().containsKey(currentSearcText)){
            currentResponse=AppData.getInstance().getResponseMap().get(currentSearcText);
            textview_allsong.setText("All songs: " + currentResponse.getResultCount());
            setRecyclerView();
        }else {
            textview_allsong.setText("");
            if (musicArrayAdapter != null) {
                musicArrayAdapter.refreshList(new ArrayList<MusicResponseRow>());
            }
            showLoader();
            OkHttpHandler okHttpHandler = new OkHttpHandler();
            okHttpHandler.execute(currentSearcText);
        }
    }


    private void initViews(View view){
        swiperefreshparent_layout=(SwipeRefreshLayout) view.findViewById(R.id.swiperefreshparent_layout);
        recycler_view=(RecyclerView) view.findViewById(R.id.recycler_view);
        textview_allsong=(TextView) view.findViewById(R.id.textview_allsong);
        textview_nodata=(TextView) view.findViewById(R.id.textview_nodata);
    }
    private void setRecyclerView(){
        if((currentResponse!=null)) {
            if (currentResponse.getResults().size() > 0) {
                if (musicArrayAdapter == null) {
                    musicArrayAdapter = new MusicArrayAdapter(getActivity(), currentResponse.getResults());
                    musicArrayAdapter.setMusicArrayInterface(this);
                    //musicArrayAdapter.setSearchArray(true);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    recycler_view.setLayoutManager(mLayoutManager);
                    recycler_view.setItemAnimator(new DefaultItemAnimator());
                    recycler_view.setAdapter(musicArrayAdapter);
                } else {
                    musicArrayAdapter.refreshList(currentResponse.getResults());
                }
            } else {
                showErrorMessage(getString(R.string.emptysearch_text));
            }
        }else{
            showErrorMessage(getString(R.string.nointernet_text));
        }
    }

    private void showErrorMessage(String error){
        swiperefreshparent_layout.setVisibility(View.GONE);
        textview_nodata.setVisibility(View.VISIBLE);
        textview_nodata.setText(error);
    }

    @Override
    public void tappedRow(int position) {
        Intent intent=new Intent(getActivity(), PlayActivity.class);
        Gson gson=new Gson();
        intent.putExtra("rowdata",gson.toJson(currentResponse.getResults().get(position)));
        startActivity(intent);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------
    //get search data from itunes server
    public class OkHttpHandler extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            Request.Builder builder = new Request.Builder();
            String mainUrlStr = "http://itunes.apple.com/search?term={searchText}&limit=4";
            mainUrlStr = mainUrlStr.replace("{searchText}", (String) objects[0]);
            builder.url(mainUrlStr);
            Request request = builder.build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            hideLoader();
            Gson gson = new Gson();
            currentResponse = gson.fromJson(((String) o), SearchMusicResponse.class);
            if (currentResponse != null) {
                textview_allsong.setText("All songs: " + currentResponse.getResultCount());
                AppData.getInstance().getResponseMap().put(currentSearcText,currentResponse);
            }
            setRecyclerView();
        }
    }

    //---------------------------------------------------------------------------------------------------
    //loader related methode
    private void createLoader(){
        swiperefreshparent_layout.setColorSchemeColors(MyTools.getColorBack(getActivity(),R.color.Orange),
                MyTools.getColorBack(getActivity(),R.color.LiteGreen), MyTools.getColorBack(getActivity(),R.color.Orange),
                MyTools.getColorBack(getActivity(),R.color.LiteGreen));
    }
    private void showLoader() {
        try {
            if (swiperefreshparent_layout.getVisibility() == View.GONE) {
                swiperefreshparent_layout.setVisibility(View.VISIBLE);
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
            } else {
                swiperefreshparent_layout.setRefreshing(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void hideLoader() {
        swiperefreshparent_layout.setRefreshing(false);
    }


}
