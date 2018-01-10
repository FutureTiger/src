package com.example.xd.myapplication.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.xd.myapplication.models.Music;
import com.example.xd.myapplication.models.MusicRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xd on 17-7-31.
 */

public class DBCommand {

    private static DBCommand instance = null;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public static DBCommand getInstance(Context context){

        if(instance == null){
            synchronized (DBCommand.class){
                if(instance == null){
                    instance = new DBCommand(context);
                }
            }
        }
        return instance;
    }

    private DBCommand(Context context){
        dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
    }

    public Music queryById(int id){
        Cursor cursor = null;
        if(db.isOpen()){
            Music music = new Music();
            try{
                cursor =  db.rawQuery("select * from "+dbHelper.TABLE_MUSIC+" where id=?",new String[]{""+id});
                while (cursor.moveToNext()){
                    music.id = cursor.getInt(cursor.getColumnIndex("id"));
                    music.name = cursor.getString(cursor.getColumnIndex("music_name"));
                    music.title = cursor.getString(cursor.getColumnIndex("music_title"));
                    music.path =cursor.getColumnName(cursor.getColumnIndex("music_path"));
                    music.duration = cursor.getInt(cursor.getColumnIndex("duration"));
                    music.albums = cursor.getString(cursor.getColumnIndex("albums"));
                    music.albumId = cursor.getInt(cursor.getColumnIndex("album_id"));
                    music.artist = cursor.getString(cursor.getColumnIndex("artist"));
                    music.size = cursor.getLong(cursor.getColumnIndex("size"));
                }
            }catch (Exception e){
                e.printStackTrace();;
            }finally {
                if(cursor != null){
                    cursor.close();
                }
            }


            return  music;
        }
        return null;

    }

    public List<Music> queryAll(){
        List<Music> list = new ArrayList<Music>();

        if(db.isOpen()){
            Cursor cursor = null;
            try{
               cursor =  db.rawQuery("select * from "+dbHelper.TABLE_MUSIC,null);
                while (cursor.moveToNext()){
                    Music music = new Music();
                    music.id = cursor.getInt(cursor.getColumnIndex("id"));
                    music.name = cursor.getString(cursor.getColumnIndex("music_name"));
                    music.title = cursor.getString(cursor.getColumnIndex("music_title"));
                    music.path =cursor.getColumnName(cursor.getColumnIndex("music_path"));
                    music.duration = cursor.getInt(cursor.getColumnIndex("duration"));
                    music.albums = cursor.getString(cursor.getColumnIndex("albums"));
                    music.albumId = cursor.getInt(cursor.getColumnIndex("album_id"));
                    music.artist = cursor.getString(cursor.getColumnIndex("artist"));
                    music.size = cursor.getLong(cursor.getColumnIndex("size"));
                    list.add(music);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(cursor != null){
                    cursor.close();
                }

            }
        }

        return  list;
    }

    public void insertMusic(Music music){
        if(db.isOpen()){
            db.execSQL("insert into music (id,music_title,music_name,music_path," +
                    "duration,albums,album_id,artist,size) values(?,?,?,?,?,?,?,?,?)",new Object[]{music.id,
            music.title,music.name,music.path,music.duration,music.albums,music.albumId,music.artist,music.size});
        }


    }

    public void insertMusicList(List<Music> list){
        if(list!= null && list.size()>0){
            for(Music music : list){
                insertMusic(music);
            }
        }
    }

    public void deleteMusic(Music music){
        if(db.isOpen()){
            db.execSQL("delete from music where id=?",new Object[]{music.id});
        }

    }

    public void updateMusic(Music music){
        if(db.isOpen()){
            db.execSQL("update music set music_title=?,music_name=?,music_path=?,duration=?," +
                    "albums=?,album_id,artist=?,size=? where id=?",new Object[]{music.title,music.name,
                     music.path,music.title,music.duration,music.albums,music.albumId,music.artist,music.size,music.id});
        }
    }

    public List<Music> queryLike(){
        List<Music> list = new ArrayList<Music>();
        if(db.isOpen()){
            Cursor cursor = null;
            try{
                cursor =  db.rawQuery("select * from  music,music_like " +
                        "where music.id = music_like.music_id",null);

                while (cursor.moveToNext()){
                    Music music = new Music();
                    music.id = cursor.getInt(cursor.getColumnIndex("id"));
                    music.name = cursor.getString(cursor.getColumnIndex("music_name"));
                    music.title = cursor.getString(cursor.getColumnIndex("music_title"));
                    music.path =cursor.getColumnName(cursor.getColumnIndex("music_path"));
                    music.duration = cursor.getInt(cursor.getColumnIndex("duration"));
                    music.albums = cursor.getString(cursor.getColumnIndex("albums"));
                    music.albumId = cursor.getInt(cursor.getColumnIndex("album_id"));
                    music.artist = cursor.getString(cursor.getColumnIndex("artist"));
                    music.size = cursor.getLong(cursor.getColumnIndex("size"));
                    list.add(music);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(cursor != null){
                    cursor.close();
                }
            }



        }
        return list;
    }

    public void insertMusicLike(Music music){

        if(db.isOpen()){
            db.execSQL("insert into music_like(music_id) values(?)",new Object[]{music.id});
        }
    }

    public void deleteMusicLike(Music music){
        if(db.isOpen()){
            db.execSQL("delete from music_like where music_id=?",new Object[]{music.id});
        }
    }

    public void updateMusicLike(Music music){

    }

    public List<MusicRecord> queryRecordAll(){
        List<MusicRecord> list = new ArrayList<MusicRecord>();
        if(db.isOpen()){
            Cursor cursor = null;
            try{
                cursor =  db.rawQuery("select * from  music,music_record " +
                        "where music.id = music_record.music_id",null);

                while (cursor.moveToNext()){
                    MusicRecord musicRecord = new MusicRecord();
                    musicRecord.startTime = cursor.getLong(cursor.getColumnIndex("start_time"));
                    musicRecord.endTime = cursor.getLong(cursor.getColumnIndex("end_time"));
                    Music music = new Music();
                    music.id = cursor.getInt(cursor.getColumnIndex("id"));
                    music.name = cursor.getString(cursor.getColumnIndex("music_name"));
                    music.title = cursor.getString(cursor.getColumnIndex("music_title"));
                    music.path =cursor.getColumnName(cursor.getColumnIndex("music_path"));
                    music.duration = cursor.getInt(cursor.getColumnIndex("duration"));
                    music.albums = cursor.getString(cursor.getColumnIndex("albums"));
                    music.albumId = cursor.getInt(cursor.getColumnIndex("album_id"));
                    music.artist = cursor.getString(cursor.getColumnIndex("artist"));
                    music.size = cursor.getLong(cursor.getColumnIndex("size"));
                    musicRecord.music = music;
                    list.add(musicRecord);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (cursor != null){
                    cursor.close();
                }
            }


        }

        return  list;
    }

    public void insertRecord(MusicRecord record){
        if(db.isOpen()){
            db.execSQL("insert into music_record(music_id,start_time,end_time) values(?,?,?)",
                    new Object[]{record.music.id,record.startTime,record.endTime});
        }
    }

    public void deleteRecord(){
        db.execSQL("delete * from record");
    }

    public void updateRecord(Music music){

    }

    public void closeDb(){
        if(db !=null){
            db.close();
            db = null;
        }
        if(dbHelper != null){
            dbHelper.close();
            dbHelper = null;
        }
        if(instance != null){
            instance = null;
        }

    }

}
