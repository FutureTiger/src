package com.example.xd.myapplication.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.xd.myapplication.database.DBCommand;
import com.example.xd.myapplication.models.Music;
import com.example.xd.myapplication.models.MusicRecord;
import com.example.xd.myapplication.models.OnStatusChangeListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by xd on 17-7-26.
 */

public class MusicPlayer {

    private static MusicPlayer instance;
    private static final int DEFAULT_POSITION = 0;


    public static final int PLAY = 0;
    public static final int STOP = 1;
    public static final int PAUSE = 2;
    public static final int FINISH = 3;
    public static final int INIT = -1;
    public static final String TAG = MusicPlayer.class.getName();

    private MediaPlayer player;
    private List<Music> musicList = new ArrayList<Music>();
    private List<Music> oldList = new ArrayList<Music>();
    private int musicPosition = -1;
    private int status = INIT;
    private Music currentMusic;
    private AudioManager audioManager;
    private OnStatusChangeListener statusChangeListener;
    private Context context;
    private MusicRecord record;
    private DBCommand dbCommand;
    private int stramVolum = -1;
    private int requestResult;

    public static MusicPlayer getInstance(Context context){
       if(instance == null){
           synchronized (MusicPlayer.class){
               if(instance == null){
                   instance = new MusicPlayer(context);
               }
           }
       }
        return instance;
    }

    private MusicPlayer(Context context){
        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        this.context = context;
        dbCommand = DBCommand.getInstance(context);
        player = new MediaPlayer();
        player.setOnCompletionListener(listener);
    }

    public void setOnStatusChangeListenler(OnStatusChangeListener listenler){
        statusChangeListener = listenler;
    }


    public int getStatus(){
        return  status;
    }

    public Music getCurrentMusic(){
        if(currentMusic == null && musicList.size() > 0){
            currentMusic = musicList.get(musicPosition == -1?DEFAULT_POSITION:musicPosition);
        }
        return currentMusic;
    }

    public int getCurrentDuration(){
        if(player != null){
            return player.getCurrentPosition();
        }
        return 0;
    }

    public int getTotaleDuration(){
        if(currentMusic == null){
            return -1;
        }
        return currentMusic.duration;
    }

    public void setCurrentDuration(int position){
        if(player !=null ){
            player.seekTo(getTotaleDuration()*position/100);
        }
    }

    public synchronized void setMusicList(List<Music> list){
        Music oldMusic = getCurrentMusic();
        musicList = list;
        if(oldMusic != null){
            musicPosition = musicList.indexOf(oldMusic);
        }
        if(oldList.size() == 0){
            oldList = dbCommand.queryAll();
        }

        /*for(Music inListMusic : list){
            int i = 0;
            boolean isRemove = false;
            for(Music inOldMusic : oldList){
                if(inListMusic.id == inOldMusic.id){
                    isRemove = true;
                    oldList.remove(inOldMusic);
                    break;
                }
            }
            if(!isRemove && i == oldList.size()){
                dbCommand.insertMusic(inListMusic);
            }
        }*///会爆出ConcurrentModificationException错误，需要用Iterator迭代器删除
        Iterator<Music> inList = list.iterator();
        Iterator<Music> inOld = oldList.iterator();
        while (inList.hasNext()){
            int i = 0;
            boolean isRemove = false;
            Music fresh = inList.next();
            while (inOld.hasNext()){
                i++;
                Music old = inOld.next();
                if(fresh.id == old.id){
                    inOld.remove();
                    isRemove = true;
                    break;
                }
            }
            if(!isRemove && i == oldList.size()){
                dbCommand.insertMusic(fresh);
            }
        }

        /*for (int i=0;i<list.size();i++){
            int j=0;
            boolean isRemove=false;
            for(;j<oldList.size();j++){
                if(list.get(i).id == oldList.get(j).id){
                    oldList.remove(j);
                    isRemove = true;
                    break;
                }
            }
            if(oldList.size()>0&&!isRemove&& j==oldList.size()){
                dbCommand.insertMusic(oldList.get(j));
            }
        }*/

        Log.i("VIND","finish update database");
        for (Music remove : oldList){
            dbCommand.deleteMusic(remove);
        }

        oldList = musicList;
    }

    public void release(){
        if(player != null){
            player.release();
            player = null;
        }
    }

