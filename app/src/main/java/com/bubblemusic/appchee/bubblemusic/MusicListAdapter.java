package com.bubblemusic.appchee.bubblemusic;

import android.app.Activity;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bubblemusic.appchee.bubblemusic.DataBase.DatabaseHelper;
import com.bubblemusic.appchee.bubblemusic.MusicPlaying.MusicApplication;
import com.bubblemusic.appchee.bubblemusic.MusicPlaying.PlayActivity;
import com.bubblemusic.appchee.bubblemusic.page.NowPlay;
import com.bumptech.glide.Glide;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ItemViewHolder>{
    private ArrayList<MusicItem> data;
    private Context mcontext;

    public MusicListAdapter(HashMap<Long,MusicItem> hash, Context c)
    {
        data=new ArrayList<>(hash.values());
        mcontext=c;

    }//분류한거
    public MusicListAdapter(ArrayList<MusicItem> array, Context c)
    {
        data=new ArrayList<>();
        data.addAll(array);
        mcontext=c;
    }// 메인

    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent, false);
        return new ItemViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setPos(position);
        holder.onBind(data.get(position));
    }

    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return data.size();
    }

    void addItem(MusicItem addData) {
        // 외부에서 item을 추가시킬 함수입니다.
        data.add(addData);
    }



    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        private ImageView mImgAlbumArt;
        private TextView mTxtTitle;
        private TextView mTxtSubTitle;
        private TextView mTxtDuration;
        int pos=0;

        private ItemViewHolder(View view) {
            super(view);
            mImgAlbumArt = (ImageView) view.findViewById(R.id.img_albumart);
            mTxtTitle = (TextView) view.findViewById(R.id.txt_title);
            mTxtSubTitle = (TextView) view.findViewById(R.id.txt_sub_title);
            mTxtDuration = (TextView) view.findViewById(R.id.txt_duration);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicApplication.getInstance().getServiceInterface().setPlayList(data);
                    MusicApplication.getInstance().getServiceInterface().play(pos);
                    ((MainActivity)mcontext).setNowPlay(MusicApplication.getInstance().getServiceInterface().nowplay());
                    //DBupdate(data.get(pos).getmId());
                }
            });
        }
        public void onBind(MusicItem item) {
            mTxtTitle.setText(item.getTitle());
            mTxtSubTitle.setText(item.getArtist() + "(" + item.getAlbum() + ")");
            mTxtDuration.setText(android.text.format.DateFormat.format("mm:ss",item.getDuration()));
            Uri albumArtUri = ContentUris.withAppendedId(artworkUri, item.getAlbumId());
            Glide.with(itemView.getContext()).load(albumArtUri).error(R.drawable.ic_launcher_background).into(mImgAlbumArt);
            Log.d("recycle",""+pos);
        }

        public void setPos(int position)
        {
            pos=position;
        }
    }

    public void updateList(ArrayList<MusicItem> d)
    {
        data.clear();
        data.addAll(d);
    }

    private void DBupdate(Long trackid) //track 클릭시 top,recent update
    {
        DatabaseHelper dbhelper=new DatabaseHelper(mcontext.getApplicationContext());
        dbhelper.updateTopTrack(trackid); // toptrack
        dbhelper.updateRecentTable(trackid);
    }
}
