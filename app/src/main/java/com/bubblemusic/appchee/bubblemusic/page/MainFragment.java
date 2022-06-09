package com.bubblemusic.appchee.bubblemusic.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import com.bubblemusic.appchee.bubblemusic.MainActivity;
import com.bubblemusic.appchee.bubblemusic.MusicItem;
import com.bubblemusic.appchee.bubblemusic.MusicListAdapter;
import com.bubblemusic.appchee.bubblemusic.MusicPlaying.PlayActivity;
import com.bubblemusic.appchee.bubblemusic.R;
import com.bubblemusic.appchee.bubblemusic.Sendlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MainFragment extends Fragment {

    HashMap<Long,MusicItem> viewList=null;
    private RecyclerView  mRecyclerView;
    private MusicListAdapter mAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewList=new LinkedHashMap<>();
        Sendlist send=(Sendlist)getActivity();
        viewList.putAll(send.getMusiclist());

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.mainpage, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.main_list);
        mAdapter = new MusicListAdapter(viewList,(MainActivity)getActivity());

        mRecyclerView.setAdapter(mAdapter); //test
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        return rootView;
    }
}
