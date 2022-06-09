package com.bubblemusic.appchee.bubblemusic.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.bubblemusic.appchee.bubblemusic.ClassifyItem;
import com.bubblemusic.appchee.bubblemusic.ClassifyListAdapter;
import com.bubblemusic.appchee.bubblemusic.MusicListAdapter;
import com.bubblemusic.appchee.bubblemusic.MusicPlaying.PlayActivity;
import com.bubblemusic.appchee.bubblemusic.R;

public class ClassifiyListActivity extends AppCompatActivity {
    ClassifyItem item=null;
    private RecyclerView mRecyclerView;
    private MusicListAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewlist);

        item= (ClassifyItem) getIntent().getSerializableExtra("classify");

        mAdapter = new MusicListAdapter(item.getItem(),this);
        mRecyclerView=findViewById(R.id.main_list);
        mRecyclerView.setAdapter(mAdapter); //test

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.add(R.id.fragmentNow_2,new NowPlay());
        ft.commit();

    }
}