    public void start(){
        if(player != null) {
            if (musicPosition != -1) {
                if(requestResult != AudioManager.AUDIOFOCUS_GAIN){
                    requestResult = audioManager.requestAudioFocus(audioFocusChangeListener,
                            AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
                }
                if(requestResult == AudioManager.AUDIOFOCUS_GAIN){
                    if(status == PAUSE){
                        player.start();
                        status = PLAY;
                        statusChangeListener.onStatusChange(status);
                    }else{
                        start(musicList.get(musicPosition));
                    }

                }
            } else {
                if (musicList.size() > 0) {
                    start(musicList.get(DEFAULT_POSITION));
                }
            }
        }
    }

    public void start(Music music){
        if(requestResult != AudioManager.AUDIOFOCUS_GAIN){
            requestResult = audioManager.requestAudioFocus(audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        }

        if(requestResult == AudioManager.AUDIOFOCUS_GAIN){
            currentMusic = music;
            musicPosition = musicList.indexOf(music);
            /*release();
            player = new MediaPlayer();*/
            record = new MusicRecord();
            try {
                player.reset();
                player.setDataSource(music.path);
                player.prepare();
                player.start();
                status = PLAY;
                record.music = music;
                record.startTime = System.currentTimeMillis();
                statusChangeListener.onStatusChange(status);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.i(TAG,"Request audio focus faild!");
        }

    }

    public void startNetMusic(String url){
        if(requestResult != AudioManager.AUDIOFOCUS_GAIN){
            requestResult = audioManager.requestAudioFocus(audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        }

        if(requestResult == AudioManager.AUDIOFOCUS_GAIN){
            currentMusic = null;
            /*release();
            player = new MediaPlayer();*/
            record = null;
            try {
                player.reset();
                player.setDataSource(url);
                player.prepare();
                player.start();
                status = PLAY;
                statusChangeListener.onStatusChange(status);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.i(TAG,"Request audio focus faild!");
        }
    }

    public void pause(){
        if(player != null && player.isPlaying()){
            player.pause();
            status = PAUSE;
            statusChangeListener.onStatusChange(status);
        }
    }

    public void stop(){
        if(player != null){
            player.stop();
            status = STOP;
            if(record != null){
                record.endTime = System.currentTimeMillis();
                dbCommand.insertRecord(record);
            }
            statusChangeListener.onStatusChange(status);
            record = null;
        }
    }

    public void destroy(){
        release();
        if(dbCommand != null){
            dbCommand.closeDb();
            dbCommand = null;
        }
        if(audioManager != null){
            audioManager.abandonAudioFocus(audioFocusChangeListener);
            audioManager = null;
        }
        musicList.clear();
        oldList.clear();
        currentMusic = null;
        instance = null;
    }

    public void next(){
        Log.i("VIND","next");
        if(record != null){
            record.endTime = System.currentTimeMillis();
            dbCommand.insertRecord(record);
        }
        if(musicList.size()>0 && musicPosition < musicList.size()) {
            musicPosition = (musicPosition+1)%musicList.size();
            start(musicList.get(musicPosition));
        }else {
            musicPosition = -1;
        }
    }

    public void redomNext(){
        Random r = new Random(1000);
        musicPosition = r.nextInt()%musicList.size();
        if(musicList.size()>0 && musicPosition < musicList.size()) {
            start(musicList.get(musicPosition));
        }
    }

    MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if(status == PLAY){
                status = FINISH;
                next();
            }


        }
    };

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {

            switch (focusChange){
                case AudioManager.AUDIOFOCUS_GAIN:
                    /*if(stramVolum != -1){
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,stramVolum,AudioManager.FLAG_PLAY_SOUND);
                        stramVolum = -1;
                    }*/
                    if(status==PAUSE){
                        start();
                    }else if(status == STOP){
                        start(musicList.get(musicPosition));
                    }
                    player.setVolume(1.0f,1.0f);
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    /*if(status == PLAY){
                        stop();
                    }
                    break;*/
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if(status == PLAY){
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    /*stramVolum = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,stramVolum/2,AudioManager.FLAG_PLAY_SOUND);*/
                    if(status == PLAY){
                        player.setVolume(0.1f,0.1f);
                    }
                    break;
                default:
                    break;
            }
            requestResult = focusChange;
        }
    };

}
