package com.bubblemusic.appchee.bubblemusic.DataBase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.util.Log;

import com.bubblemusic.appchee.bubblemusic.MusicItem;

import java.util.ArrayList;
import java.util.Collections;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "subData";
    private static final int DATABASE_VERSION = 1;
    private static final String TOP_TABLE = "toptrack_table";
    private static final String TAG_TABLE = "tag_table";
    private static final String RECENT_TABLE="recent_table";
    private static final String KEY_ID = "id";
    private static final String KEY_TAG = "tag";
    private static final String KEY_COUNT = "count";

    ArrayList<MyTopTrack> topTrack=null;
    ArrayList<Long> recentTrack=null;

    private static final String CREATE_COUNT_TABLE = "CREATE TABLE " + TOP_TABLE + "(" + KEY_ID + " INTEGER," + KEY_COUNT + " INTEGER );"; //count table 생성
    private static final String CREATE_TAG_TABLE = "CREATE TABLE " + TAG_TABLE + "(" + KEY_ID + " INTEGER,"+ KEY_TAG + " TEXT );"; //tag table 생성
    private static final String CREATE_RECENT_TABLE= "CREATE TABLE "+RECENT_TABLE + "("+KEY_ID+ " INTEGER );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("dataaaa","check oncreate");
        db.execSQL(CREATE_COUNT_TABLE);
        db.execSQL(CREATE_TAG_TABLE);
        db.execSQL(CREATE_RECENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("dataaaa","check upgrade");
        db.execSQL("DROP TABLE IF EXISTS '" + TOP_TABLE + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TAG_TABLE + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + RECENT_TABLE + "'");
        onCreate(db);
    }

    public void initTopTable(ArrayList<MusicItem> item)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i=0;i<item.size();i++)
        {
            String query="INSERT INTO "+TOP_TABLE+" values("+item.get(i).getmId()+", "+0+");";
            db.execSQL(query);
        }
        db.close();
    }

    public ArrayList<MyTopTrack> getTopTrack() { //많이듣는 음악 List
        SQLiteDatabase db = this.getReadableDatabase();
        topTrack=new ArrayList<>();
        String sort="count desc";
        Cursor  cursor = db.rawQuery("select * from "+TOP_TABLE+" order by "+KEY_COUNT+" DESC",null);
        if(cursor.moveToFirst())
        {
            if (cursor != null && cursor.getCount() > 0) {
                do {
                    Long ID=cursor.getLong(cursor.getColumnIndex(KEY_ID));
                    int count=cursor.getInt(cursor.getColumnIndex(KEY_COUNT));
                    topTrack.add(new MyTopTrack(ID,count));
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return topTrack;
    }

    public ArrayList<Long> getrecentTrack() { //recent track
        SQLiteDatabase db = this.getReadableDatabase();
        recentTrack=new ArrayList<>();
        Cursor  cursor = db.rawQuery("select * from "+RECENT_TABLE,null);
        if(cursor.moveToFirst())
        {
            if (cursor != null && cursor.getCount() > 0) {
                do {
                    Long ID=cursor.getLong(cursor.getColumnIndex(KEY_ID));
                    recentTrack.add(ID);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        Collections.reverse(recentTrack); //역순이 최근
        return recentTrack;
    }

    public void updateRecentTable(Long id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String qur="select * from " + RECENT_TABLE + " where "+KEY_ID+"="+id+";";
        Cursor c=db.rawQuery(qur,null);
        if(c.getCount()>0) //이미 table에 존재
        {
            String q="delete from " + RECENT_TABLE + " where "+KEY_ID+"="+id+";";
            db.execSQL(q);
            q="insert into "+RECENT_TABLE+" values("+id+");";
            db.execSQL(q);
        }
        else if(c.getCount()==0)
        {
            String q="insert into "+RECENT_TABLE+" values("+id+");";
            db.execSQL(q);
        }
        db.close();
    }

    public void updateTopTrack(Long trackID) //음악 끝날때 들었는지 판단 후 top track에 insert or update
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String qur="select * from " + TOP_TABLE + " where "+KEY_ID+"="+trackID+";";
        Cursor c=db.rawQuery(qur,null);
       if(c.getCount()==0) //data없음
       {
           String q="insert into "+TOP_TABLE+" values("+trackID+","+1+");";
           db.execSQL(q);
           Log.d("databaseCheck","first Insert");
       }
       else
       {
           int count=1;
           if(c.moveToFirst())
           {
               if (c != null && c.getCount() == 1) {
                   do {
                       Log.d("testcheck",""+c.getCount());
                       count=c.getInt(c.getColumnIndex(KEY_COUNT));
                       count++;
                   } while (c.moveToNext());
               }
           }

           qur="update "+TOP_TABLE+" set "+KEY_COUNT+ "="+count+" where "+KEY_ID+"="+trackID+";";
           db.execSQL(qur);
           Log.d("databaseCheck",""+count);
       }
        db.close();
    }

    public void insertTopTrack(Long trackID) //첫 play 시
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String qur="insert into "+TOP_TABLE+" values("+trackID+","+1+");";
        db.execSQL(qur);
        db.close();
    }



    public int CountTableCount() //Top Table 행 갯수
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = KEY_ID+" = ? AND "+KEY_COUNT+" = ?";
        String tableName = TOP_TABLE;
        Cursor c = db.query(tableName, null, selection, null, null, null, null);
        int result = c.getCount();
        c.close();
        return result;
    }

    public void TestInsert() //Test용
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String insert="insert into "+TOP_TABLE+" values("+12333+","+10+");";
        String insert1="insert into "+TOP_TABLE+" values("+12334+","+1+");";
        String insert2="insert into "+TOP_TABLE+" values("+12339+","+18+");";
        db.execSQL(insert);
        db.execSQL(insert1);
        db.execSQL(insert2);
        db.close();
    }

    public void removeTopTableAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+TOP_TABLE);
        db.close();
    }

    public void removeTopItem(Long id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String q="delete from " + TOP_TABLE + " where "+KEY_ID+"="+id+";";
        db.execSQL(q);
        db.close();
    }

    public void removeRecentItem(Long id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String q="delete from " + RECENT_TABLE + " where "+KEY_ID+"="+id+";";
        db.execSQL(q);
        db.close();
    }
}