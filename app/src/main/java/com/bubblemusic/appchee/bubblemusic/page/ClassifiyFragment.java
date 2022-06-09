package com.bubblemusic.appchee.bubblemusic.page;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bubblemusic.appchee.bubblemusic.MainActivity;
import com.bubblemusic.appchee.bubblemusic.MusicItem;
import com.bubblemusic.appchee.bubblemusic.MusicListAdapter;
import com.bubblemusic.appchee.bubblemusic.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClassifiyFragment extends Fragment {
    private ArrayList<MusicItem> items=new ArrayList<>();
    private RecyclerView mRecyclerView;
    private MusicListAdapter mAdapter;

    public ClassifiyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_classifiy, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id._sublist);
        mAdapter = new MusicListAdapter(items,(MainActivity)getActivity());

        mRecyclerView.setAdapter(mAdapter); //test
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    public void listUpdate(ArrayList<MusicItem> data) //classifiy가 눌릴때 recycleview update
    {
        mAdapter.updateList(data);
        mAdapter.notifyDataSetChanged();
    }
}
