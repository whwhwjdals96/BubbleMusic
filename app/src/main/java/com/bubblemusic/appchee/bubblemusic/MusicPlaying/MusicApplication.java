package com.bubblemusic.appchee.bubblemusic.MusicPlaying;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.bubblemusic.appchee.bubblemusic.MainActivity;
import com.bubblemusic.appchee.bubblemusic.MusicItem;
import com.bubblemusic.appchee.bubblemusic.page.ClassifiyListActivity;

public class MusicApplication extends Application {
    private static MusicApplication mInstance;
    private MusicServiceInterface mInterface;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TestGG","Music");
        mInstance = this;
        mInterface = new MusicServiceInterface(getApplicationContext());
    }

    public static MusicApplication getInstance() {
        return mInstance;
    }

    public MusicServiceInterface getServiceInterface() {
        return mInterface;
    }

    ///////

}
