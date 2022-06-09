package com.bubblemusic.appchee.bubblemusic;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bubblemusic.appchee.bubblemusic.page.ClassifiyListActivity;
import com.bubblemusic.appchee.bubblemusic.page.NowPlay;
import com.bubblemusic.appchee.bubblemusic.page.OnItemClick;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassifyListAdapter extends RecyclerView.Adapter<ClassifyListAdapter.ItemViewHolder> {
    private ArrayList<ClassifyItem> data;
    private OnItemClick click;
    public ClassifyListAdapter(HashMap<String,ClassifyItem> m, OnItemClick listener)
    {
        this.data=new ArrayList<>(m.values());
        this.click=listener;
    }

    public ClassifyListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent, false);
        return new ClassifyListAdapter.ItemViewHolder(view);
    }


    @NonNull
    @Override

    public void onBindViewHolder(@NonNull ClassifyListAdapter.ItemViewHolder holder, int position) {
        holder.setPos(position);
        holder.onBind(data.get(position));
    }

    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return data.size();
    }

    void addItem(ClassifyItem addData) {
        // 외부에서 item을 추가시킬 함수입니다.
        data.add(addData);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        private ImageView mImgAlbumArt;
        private TextView mTxtTitle;
        private TextView mTxtSubTitle;
        private TextView mTxtDuration;
        private  int pos=0;
        private ItemViewHolder(View view) {
            super(view);
            mImgAlbumArt = (ImageView) view.findViewById(R.id.img_albumart);
            mTxtTitle = (TextView) view.findViewById(R.id.txt_title);
            mTxtSubTitle = (TextView) view.findViewById(R.id.txt_sub_title);
            mTxtDuration = (TextView) view.findViewById(R.id.txt_duration);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {                              //click

                    click.onClick(data.get(pos)); //artist는 test중...... classififragment
                }
            });
        }
        public void onBind(ClassifyItem item) {

            mTxtTitle.setText(item.getKey());
            mTxtSubTitle.setText("");
            mTxtDuration.setText(""+item.getCount()+"Songs");
            if(!item.getItem().isEmpty())
            {
                Uri albumArtUri = ContentUris.withAppendedId(artworkUri, item.getItem().get(0).getAlbumId());
                Glide.with(itemView.getContext()).load(albumArtUri).error(R.drawable.ic_launcher_background).into(mImgAlbumArt);
            }
        }

        public void setPos(int position)
        {
            pos=position;
        }
    }
}
