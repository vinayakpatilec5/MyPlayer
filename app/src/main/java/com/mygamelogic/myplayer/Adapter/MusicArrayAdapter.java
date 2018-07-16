package com.mygamelogic.myplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mygamelogic.myplayer.Interfaces.MusicArrayInterface;
import com.mygamelogic.myplayer.R;
import com.mygamelogic.myplayer.Response.MusicResponseRow;

import java.util.List;

/**
 * Created by admin on 13/07/18.
 */

public class MusicArrayAdapter extends RecyclerView.Adapter<MusicArrayAdapter.MyViewHolder>{
    private List<MusicResponseRow> musicResponseRows;

    private Context context;
    private MusicArrayInterface musicArrayInterface;
    public void setMusicArrayInterface(MusicArrayInterface musicArrayInterface) {
        this.musicArrayInterface = musicArrayInterface;
    }

    public MusicArrayAdapter(Context context, List<MusicResponseRow> musicResponseRows) {
        this.musicResponseRows = musicResponseRows;
        this.context = context;
    }
    public void refreshList(List<MusicResponseRow> musicResponseRows) {
        this.musicResponseRows=musicResponseRows;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (musicResponseRows == null) {
            return 0;
        }
        return musicResponseRows.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textview_musictitle, textview_musiccontent;
        private ImageView imageview_musicrow;
        public RelativeLayout layout_mainrow,viewBackground;
        public MyViewHolder(View view) {
            super(view);
            layout_mainrow = (RelativeLayout) view.findViewById(R.id.layout_mainrow);
            textview_musictitle = (TextView) view.findViewById(R.id.textview_musictitle);
            textview_musiccontent = (TextView) view.findViewById(R.id.textview_musiccontent);
            imageview_musicrow=(ImageView) view.findViewById(R.id.imageview_musicrow);
            viewBackground=(RelativeLayout) view.findViewById(R.id.viewBackground);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            MusicResponseRow row=musicResponseRows.get(position);
            if((row.getArtworkUrl100()!=null)&&(!row.getArtworkUrl100().isEmpty())){
                Glide.with(context).load(row.getArtworkUrl100())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageview_musicrow);
            }
            if(row.getTrackName()!=null){
                holder.textview_musictitle.setText(row.getTrackName());
            }
            String content="";
            if((row.getArtistName()!=null)&&(!row.getArtistName().isEmpty())){
                content+=row.getArtistName();
                if((row.getPrimaryGenreName()!=null)&&(!row.getPrimaryGenreName().isEmpty())){
                    content=content+"  |  "+row.getPrimaryGenreName();
                }
            }else{
                if((row.getPrimaryGenreName()!=null)&&(!row.getPrimaryGenreName().isEmpty())){
                    content=content+row.getPrimaryGenreName();
                }
            }
            holder.textview_musiccontent.setText(content);
            holder.layout_mainrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicArrayInterface.tappedRow(position);
                }
            });
        }catch (Exception e) {
            Log.e("Feed", "caught exception" + e.toString());
            e.printStackTrace();
        }
    }
}
