package com.example.xd.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xd on 17-7-31.
 */

public class DBHelper extends SQLiteOpenHelper {

    public int id;
    public String title;
    public String name;
    public String path;
    public int duration;
    public String albums;
    public String artist;
    public long size;

    public static final String DB_NAME = "music_db";
    public static final String TABLE_MUSIC = "music";
    public static final String TABLE_LIKE = "music_like";
    public static final String TABLE_RECORD = "music_record";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table "+TABLE_MUSIC+" ( " +
                "id integer primary key," +
                "music_title varchar(60)," +
                "music_name varchar(40)," +
                "music_path varchar(200)," +
                "duration INTEGER," +
                "albums varchar(40)," +
                "album_id integer," +
                "artist varchar(40)," +
                "size integer)");

        db.execSQL("create table "+TABLE_LIKE+" ( " +
                "id integer primary key autoincrement," +
                "music_id integer)");

        db.execSQL("create table "+TABLE_RECORD+" (" +
                "id integer primary key autoincrement," +
                "music_id integer," +
                "start_time integer," +
                "end_time integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
