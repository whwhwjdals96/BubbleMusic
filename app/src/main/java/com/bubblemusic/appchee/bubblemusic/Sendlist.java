package com.bubblemusic.appchee.bubblemusic;

import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;

public interface Sendlist {
    public HashMap<Long,MusicItem> getMusiclist();
    public HashMap<String,ClassifyItem> getAlbumClassify();
    public HashMap<String,ClassifyItem> getArtistClassify();
    public HashMap<String,ClassifyItem> getgenreClassify();
    public HashMap<String,ClassifyItem> getMyListClassify();
    public void sample(ArrayList<MusicItem> data);
    public void myListClick(ClassifyItem data);
    public void nowplayExtend(FrameLayout frag);
    public float getWidth();
    public float getHight();
}
