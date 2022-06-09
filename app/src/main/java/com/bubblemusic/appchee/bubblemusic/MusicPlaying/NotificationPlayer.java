package com.bubblemusic.appchee.bubblemusic.MusicPlaying;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bubblemusic.appchee.bubblemusic.MainActivity;
import com.bubblemusic.appchee.bubblemusic.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

import java.util.concurrent.ExecutionException;

public class NotificationPlayer {
    private final static String NOTIFICATION_PLAYER_ID = "BubbleMusic";
    private MusicService mService;
    private NotificationManager mNotificationManager;
    private NotificationManagerBuilder mNotificationManagerBuilder;
    private boolean isForeground;
    private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");

    public NotificationPlayer(MusicService service) {
        mService = service;
        mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void updateNotificationPlayer() {
        cancel();
        mNotificationManagerBuilder = new NotificationManagerBuilder();
        mNotificationManagerBuilder.execute();
    }

    public void removeNotificationPlayer() {
        cancel();
        mService.stopForeground(true);
        isForeground = false;
    }

    private void cancel() {
        if (mNotificationManagerBuilder != null) {
            mNotificationManagerBuilder.cancel(true);
            mNotificationManagerBuilder = null;
        }
    }

    private class NotificationManagerBuilder extends AsyncTask<Void, Void, Notification> {
        private RemoteViews mRemoteViews;
        private NotificationCompat.Builder mNotificationBuilder;
        private PendingIntent mMainPendingIntent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(NOTIFICATION_PLAYER_ID, "Music", NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(channel);
            }
                Intent mainActivity = new Intent(mService, MainActivity.class);
                mMainPendingIntent = PendingIntent.getActivity(mService, 0, mainActivity, 0);
                mRemoteViews = createRemoteView(R.layout.notification_player);
                mNotificationBuilder = new NotificationCompat.Builder(mService,NOTIFICATION_PLAYER_ID);
                mNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                        .setOngoing(true)
                        .setContentIntent(mMainPendingIntent)
                        .setContent(mRemoteViews);

                Notification notification = mNotificationBuilder.build();
                notification.contentIntent = mMainPendingIntent;
                if (!isForeground) {
                    isForeground = true;
                    // 서비스를 Foreground 상태로 만든다
                    mService.startForeground(2, notification);
                }
        }



        @Override
        protected Notification doInBackground(Void... params) {
            mNotificationBuilder.setContent(mRemoteViews);
            mNotificationBuilder.setContentIntent(mMainPendingIntent);
            mNotificationBuilder.setPriority(Notification.PRIORITY_MAX);
            Notification notification = mNotificationBuilder.build();
            updateRemoteView(mRemoteViews, notification);
            return notification;
        }

        @Override
        protected void onPostExecute(Notification notification) {
            super.onPostExecute(notification);
            try {
                mNotificationManager.notify(2, notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private RemoteViews createRemoteView(int layoutId) {
            Log.d("remoteview","true");
            RemoteViews remoteView = new RemoteViews(mService.getPackageName(), layoutId);
            Intent actionTogglePlay = new Intent("PLAY");
            Intent actionForward = new Intent("FORWARD");
            Intent actionRewind = new Intent("REWIND");
            Intent actionClose = new Intent("CLOSE");
            PendingIntent togglePlay = PendingIntent.getService(mService, 0, actionTogglePlay, 0);
            PendingIntent forward = PendingIntent.getService(mService, 0, actionForward, 0);
            PendingIntent rewind = PendingIntent.getService(mService, 0, actionRewind, 0);
            PendingIntent close = PendingIntent.getService(mService, 0, actionClose, 0);

            remoteView.setOnClickPendingIntent(R.id.btn_play_pause, togglePlay);
            remoteView.setOnClickPendingIntent(R.id.btn_forward, forward);
            remoteView.setOnClickPendingIntent(R.id.btn_rewind, rewind);
            remoteView.setOnClickPendingIntent(R.id.btn_close, close);
            return remoteView;
        }

        private void updateRemoteView(RemoteViews remoteViews, Notification notification) {
////            if (mService.isPlaying()) {
////                remoteViews.setImageViewResource(R.id.btn_play_pause,);
////            } else {
////                remoteViews.setImageViewResource(R.id.btn_play_pause, R.drawable.play); 나중에
            NotificationTarget notificationTarget = new NotificationTarget(mService,
                    R.id.img_albumart,
                    remoteViews,
                    notification,
                    2);

            String title = mService.getnowPlay().getTitle();
            remoteViews.setTextViewText(R.id.txt_title, title);
            Uri albumArtUri = ContentUris.withAppendedId(artworkUri, mService.getnowPlay().getAlbumId());
            Glide.with(mService).asBitmap().load(albumArtUri).error(R.drawable.ic_launcher_background).into(notificationTarget);

        }
    }
}
