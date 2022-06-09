package com.bubblemusic.appchee.bubblemusic.page;


import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bubblemusic.appchee.bubblemusic.InitDataSave;
import com.bubblemusic.appchee.bubblemusic.MusicItem;
import com.bubblemusic.appchee.bubblemusic.MusicPlaying.MusicApplication;
import com.bubblemusic.appchee.bubblemusic.R;
import com.bubblemusic.appchee.bubblemusic.Sendlist;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlay extends Fragment {
    boolean receiveOrderChange=false;
    boolean receiveOrderClose=false;
    boolean isRun=false;

    final private long buttonSleepTime=700;

    BroadcastReceiver broadcastReceiver;
    float origin_pre_pos;
    float origin_play_pos;
    float origin_next_pos;
    float origin_album_pos;
    float origin_album_pos_y;
    float origin_title_pos;
    float origin_title_pos_y;

    float windowWidth;
    boolean first_ani_start=true;

    public boolean _isExpand=false;

    public boolean _isitem=false;
    private ImageButton pre;
    private ImageButton play;
    private ImageButton next;
    private ImageButton pause;
    private ImageView album;
    private TextView title;
    private FrameLayout TextContainer;
    private ViewGroup rootView;
    private View view;

    private FrameLayout bigvr;// 확장
    private TextView title_ex,artist_ex;
    private ConstraintLayout co;
    private SeekBar sb;

    MediaPlayer mp;
    Timer timer;

    ArrayList<Timer> timers;

    private MusicItem initItem;
    private InitDataSave savedData;

    Uri albumArtUri=null;

    private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
    public NowPlay() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(MusicApplication.getInstance().getServiceInterface().mService != null)
        {
            Log.d("ppap","mService not null");
            if(MusicApplication.getInstance().getServiceInterface().mService.isNoti==true)
            {
                Log.d("ppap","mService not null and isNoti");
                sb.setMax(MusicApplication.getInstance().getServiceInterface().getMediaPlayer().getDuration());
                sb.setProgress(MusicApplication.getInstance().getServiceInterface().getMediaPlayer().getCurrentPosition());
                startSeekBar();
                if(MusicApplication.getInstance().getServiceInterface().getMediaPlayer().isPlaying())
                {
                    play.setVisibility(View.INVISIBLE);
                    play.setClickable(false);

                    pause.setVisibility(View.VISIBLE);
                    pause.setClickable(true);
                }
                else if(MusicApplication.getInstance().getServiceInterface().getMediaPlayer().isPlaying()==false)
                {
                    pause.setVisibility(View.INVISIBLE);
                    pause.setClickable(false);

                    play.setVisibility(View.VISIBLE);
                    play.setClickable(true);
                }
            }
            else
            {
                SharedPreferences s=getActivity().getSharedPreferences("newsave",Context.MODE_PRIVATE);
                int dur=s.getInt("DUR",0);
                int du=s.getInt("Du",100);
                sb.setMax(du);
                sb.setProgress(dur);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("ppap","nowplay 실행");
        final Sendlist send=(Sendlist)getActivity();
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_now_play, container, false);

        bigvr=rootView.findViewById(R.id.nowplay_big);
        title_ex=bigvr.findViewById(R.id.title_ex);
        artist_ex=bigvr.findViewById(R.id.artist_ex);
        co=rootView.findViewById(R.id.textcon_ex);

        view= inflater.inflate(R.layout.fragment_now_play, null);
        pre=rootView.findViewById(R.id.pre);
        play=rootView.findViewById(R.id.play);
        pause=rootView.findViewById(R.id.pause); //pause는 play랑 위치 같음
        next=rootView.findViewById(R.id.next);
        album=rootView.findViewById(R.id.album);
        title=(TextView) rootView.findViewById(R.id._title);
        title.setText("choose your song");
        title.setSelected(true);
        TextContainer=rootView.findViewById(R.id.textcontain);
        sb=(SeekBar)rootView.findViewById(R.id.playseek);

        timer=new Timer();

        timers=new ArrayList<>();

        // Inflate the layout for this fragment
        musicPlayinit();
        if(initItem!=null) {
            title.setText(initItem.getTitle());
            albumArtUri = ContentUris.withAppendedId(artworkUri, initItem.getAlbumId());
            Glide.with(this).load(albumArtUri).override(1024,1024).error(R.drawable.ic_launcher_background).into(album);
            _isitem=true;
        }

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(MusicApplication.getInstance().getServiceInterface().getMediaPlayer() !=null && fromUser) {
                    MusicApplication.getInstance().getServiceInterface().getMediaPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        album.setOnTouchListener(new View.OnTouchListener() { //album image touch=> 확장
            @Override
            public boolean onTouch(View v, MotionEvent event) { //이미지 클릭 시 확장
                if(_isitem==true) // nowplay에 item 등록되어있음
                {
                    send.nowplayExtend(bigvr);
                    extendAnim(send);
                    bigvr.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                }
                return false;
            }
        });

        TextContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { //title 클릭시 확장
                if(_isitem==true) // nowplay에 item 등록되어있음
                {
                    send.nowplayExtend(bigvr);
                    extendAnim(send);
                    bigvr.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                }
                return false;
            }
        });

        pre.setOnClickListener(new View.OnClickListener() { //이전버튼
            @Override
            public void onClick(View v) { // 이전 버튼
                if(SystemClock.elapsedRealtime()-MusicApplication.getInstance().getServiceInterface().mService.lastclicktime<buttonSleepTime) //중복 클릭 방지
                {
                    Log.d("time",""+(SystemClock.elapsedRealtime()-MusicApplication.getInstance().getServiceInterface().mService.lastclicktime));
                    return;
                }
                MusicApplication.getInstance().getServiceInterface().mService.lastclicktime=SystemClock.elapsedRealtime();

                Log.d("Action","pre button");
                if(isListNull()==true) // service 비어있나 확인후 list삽입
                {
                    Log.d("Null","service is null");
                    if(savedData.getData().size()>0) {
                        MusicApplication.getInstance().getServiceInterface().setPlayList(savedData.getData());
                        MusicApplication.getInstance().getServiceInterface().setNowPos(savedData.getListpos());
                        if(isListNull()==false) { // 들어갔나 확인
                            MusicApplication.getInstance().getServiceInterface().rewind();
                            updateNow();
                            Log.d("Action",""+MusicApplication.getInstance().getServiceInterface().getNowPos());
                            play.setVisibility(View.INVISIBLE);
                            play.setClickable(false);

                            pause.setVisibility(View.VISIBLE);
                            pause.setClickable(true);
                        }
                    }
                }
                else
                {
                    Log.d("Null","service is not null");
                    MusicApplication.getInstance().getServiceInterface().rewind();
                    updateNow();
                    Log.d("Action",""+MusicApplication.getInstance().getServiceInterface().getNowPos());
                    play.setVisibility(View.INVISIBLE);
                    play.setClickable(false);

                    pause.setVisibility(View.VISIBLE);
                    pause.setClickable(true);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //다음버튼
                if(SystemClock.elapsedRealtime()-MusicApplication.getInstance().getServiceInterface().mService.lastclicktime<buttonSleepTime) //중복 클릭 방지
                {
                    Log.d("time",""+(SystemClock.elapsedRealtime()-MusicApplication.getInstance().getServiceInterface().mService.lastclicktime));
                    return;
                }
                MusicApplication.getInstance().getServiceInterface().mService.lastclicktime=SystemClock.elapsedRealtime();

                Log.d("Action","next button");
                if(isListNull()==true) // service 비어있나 확인후 list삽입
                {
                    Log.d("Null","service is null");
                    if(savedData.getData().size()>0) {
                        MusicApplication.getInstance().getServiceInterface().setPlayList(savedData.getData());
                        MusicApplication.getInstance().getServiceInterface().setNowPos(savedData.getListpos());
                        if(isListNull()==false) { // 들어갔나 확인
                            MusicApplication.getInstance().getServiceInterface().forward();
                            updateNow();
                            Log.d("Action",""+MusicApplication.getInstance().getServiceInterface().getNowPos());
                            play.setVisibility(View.INVISIBLE);
                            play.setClickable(false);

                            pause.setVisibility(View.VISIBLE);
                            pause.setClickable(true);
                        }
                    }
                }
                else
                {
                    Log.d("Null","service is not null");
                    MusicApplication.getInstance().getServiceInterface().forward();
                    updateNow();
                    Log.d("Action",""+MusicApplication.getInstance().getServiceInterface().getNowPos());
                    play.setVisibility(View.INVISIBLE);
                    play.setClickable(false);

                    pause.setVisibility(View.VISIBLE);
                    pause.setClickable(true);
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //시작버튼
                if(SystemClock.elapsedRealtime()-MusicApplication.getInstance().getServiceInterface().mService.lastclicktime<buttonSleepTime) //중복 클릭 방지
                {
                    Log.d("time",""+(SystemClock.elapsedRealtime()-MusicApplication.getInstance().getServiceInterface().mService.lastclicktime));
                    return;
                }
                MusicApplication.getInstance().getServiceInterface().mService.lastclicktime=SystemClock.elapsedRealtime();

                Log.d("Action","play button");
                if(isListNull()==true) // service 비어있나 확인후 list삽입
                {
                    Log.d("Action","시작버튼 == true");
                    if(savedData.getData().size()>0) {
                        MusicApplication.getInstance().getServiceInterface().setPlayList(savedData.getData());
                        MusicApplication.getInstance().getServiceInterface().setNowPos(savedData.getListpos());
                        if(isListNull()==false) { // 들어갔나 확인
                            MusicApplication.getInstance().getServiceInterface().play(savedData.getListpos());
                            Log.d("Action","play btton up + "+MusicApplication.getInstance().getServiceInterface().mService.isNoti);
                            if(MusicApplication.getInstance().getServiceInterface().mService.isNoti ==false || isRun ==false)
                            {
                                Log.d("Action","play btton up");
                                startSeekBar();
                            }
                            MusicApplication.getInstance().getServiceInterface().getMediaPlayer().seekTo(sb.getProgress());
                            Log.d("Action",""+MusicApplication.getInstance().getServiceInterface().getNowPos());
                            play.setVisibility(View.INVISIBLE);
                            play.setClickable(false);

                            pause.setVisibility(View.VISIBLE);
                            pause.setClickable(true);
                        }
                    }
                }
                else
                {
                    Log.d("Action","시작버튼 == false");
                    Log.d("Null","service is not null");
                    MusicApplication.getInstance().getServiceInterface().play();
                    Log.d("Action","play btton down + "+MusicApplication.getInstance().getServiceInterface().mService.isNoti);
                    if(MusicApplication.getInstance().getServiceInterface().mService.isNoti ==false || isRun==false)
                    {
                        Log.d("Action","play btton down");
                        startSeekBar();
                    }
                    MusicApplication.getInstance().getServiceInterface().getMediaPlayer().seekTo(sb.getProgress());
                    Log.d("Action",""+MusicApplication.getInstance().getServiceInterface().getNowPos());
                    play.setVisibility(View.INVISIBLE);
                    play.setClickable(false);

                    pause.setVisibility(View.VISIBLE);
                    pause.setClickable(true);
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //멈춤버튼
                if(SystemClock.elapsedRealtime()-MusicApplication.getInstance().getServiceInterface().mService.lastclicktime<buttonSleepTime) //중복 클릭 방지
                {
                    Log.d("timetime",""+(SystemClock.elapsedRealtime()-MusicApplication.getInstance().getServiceInterface().mService.lastclicktime));
                    return;
                }
                MusicApplication.getInstance().getServiceInterface().mService.lastclicktime=SystemClock.elapsedRealtime();

                Log.d("TestGG","pause button");
                if(isListNull()==true) // service 비어있나 확인후 list삽입
                {
                    Log.d("TestGG","service is null");
                    if(savedData.getData().size()>0) {
                        MusicApplication.getInstance().getServiceInterface().setPlayList(savedData.getData());
                        MusicApplication.getInstance().getServiceInterface().setNowPos(savedData.getListpos());
                        if(isListNull()==false) { // 들어갔나 확인
                            MusicApplication.getInstance().getServiceInterface().pause();
                            Log.d("TestGG",""+MusicApplication.getInstance().getServiceInterface().getNowPos());
                            pause.setVisibility(View.INVISIBLE);
                            pause.setClickable(false);

                            play.setVisibility(View.VISIBLE);
                            play.setClickable(true);
                        }
                    }
                }
                else
                {
                    Log.d("TestGG","service is not null");
                    MusicApplication.getInstance().getServiceInterface().pause();
                    Log.d("TestGG",""+MusicApplication.getInstance().getServiceInterface().getNowPos());
                    pause.setVisibility(View.INVISIBLE);
                    pause.setClickable(false);

                    play.setVisibility(View.VISIBLE);
                    play.setClickable(true);
                }
            }
        });

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("PLAY");
        intentFilter.addAction("CLOSE");
        intentFilter.addAction("REWIND");
        intentFilter.addAction("FORWARD");
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("PLAY"))
                {
                    Log.d("broadcassss","play");
                }
                else if(intent.getAction().equals("CLOSE"))
                {
                    Log.d("broadcassss","close");
                }
                else if(intent.getAction().equals("REWIND"))
                {
                    receiveOrderChange=true; //음악바뀐거 전달
                    sb.setProgress(0);
                    sb.setMax(MusicApplication.getInstance().getServiceInterface().getMediaPlayer().getDuration());

                    Log.d("broadcassss","rewind");
                }

                else if(intent.getAction().equals("FORWARD"))
                {
                    receiveOrderChange=true;
                    sb.setProgress(0);
                    sb.setMax(MusicApplication.getInstance().getServiceInterface().getMediaPlayer().getDuration());
                    Log.d("broadcassss","forward");
                }
            }
        };
        getActivity().registerReceiver(broadcastReceiver,intentFilter);
        return rootView;
    }

    public void setNowPlay(MusicItem item) // 리스트에서 누를 때
    {
        Log.d("nextProcess","set Nowplay");
        title.setText(item.getTitle());
        albumArtUri = ContentUris.withAppendedId(artworkUri, item.getAlbumId());
        Glide.with(this).load(albumArtUri).override(1024,1024).error(R.drawable.ic_launcher_background).into(album);
        _isitem=true;

        play.setVisibility(View.INVISIBLE);
        play.setClickable(false);

        pause.setVisibility(View.VISIBLE);
        pause.setClickable(true);

        if(MusicApplication.getInstance().getServiceInterface().mService.isNoti == false || isRun==false)
        {
            sb.setProgress(0);
            sb.setMax(MusicApplication.getInstance().getServiceInterface().getMediaPlayer().getDuration());

            startSeekBar();
        }
        else
        {
            sb.setProgress(0);
            sb.setMax(MusicApplication.getInstance().getServiceInterface().getMediaPlayer().getDuration());
        }
    }
    public void test(String s)
    {
        title.setText(s);
    }

    private void musicPlayinit()
    {
        Log.d("TestGG","NowPLAY class");
        SharedPreferences pre=getActivity().getPreferences(Context.MODE_PRIVATE);
        Gson gson=new Gson();
        String json=pre.getString("initdata",null);
        if(json!=null)
        {
            savedData=gson.fromJson(json,InitDataSave.class);
            if(savedData.getData().size()>0)
            {
                initItem=savedData.getData().get(savedData.getListpos());
            }
        }
        else
        {
            savedData=new InitDataSave();
        }
        SharedPreferences s=getActivity().getSharedPreferences("newsave",Context.MODE_PRIVATE);
        int dur=s.getInt("DUR",0);
        int du=s.getInt("Du",100);
        sb.setMax(du);
        sb.setProgress(dur);
    }

    private boolean isListNull()
    {
        if(MusicApplication.getInstance().getServiceInterface().getNowList().isEmpty()) return true;
        else return false;
    }

    private void updateNow() // 이전, 다음 버튼 누를 때
    {
        MusicItem item=MusicApplication.getInstance().getServiceInterface().nowplay();
        title.setText(item.getTitle());
        albumArtUri = ContentUris.withAppendedId(artworkUri, item.getAlbumId());
        Glide.with(this).load(albumArtUri).override(1024,1024).error(R.drawable.ic_launcher_background).into(album);
        final MediaPlayer tmp=MusicApplication.getInstance().getServiceInterface().getMediaPlayer();

        if(MusicApplication.getInstance().getServiceInterface().mService.isNoti == false || isRun==false) {
            sb.setProgress(0);
            sb.setMax(MusicApplication.getInstance().getServiceInterface().getMediaPlayer().getDuration());

            startSeekBar();
        }
    }

    private void extendAnim(Sendlist send)
    {
        if(first_ani_start==true) //처음에만 저장
        {
            origin_next_pos=next.getX();// 원래위치 저장
            origin_pre_pos=pre.getX();// 원래위치 저장
            origin_play_pos=play.getX();// 원래위치 저장
            origin_album_pos=album.getX();
            origin_album_pos_y=album.getY();
            origin_title_pos=TextContainer.getX();
            origin_title_pos_y=TextContainer.getY();
            first_ani_start=false;
        }

        if(_isExpand == false) { // 확대\
            co.setY(send.getHight()-send.getWidth()+120);
            windowWidth=send.getWidth(); //getwidth
            play.animate().x((windowWidth*0.5f)-(play.getWidth()*0.5f));//왼쪽 이동
            play.animate().translationYBy(-50f);//위로 올라가는거
            play.animate().scaleXBy(0.3f); //크기
            play.animate().scaleYBy(0.3f);
            pause.animate().x((windowWidth*0.5f)-(play.getWidth()*0.5f));//왼쪽 이동
            pause.animate().translationYBy(-50f);//위로 올라가는거
            pause.animate().scaleXBy(0.3f); //크기
            pause.animate().scaleYBy(0.3f);

            pre.animate().x((windowWidth * 0.3f) - (pre.getWidth() * 0.5f));
            pre.animate().translationYBy(-50f);
            pre.animate().scaleXBy(0.2f);
            pre.animate().scaleYBy(0.2f);

            next.animate().x((windowWidth * 0.7f) - (next.getWidth() * 0.5f));
            next.animate().translationYBy(-50f);
            next.animate().scaleXBy(0.2f);
            next.animate().scaleYBy(0.2f);

            //앨범 아트 확대
            float nAlbumsize=(windowWidth/album.getWidth())*0.9f; //실제크기에서의 퍼센트 => 기존*이거=확대 크기
            //float albumHight=send.getHight()*((album.getWidth()*0.5f)*nAlbumsize)/send.getHight();

            album.animate().scaleX(nAlbumsize); //크기 조절
            album.animate().scaleY(nAlbumsize);
            album.animate().x((windowWidth*0.5f)-(album.getWidth()*0.5f));//가운데로 이동
            //album.animate().y(albumHight); //높이 조절
            album.animate().y(album.getWidth()*nAlbumsize*0.5f); //높이 조절

            title_ex.setText(title.getText());
            TextContainer.animate().alpha(0).setDuration(200);

            AlphaAnimation ap=new AlphaAnimation(0,1);
            ap.setDuration(500);
            co.setAnimation(ap);

            Log.d("albumsize",""+((album.getWidth()*0.5f)*nAlbumsize)/send.getHight());
        }
        _isExpand=true;
    }

    public void reduceAnim()
    {
        _isExpand=false;
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,1);
        translateAnimation.setDuration(260);
        bigvr.startAnimation(translateAnimation); //background 사라짐

        play.animate().x(origin_play_pos);
        play.animate().translationYBy(50f);
        play.animate().scaleXBy(-0.3f);
        play.animate().scaleYBy(-0.3f);
        pause.animate().x(origin_play_pos);
        pause.animate().translationYBy(50f);
        pause.animate().scaleXBy(-0.3f);
        pause.animate().scaleYBy(-0.3f);

        pre.animate().x(origin_pre_pos);
        pre.animate().translationYBy(50f);
        pre.animate().scaleXBy(-0.2f);
        pre.animate().scaleYBy(-0.2f);

        next.animate().x(origin_next_pos);
        next.animate().translationYBy(50f);
        next.animate().scaleXBy(-0.2f);
        next.animate().scaleYBy(-0.2f);

        //앨범
        album.animate().scaleX(1f); //크기 조절
        album.animate().scaleY(1f);
        album.animate().x(origin_album_pos);
        album.animate().y(origin_album_pos_y);

        //title
        TextContainer.animate().alpha(1f).setDuration(100);
    }

    private void startSeekBar() // Thread
    {
        isRun=true;
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int cc=0;
            @Override
            public void run() {
                if(MusicApplication.getInstance().getServiceInterface().mService.isMainactivity==false)
                {
                    isRun=false;
                    Log.d("Thread","activity cut");
                    timer.cancel(); // activity종료하면 죽임
                }
                else {
                    if(MusicApplication.getInstance().getServiceInterface().mService.isNoti==false)
                    {
                        Log.d("Thread","noti cut");
                        isRun=false;
                        timer.cancel();
                    }
                    else if(MusicApplication.getInstance().getServiceInterface().mService.isNoti==true)
                    {
                        sb.setProgress(MusicApplication.getInstance().getServiceInterface().getMediaPlayer().getCurrentPosition());
                        Log.d("Thread", "" + (cc++));
                    }
                }
            }
        },0,1000);
    }
}
