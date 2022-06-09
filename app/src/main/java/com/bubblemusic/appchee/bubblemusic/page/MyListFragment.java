package com.bubblemusic.appchee.bubblemusic.page;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bubblemusic.appchee.bubblemusic.ClassifyItem;
import com.bubblemusic.appchee.bubblemusic.ClassifyListAdapter;
import com.bubblemusic.appchee.bubblemusic.DataBase.DatabaseHelper;
import com.bubblemusic.appchee.bubblemusic.R;
import com.bubblemusic.appchee.bubblemusic.Sendlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MyListFragment extends Fragment implements OnItemClick {
    private RecyclerView mRecyclerView;
    private ClassifyListAdapter mAdapter;
    private HashMap<String,ClassifyItem> classify=null;//album,artist 분류

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        classify=new LinkedHashMap<>();
        setViewFromMyList();                        //분류
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.albumpage, container, false);
        if(classify.size() > 0) {
            ////////////////////////////////
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.album_list);
            mAdapter = new ClassifyListAdapter(classify, this);
            mRecyclerView.setAdapter(mAdapter); //test
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
        }
        return rootView;

    }

    private void setViewFromMyList()
    {
        Sendlist send=(Sendlist)getActivity();
        classify=send.getMyListClassify();
    }

    @Override //item클릭시 classify 실행
    public void onClick(ClassifyItem item) {
        Sendlist send=(Sendlist)getActivity();
        send.myListClick(item);
    }
}
