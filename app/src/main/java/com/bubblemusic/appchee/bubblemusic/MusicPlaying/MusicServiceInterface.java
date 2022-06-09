package com.bubblemusic.appchee.bubblemusic.MusicPlaying;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.bubblemusic.appchee.bubblemusic.MusicItem;

import java.util.ArrayList;

public class MusicServiceInterface {
    private ServiceConnection mServiceConnection;
    public MusicService mService;

    public MusicServiceInterface(Context context) {
        Log.d("TestGG","ServiceIner");
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((MusicService.MusicServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceConnection = null;
                mService = null;
            }
        };
        context.bindService(new Intent(context, MusicService.class)
                .setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void setPlayList(ArrayList<MusicItem> items) {
        if (mService != null) {
            Log.d("TestGG","Not null");
            mService.setMusicItem(items);
        }
    }
    public void setNowPos(int i)
    {
        if (mService != null) {
            mService.setNowPlayPos(i);
        }
    }

    public void play(int position) {
        if (mService != null) {
            mService.musicPlay(position);
        }
    }

    public void play() {
        if (mService != null) {
            mService.musicPlay();
        }
    }

    public void pause() {
        if (mService != null) {
            mService.pause();
        }
    }

    public void forward() {
        if (mService != null) {
            mService.forward();
        }
    }

    public void rewind() {
        if (mService != null) {
            mService.rewind();
        }
    }

    public MusicItem nowplay()
    {
        MusicItem m=mService.getnowPlay();
        return m;
    }

    public int ListSize()
    {
        return mService.Listsize();
    }

    public int getNowPos()
    {
        return mService.getNowPlayPos();
    }

    public ArrayList<MusicItem> getNowList()
    {
        return mService.getItems();
    }

    public MediaPlayer getMediaPlayer(){
        return mService.mp();
    }

    public void startNotification()
    {
        mService.setNotification();
    }

    public void setisMainActivity(boolean b)
    {
        mService.isMainActivity(b);
    }

    public boolean isBinded()
    {
        return mService.isBinded();
    }
}
