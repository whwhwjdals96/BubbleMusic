package com.bubblemusic.appchee.bubblemusic.DataBase;

import java.util.ArrayList;

public class MyTopTrack {
    private Long trackID;
    private int count;

    public MyTopTrack(Long ID, int _count)
    {
        this.trackID=ID;
        this.count=_count;
    }

    public Long getTrackID()
    {
        return this.trackID;
    }
    public int getCount()
    {
        return this.count;
    }
}
