package com.bubblemusic.appchee.bubblemusic;

import java.util.ArrayList;

public class InitDataSave {
    private int tabpos=0;
    private int listpos=0;
    private int duration=1000;
    private int seekbarPos=0;
    private ArrayList<MusicItem> data=new ArrayList<>();

    public InitDataSave(int tab,int lPos,ArrayList<MusicItem> data)
    {
        this.data.addAll(data);
        this.listpos=lPos;
        this.tabpos=tab;
    }

    public InitDataSave()
    {

    }

    public void setData(ArrayList<MusicItem> data) {
        this.data = data;
    }

    public void setListpos(int listpos) {
        this.listpos = listpos;
    }

    public void setTabpos(int tabpos) {
        this.tabpos = tabpos;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setSeekbarPos(int seekbarPos) {
        this.seekbarPos = seekbarPos;
    }

    public ArrayList<MusicItem> getData()
    {
        return this.data;
    }
    public int getTabpos()
    {
        return this.tabpos;
    }
    public int getListpos()
    {
        return this.listpos;
    }

    public int getDuration() {
        return duration;
    }

    public int getSeekbarPos() {
        return seekbarPos;
    }
}
