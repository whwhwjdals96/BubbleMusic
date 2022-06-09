package com.bubblemusic.appchee.bubblemusic.MusicPlaying;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bubblemusic.appchee.bubblemusic.DataBase.DatabaseHelper;
import com.bubblemusic.appchee.bubblemusic.InitDataSave;
import com.bubblemusic.appchee.bubblemusic.MainActivity;
import com.bubblemusic.appchee.bubblemusic.MusicItem;
import com.bubblemusic.appchee.bubblemusic.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

//public class MusicService extends Service {
//    private final IBinder mBinder = new MusicServiceBinder();
//    private MediaPlayer mMediaPlayer;
//    private boolean isPrepared;
//
//    private int nowPlayPos=0;
//
//    private NotificationPlayer notificationPlayer;
//
//    private ArrayList<MusicItem> items=new ArrayList<>();
//
//    public class MusicServiceBinder extends Binder {
//        MusicService getService() {
//            Log.d("TestGG","getservice");
//            return MusicService.this;
//        }
//    }
//
//    public MusicService() {
//    }
//
//    public void onCreate()
//    {
//        super.onCreate();
//        Log.d("TestGG","service Create");
//        notificationPlayer=new NotificationPlayer(this);
//        mMediaPlayer=new MediaPlayer();
//        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
//        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                isPrepared = true;
//                mp.start();
//            }
//        });
//        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                isPrepared = false;
//                updateNotificationPlayer();
//            }
//        });
//        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                isPrepared = false;
//                removeNotificationPlayer();
//                return false;
//            }
//        });
//        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//            @Override
//            public void onSeekComplete(MediaPlayer mp) {
//
//            }
//        });
//    }
//
//    private void updateNotificationPlayer() {
//        if (notificationPlayer != null) {
//            notificationPlayer.updateNotificationPlayer();
//        }
//    }
//
//    private void removeNotificationPlayer() {
//        if (notificationPlayer != null) {
//            notificationPlayer.removeNotificationPlayer();
//        }
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        return mBinder;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mMediaPlayer != null) {
//            mMediaPlayer.stop();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
//    }
//
//    public void setMusicItem(ArrayList<MusicItem> data)
//    {
//        if(!items.equals(data))
//        {
//            items.clear();
//            items.addAll(data);
//        }
//    }
//
//    private void prepare()
//    {
//        try {
//            mMediaPlayer.setDataSource(items.get(nowPlayPos).getDataPath());
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mMediaPlayer.prepareAsync();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void stop() {
//        mMediaPlayer.stop();
//        mMediaPlayer.reset();
//    }
//
//    public void musicPlay(int pos)
//    {
//        nowPlayPos=pos;
//        stop();
//        prepare();
//        updateNotificationPlayer();
//        DBupdate(items.get(nowPlayPos).getmId());
//    }
//    public void musicPlay() {
//        if (isPrepared) {
//            mMediaPlayer.start();
//            updateNotificationPlayer();
//            DBupdate(items.get(nowPlayPos).getmId());
//        }
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null) {
//            String action = intent.getAction();
//            if (action.equals("PLAY")) {
//                if (mMediaPlayer.isPlaying()) {
//                    pause();
//                } else {
//                    musicPlay();
//                }
//            } else if (action.equals("REWIND")) {
//                rewind();
//            } else if (action.equals("FORWARD")) {
//                forward();
//            } else if (action.equals("CLOSE")) {
//                pause();
//                removeNotificationPlayer();
//            }
//        }
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    public void pause() {
//        if (isPrepared) {
//            mMediaPlayer.pause();
//            updateNotificationPlayer();
//        }
//    }
//
//    public void forward() {
//        if (items.size() - 1 > nowPlayPos) {
//            nowPlayPos++; // 다음 포지션으로 이동.
//        } else {
//            nowPlayPos = 0; // 처음 포지션으로 이동.
//        }
//        musicPlay(nowPlayPos);
//    }
//
//    public void rewind() {
//        if (nowPlayPos > 0) {
//            nowPlayPos--; // 이전 포지션으로 이동.
//        } else {
//            nowPlayPos = items.size() - 1; // 마지막 포지션으로 이동.
//        }
//        musicPlay(nowPlayPos);
//    }
//
//    public MusicItem getnowPlay()
//    {
//        return items.get(nowPlayPos);
//    }
//
//    public int Listsize()
//    {
//        return items.size();
//    }
//
//    public ArrayList<MusicItem> getItems()
//    {
//        return items;
//    }
//    public int getNowPlayPos()
//    {
//        return nowPlayPos;
//    }
//    public  void setNowPlayPos(int i)
//    {
//        nowPlayPos=i;
//    }
//    private void DBupdate(Long trackid) //track 클릭시 top,recent update
//    {
//        DatabaseHelper dbhelper=new DatabaseHelper(getApplicationContext());
//        dbhelper.updateTopTrack(trackid); // toptrack
//        dbhelper.updateRecentTable(trackid);
//    }
//
//    public MediaPlayer mp()
//    {
//        if(mMediaPlayer!=null)
//        {
//            return mMediaPlayer;
//        }
//        else
//        {
//            return null;
//        }
//    }
//}

//////////////var2


public class MusicService extends Service {
    public boolean isMainactivity=true;

    private final IBinder mBinder = new MusicServiceBinder();
    private MediaPlayer mMediaPlayer;
    private boolean isPrepared;
    ServiceConnection sb;

    private Notification notification;
    private NotificationManager mNotificationManager;
    private PendingIntent mMainPendingIntent;
    private NotificationCompat.Builder mNotificationBuilder;
    private RemoteViews mRemoteViews;
    private final String channelID="bubbleMusicNoti";
    private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
    private final int NOTIFICATION_PLAYER_ID=141;
    private final long buttonSleepTime=700;
    private boolean isForeground;
    public boolean isNoti=false;
    public long lastclicktime=0;

    private int nowPlayPos=0;
    boolean binded=true;

    private ArrayList<MusicItem> items=new ArrayList<>();

    Intent sendAction;


    public class MusicServiceBinder extends Binder {
        MusicService getService() {
            Log.d("TestGG","getservice");
            return MusicService.this;
        }
    }

    public MusicService() {
    }

    public void onCreate()
    {
        super.onCreate();
        Log.d("TestGG","service Create");
        mMediaPlayer=new MediaPlayer();
        sendAction=new Intent();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                mp.start();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPrepared = false;
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isPrepared = false;
                return false;
            }
        });
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {

            }
        });
        sb=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binded=true;
                Log.d("serviceProgress","connect::"+binded);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                binded=false;
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d("serviceProgress","destroy");
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void setMusicItem(ArrayList<MusicItem> data)
    {
        if(!items.equals(data))
        {
            items.clear();
            items.addAll(data);
        }
    }

    private void prepare()
    {
        try {
            mMediaPlayer.setDataSource(items.get(nowPlayPos).getDataPath());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        sendBroadcast(new Intent("CLOSE"));
    }

    public void musicPlay(int pos)
    {
        nowPlayPos=pos;
        stop();
        prepare();
        DBupdate(items.get(nowPlayPos).getmId());
        setNotification();
        sendBroadcast(new Intent("PLAY"));
    }
    public void musicPlay() {
        if (isPrepared) {
            mMediaPlayer.start();
            DBupdate(items.get(nowPlayPos).getmId());
            setNotification();
            sendBroadcast(new Intent("PLAY"));
        }
        else
        {
            musicPlay(nowPlayPos);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("whenonstart","ssss");
        if (intent != null) {
            String action = intent.getAction();
            if (action.equals("PLAY")) {
                if(SystemClock.elapsedRealtime()-lastclicktime>buttonSleepTime) {
                    lastclicktime=SystemClock.elapsedRealtime();
                    Log.d("timetime",""+(SystemClock.elapsedRealtime()-lastclicktime));
                    if (mMediaPlayer.isPlaying()) {
                        pause();
                    } else {
                        musicPlay();
                    }
                }
            } else if (action.equals("REWIND")) {
                if(SystemClock.elapsedRealtime()-lastclicktime>buttonSleepTime) {
                    lastclicktime=SystemClock.elapsedRealtime();
                    Log.d("timetime",""+(SystemClock.elapsedRealtime()-lastclicktime));
                    rewind();
                }

            } else if (action.equals("FORWARD")) {
                if(SystemClock.elapsedRealtime()-lastclicktime>buttonSleepTime) {
                    lastclicktime=SystemClock.elapsedRealtime();
                    Log.d("timetime",""+(SystemClock.elapsedRealtime()-lastclicktime));
                    forward();
                }

            } else if (action.equals("CLOSE")) {
                pause();
                removeNotification();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void pause() {
        if (isPrepared) {
            mMediaPlayer.pause();
            sendBroadcast(new Intent("CLOSE"));
        }
    }

    public void forward() {
        if (items.size() - 1 > nowPlayPos) {
            nowPlayPos++; // 다음 포지션으로 이동.
        } else {
            nowPlayPos = 0; // 처음 포지션으로 이동.
        }
        sendBroadcast(new Intent("FORWARD"));
        musicPlay(nowPlayPos);
    }

    public void rewind() {
        if (nowPlayPos > 0) {
            nowPlayPos--; // 이전 포지션으로 이동.
        } else {
            nowPlayPos = items.size() - 1; // 마지막 포지션으로 이동.
        }
        sendBroadcast(new Intent("REWIND"));
        musicPlay(nowPlayPos);
    }

    public MusicItem getnowPlay()
    {
        return items.get(nowPlayPos);
    }

    public int Listsize()
    {
        return items.size();
    }

    public ArrayList<MusicItem> getItems()
    {
        return items;
    }
    public int getNowPlayPos()
    {
        return nowPlayPos;
    }
    public  void setNowPlayPos(int i)
    {
        nowPlayPos=i;
    }
    private void DBupdate(Long trackid) //track 클릭시 top,recent update
    {
        DatabaseHelper dbhelper=new DatabaseHelper(getApplicationContext());
        dbhelper.updateTopTrack(trackid); // toptrack
        dbhelper.updateRecentTable(trackid);
    }

    public MediaPlayer mp()
    {
        if(mMediaPlayer!=null)
        {
            return mMediaPlayer;
        }
        else
        {
            return null;
        }
    }

    public void setNotification()
    {

        if(isNoti==false) {
            isNoti = true;
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelID, "Music", NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(channel);
            }
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            mMainPendingIntent = PendingIntent.getActivity(this, 0, mainActivity, 0);
            mRemoteViews = createRemoteView(R.layout.notification_player);
            mRemoteViews.setTextViewText(R.id.txt_title, getItems().get(getNowPlayPos()).getTitle());
            mNotificationBuilder = new NotificationCompat.Builder(this, channelID);
            mNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .setContentIntent(mMainPendingIntent)
                    .setContent(mRemoteViews);
            notification = mNotificationBuilder.build();

            //notification.contentIntent = mMainPendingIntent;
            if (!isForeground) {
                isForeground = true;
                // 서비스를 Foreground 상태로 만든다
                this.startForeground(NOTIFICATION_PLAYER_ID, notification);
            }
            if(getItems().size()>0) {
                Log.d("chochocho","new:::"+getItems().get(getNowPlayPos()).getTitle());
                NotificationTarget notificationTarget = new NotificationTarget(this,
                        R.id.img_albumart,
                        mRemoteViews,
                        notification,
                        NOTIFICATION_PLAYER_ID);
                Uri albumArtUri = ContentUris.withAppendedId(artworkUri, getItems().get(getNowPlayPos()).getAlbumId());
                Glide.with(this).asBitmap().load(albumArtUri).error(R.drawable.ic_launcher_background).into(notificationTarget);
            }
        }
        else
        {
            changeNotification();
        }
    }

    private RemoteViews createRemoteView(int layoutId) {
        Log.d("remoteview","true");
        RemoteViews remoteView = new RemoteViews(this.getPackageName(), layoutId);
        Intent actionTogglePlay = new Intent("PLAY");
        Intent actionForward = new Intent("FORWARD");
        Intent actionRewind = new Intent("REWIND");
        Intent actionClose = new Intent("CLOSE");
        PendingIntent togglePlay = PendingIntent.getService(this, 0, actionTogglePlay, 0);
        PendingIntent forward = PendingIntent.getService(this, 0, actionForward, 0);
        PendingIntent rewind = PendingIntent.getService(this, 0, actionRewind, 0);
        PendingIntent close = PendingIntent.getService(this, 0, actionClose, PendingIntent.FLAG_CANCEL_CURRENT);

        remoteView.setOnClickPendingIntent(R.id.btn_play_pause, togglePlay);
        remoteView.setOnClickPendingIntent(R.id.btn_forward, forward);
        remoteView.setOnClickPendingIntent(R.id.btn_rewind, rewind);
        remoteView.setOnClickPendingIntent(R.id.btn_close, close);
        return remoteView;
    }

    private void changeNotification()
    {
        if(getItems().size()>0) {
            Log.d("chochocho","change:::"+getItems().get(getNowPlayPos()).getTitle());
            mRemoteViews.setTextViewText(R.id.txt_title, getItems().get(getNowPlayPos()).getTitle());
            NotificationTarget notificationTarget = new NotificationTarget(this,
                    R.id.img_albumart,
                    mRemoteViews,
                    notification,
                    NOTIFICATION_PLAYER_ID);
            Uri albumArtUri = ContentUris.withAppendedId(artworkUri, getItems().get(getNowPlayPos()).getAlbumId());
            Glide.with(this).asBitmap().load(albumArtUri).error(R.drawable.ic_launcher_background).into(notificationTarget);
        }

            isForeground = true;
            // 서비스를 Foreground 상태로 만든다
            this.startForeground(NOTIFICATION_PLAYER_ID, notification);

    }

    private void removeNotification()
    {
        saveData();
        isNoti=false;
        isForeground=false;
        mNotificationManager.cancelAll();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.stopForeground(STOP_FOREGROUND_REMOVE);
        }
        else
        {
            this.stopForeground(true);
        }
        isPrepared=false;
        binded=false;
        stop();
        sendBroadcast(new Intent("CLOSE"));
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void isMainActivity(boolean b)
    {
        isMainactivity=b;
    }
    public boolean isBinded()
    {
        return binded;
    }
    private void saveData()
    {
//        InitDataSave data=new InitDataSave();
//        SharedPreferences pre = getApplicationContext().getSharedPreferences("savedata",Activity.MODE_PRIVATE);
//        data.setData(getItems());
//        data.setListpos(getNowPlayPos());
//        data.setDuration(mMediaPlayer.getDuration());
//        data.setSeekbarPos(mMediaPlayer.getCurrentPosition());
//
//        SharedPreferences.Editor editor = pre.edit();
//        editor.clear();
//        Gson gson = new Gson();
//        String json = gson.toJson(data);
//        editor.putString("initdata", json);
//        editor.commit();
//        Log.d("savedata","serversave");

        SharedPreferences s=this.getSharedPreferences("newsave",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = s.edit();

        edit.putInt("DUR",mMediaPlayer.getCurrentPosition());
        edit.putInt("Du", mMediaPlayer.getDuration());
        edit.commit();
    }
}

