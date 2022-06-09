package com.bubblemusic.appchee.bubblemusic;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

import com.bubblemusic.appchee.bubblemusic.DataBase.DatabaseHelper;
import com.bubblemusic.appchee.bubblemusic.DataBase.MyTopTrack;
import com.bubblemusic.appchee.bubblemusic.MusicPlaying.MusicApplication;
import com.bubblemusic.appchee.bubblemusic.MusicPlaying.PlayActivity;
import com.bubblemusic.appchee.bubblemusic.page.ClassifiyFragment;
import com.bubblemusic.appchee.bubblemusic.page.NowPlay;
import com.bubblemusic.appchee.bubblemusic.page.PageAdapter;
import com.google.gson.Gson;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity implements Sendlist, SendClassifiy {
    private boolean isPermission=true;

    private final static int LOADER_ID = 0x001;
    private HashMap<Long, MusicItem> songList = null;
    private ViewPager mViewPager;
    NowPlay np=null;

    InitDataSave data; // init data

    //Classify Songs List
    private HashMap<String, ClassifyItem> albumClassify = null;
    private HashMap<String, ClassifyItem> artistClassify = null;
    private HashMap<String, ClassifyItem> genreClassify = null;

    private HashMap<String, ClassifyItem> mylistClassify = null; //내 리스트 분류(custom,toptrack,recent)

    //genre DB set
    private ArrayList<GenreData> genredata = null;

    //list fragment
    FrameLayout fl = null;
    FrameLayout fls = null;
    FrameLayout exl=null;
    FrameLayout tmpFrame = null; //extend nowplay


    ArrayList<FrameLayout> floor = null;
    int startTabPos = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // OS가 Marshmallow 이상일 경우 권한체크를 해야 합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            } else {
                // READ_EXTERNAL_STORAGE 에 대한 권한이 있음.
                setMainList();
                setInitApp();
            }
        }
        // OS가 Marshmallow 이전일 경우 권한체크를 하지 않는다.
        else {
            setMainList();
            setInitApp();
        }
        ////////////////////////////////

    }

    private void setInitApp()
    {
        fl = (FrameLayout) findViewById(R.id.fragment_m);
        fls = (FrameLayout) findViewById(R.id.classifi);
        tmpFrame=(FrameLayout) findViewById(R.id.nowplay_big);
        floor = new ArrayList<>();
        framefloor(fl);
        ///////////////////////////////
        //fragment
        FrameLayout nowf=(FrameLayout) findViewById(R.id.fragmentNow);
        nowf.bringToFront();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.classifi, new ClassifiyFragment());
        ft.add(R.id.fragmentNow, new NowPlay());
        ft.commit();
        fls.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        musicPlayinit();
        mViewPager = (ViewPager) findViewById(R.id.container);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("My List"));
        tabLayout.addTab(tabLayout.newTab().setText("Music"));
        tabLayout.addTab(tabLayout.newTab().setText("Album"));
        tabLayout.addTab(tabLayout.newTab().setText("Aritist"));
        tabLayout.addTab(tabLayout.newTab().setText("Genre"));
        PagerAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),this);
        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.setCurrentItem(startTabPos); // 시작할 때 tab위치
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                Log.d("tabco",""+tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // READ_EXTERNAL_STORAGE 에 대한 권한 획득.
            setMainList();
            setInitApp();
        }
        else // 허가x
        {
            isPermission=false;
            super.finish();
        }
    }

    public void add_Item(long mId, long AlbumId, String Title, String Artist, String Album, Integer Duration, String DataPath, long addData) {
        MusicItem item = new MusicItem();
        item.setmId(mId);
        item.setAlbumId(AlbumId);
        item.setTitle(Title);
        item.setArtist(Artist);
        item.setAlbum(Album);
        item.setDuration(Duration);
        item.setDataPath(DataPath);
        item.setAddData(addData);
        songList.put(mId, item);
    }

    private static String[] genresProjection = {
            MediaStore.Audio.Genres.NAME,
            MediaStore.Audio.Genres._ID
    };


    private static String[] genreMembersID = {
            MediaStore.Audio.Genres.Members.AUDIO_ID
    };

    private void setMainList() {
        int count = 0;
        songList = new LinkedHashMap<>();
        ContentResolver contentResolver = getContentResolver();
        // 음악 앱의 데이터베이스에 접근해서 mp3 정보들을 가져온다.
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC";
        Cursor cursor = ((ContentResolver) contentResolver).query(uri, null, selection, null, sortOrder);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            do {
                long track_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                Integer mDuration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String datapath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                long add_date = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
                // add mainList
                add_Item(track_id, albumId, title, artist, album, mDuration, datapath, add_date);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }



    public static Comparator<MusicItem> albumSort = new Comparator<MusicItem>() {
        @Override
        public int compare(MusicItem o1, MusicItem o2) {
            return o1.getAlbum().compareToIgnoreCase(o2.getAlbum());
        }
    };

    public static Comparator<MusicItem> artistSort = new Comparator<MusicItem>() {
        @Override
        public int compare(MusicItem o1, MusicItem o2) {
            return o1.getArtist().compareToIgnoreCase(o2.getArtist());
        }
    };

    public static Comparator<MusicItem> addDateSort = new Comparator<MusicItem>() {
        @Override
        public int compare(MusicItem o1, MusicItem o2) {
            if (o1.getAddData() < o2.getAddData()) return 1;
            else if (o1.getAddData() > o2.getAddData()) return -1;
            else return 0;
        }
    };

    public void setNowPlay(MusicItem item) {
        np = (NowPlay) getSupportFragmentManager().findFragmentById(R.id.fragmentNow);
        np.setNowPlay(item);
    }

    @Override
    public HashMap<Long, MusicItem> getMusiclist() {
        return songList;
    }

    public HashMap<String, ClassifyItem> getAlbumClassify() {
        return albumClassify;
    }

    public HashMap<String, ClassifyItem> getArtistClassify() {
        return artistClassify;
    }

    public HashMap<String, ClassifyItem> getgenreClassify() {
        return genreClassify;
    }

    public HashMap<String, ClassifyItem> getMyListClassify() {
        return mylistClassify;
    }

    public void sample(ArrayList<MusicItem> data) //분류한거 버튼 눌렀을 때
    {
        framefloor(fls);
        ClassifiyFragment cla = (ClassifiyFragment) getSupportFragmentManager().findFragmentById(R.id.classifi);
        cla.listUpdate(data);
    }

    public void nowplayExtend(FrameLayout f) {
        if (tmpFrame == null) {
            tmpFrame = f;
            Log.d("floor", "nowplay click_null");
        }
        if (tmpFrame != null) {
            if (tmpFrame.getVisibility() != View.VISIBLE) {
                framefloor(tmpFrame);
                Log.d("floor", "nowplay click");
            }
        }
        //framefloor(fls);
    }

    public void myListClick(ClassifyItem data) {
        ArrayList<MusicItem> tracks = new ArrayList<>();

        framefloor(fls);

        ClassifiyFragment cla = (ClassifiyFragment) getSupportFragmentManager().findFragmentById(R.id.classifi);

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        if (data.getKey().equals("Top Tracks")) //toptrack 클릭시 업데이트 후 리스트로 들어간다
        {
            ArrayList<MyTopTrack> mytoptracks = db.getTopTrack();
            if (mytoptracks.size() > 0) //top track
            {
                for (int i = 0; i < mytoptracks.size(); i++) {
                    if (songList.containsKey(mytoptracks.get(i).getTrackID())) // 존재여부 확인
                    {
                        tracks.add(songList.get(mytoptracks.get(i).getTrackID()));
                        Log.d("testmain", "check top");
                    } else //현재 리스트에 없음 db에서 제거
                    {
                        db.removeTopItem(mytoptracks.get(i).getTrackID());
                    }
                }
            }
        } else if (data.getKey().equals("Recent Tracks")) //recent tracks 클릭시 업데이트 후 리스트로 들어간다
        {
            ArrayList<Long> recenttracks = db.getrecentTrack();
            if (recenttracks.size() > 0) {
                for (int i = 0; i < recenttracks.size(); i++) {
                    if (songList.containsKey(recenttracks.get(i))) {
                        tracks.add(songList.get(recenttracks.get(i)));
                    } else {
                        db.removeRecentItem(recenttracks.get(i));
                    }
                }
            }
        } else if (data.getKey().equals("Recent Add")) {
            tracks.addAll(data.getItem());
        }
        cla.listUpdate(tracks); //update
        db.close();
    }

    @Override
    public void onBackPressed() {
        np = (NowPlay) getSupportFragmentManager().findFragmentById(R.id.fragmentNow);
        if(/*np._isitem==true &&*/ np._isExpand) //tmp
        {
            Log.d("extend","aa");
            np.reduceAnim();
        }
        if (floor.size() > 1) {
            Log.d("floorSize", "" + floor.size());
            floor.get(floor.size() - 1).setVisibility(View.INVISIBLE);
            floor.remove(floor.size() - 1);
            floor.get(floor.size() - 1).setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    private void musicPlayinit()  //mainActivity에선 tab init만 (data는 버튼 눌릴 때, nowplay fragment는 nowplay class에서
    {
        Log.d("TestGG", "mainactivity");
        SharedPreferences pre = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pre.getString("initdata", null);
        if (json != null) {
            data = gson.fromJson(json, InitDataSave.class);
            startTabPos = data.getTabpos(); //시작 탭탭
            ArrayList<MusicItem> lastPlayedList = data.getData();
        } else {
            ArrayList<MusicItem> mdata = new ArrayList<>(songList.values());
            data = new InitDataSave(0, 0, mdata);
        }

    }

    @Override
    protected void onStart() {
        if(MusicApplication.getInstance().getServiceInterface().mService!=null)
        {
            MusicApplication.getInstance().getServiceInterface().setisMainActivity(true);
        }

        super.onStart();
    }

    protected void onStop() {
        super.onStop();
        if(isPermission==true) { //퍼미션이 허가되지 않았을때 종료는 이 부분 사용 불가
            Log.d("checkwhen","stop"); // 백그라운드로 돌릴때 언제 콜하나 확인
            if (MusicApplication.getInstance().getServiceInterface().getNowList().size() > 0) {
                data.setData(MusicApplication.getInstance().getServiceInterface().getNowList());
                data.setListpos(MusicApplication.getInstance().getServiceInterface().getNowPos());
                data.setDuration(MusicApplication.getInstance().getServiceInterface().getMediaPlayer().getDuration());
                data.setSeekbarPos(MusicApplication.getInstance().getServiceInterface().getMediaPlayer().getCurrentPosition());
            }
            data.setTabpos(mViewPager.getCurrentItem());

            Log.d("stopL", "" + data.getTabpos());
            SharedPreferences pre = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = pre.edit();
            editor.clear();
            Gson gson = new Gson();
            String json = gson.toJson(data);
            editor.putString("initdata", json);
            editor.commit();
        }
        MusicApplication.getInstance().getServiceInterface().setisMainActivity(false);
    }

    public float getWidth() //window width return
    {
        float width = getWindowManager().getDefaultDisplay().getWidth();
        return width;
    }

    @Override
    public float getHight() {
        float hight = getWindowManager().getDefaultDisplay().getHeight();
        return hight;
    }

    private void framefloor(FrameLayout visibleFrame) {
        visibleFrame.setVisibility(View.VISIBLE);
        //visibleFrame.setClickable(true);
        floor.add(visibleFrame);

        bigvrAnim(visibleFrame); // 밑에서 나오는 애니메이션

        if (floor.size() > 1) {
            Log.d("floor", ""+floor.size());
            //floor.get(floor.size() - 2).setVisibility(View.GONE);
            //floor.get(floor.size() - 2).setClickable(true);
        }
    }

    ////////////////////////////////////////////////////////Animation
    private void bigvrAnim(FrameLayout f)
    {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF,1f,
                Animation.RELATIVE_TO_SELF,0);
        translateAnimation.setDuration(260);
        f.startAnimation(translateAnimation);
    }

    @Override
    public void myListClassify() {
        if(mylistClassify==null) {
            Log.d("page","mylist null");
            ArrayList<MyTopTrack> mytoptracks;
            ArrayList<Long> recenttracks;
            ArrayList<MusicItem> recentadd = new ArrayList<>(songList.values());
            Collections.sort(recentadd, addDateSort); //최근 추가순으로 정렬

            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
            mytoptracks = db.getTopTrack();
            recenttracks = db.getrecentTrack();

            mylistClassify = new LinkedHashMap<>(); //내 리스트 미리생성

            ClassifyItem Toptmp = new ClassifyItem("Top Tracks");
            ClassifyItem Recenttmp = new ClassifyItem("Recent Tracks");
            ClassifyItem addtmp = new ClassifyItem("Recent Add");

            for (int i = 0; i < recentadd.size(); i++) //recent add
            {
                addtmp.addItem(recentadd.get(i));
            }

            mylistClassify.put("Recent Add", addtmp);
            mylistClassify.put("Top Tracks", Toptmp);
            mylistClassify.put("Recent Tracks", Recenttmp);

            if (mytoptracks.size() > 0) //top track
            {
                for (int i = 0; i < mytoptracks.size(); i++) {
                    if (songList.containsKey(mytoptracks.get(i).getTrackID())) // 존재여부 확인
                    {
                        mylistClassify.get("Top Tracks").getItem().add(songList.get(mytoptracks.get(i).getTrackID()));
                    } else //현재 리스트에 없음 db에서 제거
                    {
                        db.removeTopItem(mytoptracks.get(i).getTrackID());
                    }
                }
            }
            if (recenttracks.size() > 0) //recent play
            {
                for (int i = 0; i < recenttracks.size(); i++) {
                    if (songList.containsKey(recenttracks.get(i))) {
                        mylistClassify.get("Recent Tracks").addItem(songList.get(recenttracks.get(i)));
                    } else {
                        db.removeRecentItem(recenttracks.get(i));
                    }
                }
            }
            db.close();
        }
    }

    @Override
    public void artistClassify() {
        if(artistClassify==null)
        {
            Log.d("page","artist null");
            //Artist정렬
            artistClassify = new LinkedHashMap<>();
            ArrayList<MusicItem> tmp = new ArrayList<>(songList.values());
            Collections.sort(tmp, artistSort);
            for (int i = 0; i < tmp.size(); i++) {
                if (!artistClassify.containsKey(tmp.get(i).getArtist())) {
                    ClassifyItem classtmp = new ClassifyItem(tmp.get(i).getArtist());
                    classtmp.addItem(tmp.get(i));
                    artistClassify.put(tmp.get(i).getArtist(), classtmp);
                } else if (artistClassify.containsKey(tmp.get(i).getArtist())) {
                    artistClassify.get(tmp.get(i).getArtist()).addItem(tmp.get(i));
                }
            }
        }
    }

    @Override
    public void albumClassify() {
        if(albumClassify==null)
        {
            Log.d("page","album null");
            albumClassify = new LinkedHashMap<>();
            //앨범 정렬
            ArrayList<MusicItem> tmp = new ArrayList<>(songList.values());
            Collections.sort(tmp, albumSort);
            for (int i = 0; i < tmp.size(); i++) {
                if (!albumClassify.containsKey(tmp.get(i).getAlbum())) {
                    ClassifyItem classtmp = new ClassifyItem(tmp.get(i).getAlbum());
                    classtmp.addItem(tmp.get(i));
                    albumClassify.put(tmp.get(i).getAlbum(), classtmp);
                } else if (albumClassify.containsKey(tmp.get(i).getAlbum())) {
                    albumClassify.get(tmp.get(i).getAlbum()).addItem(tmp.get(i));
                }
            }
        }
    }

    @Override
    public void genreClassify() {
        if (genreClassify == null) {
            Log.d("page","genre null");
            int count = 0;
            genreClassify = new LinkedHashMap<>();
            HashMap<Long, MusicItem> unclassified = new LinkedHashMap<>();
            unclassified.putAll(songList);

            ContentResolver contentResolver = getContentResolver();
            Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
            String sortOrder = MediaStore.Audio.Genres.NAME + " COLLATE NOCASE ASC";
            Cursor genrecursor = ((ContentResolver) contentResolver).query(uri, genresProjection, null, null, sortOrder);
            genrecursor.moveToFirst();
            if (genrecursor != null && genrecursor.getCount() > 0) {
                do {
                    long ID = genrecursor.getLong(genrecursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID));
                    String title = genrecursor.getString(genrecursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME));
                    String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
                    ///////
                    Uri tmpuri = MediaStore.Audio.Genres.Members.getContentUri("external", ID);
                    Cursor cursortmp = ((ContentResolver) contentResolver).query(tmpuri, genreMembersID, selection, null, null);
                    cursortmp.moveToFirst();
                    if (cursortmp != null && cursortmp.getCount() > 0) {
                        ClassifyItem tmp = new ClassifyItem(title);
                        do {
                            Long ID1 = cursortmp.getLong(cursortmp.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.AUDIO_ID));
                            tmp.addItem(songList.get(ID1));
                            if (unclassified.containsKey(ID1)) {
                                unclassified.remove(ID1);
                            }
                            count++;
                        } while (cursortmp.moveToNext());
                        tmp.itemSort(); //분류한 item 타이틀로 정렬
                        genreClassify.put(title, tmp); //삽입
                    }
                    cursortmp.close();
                } while (genrecursor.moveToNext());
            }

        }
    }
}
