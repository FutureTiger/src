package com.example.xd.myapplication.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.Target;
import com.example.xd.myapplication.MainActivity;
import com.example.xd.myapplication.R;
import com.example.xd.myapplication.compoments.MyProgressBar;
import com.example.xd.myapplication.controller.MusicPlayer;
import com.example.xd.myapplication.models.Music;
import com.example.xd.myapplication.models.OnStatusChangeListener;
import com.example.xd.myapplication.retrofit.GankApi;

import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by xd on 17-7-26.
 */

public class PlayFragment extends Fragment {


    private ImageView musicImage,play,next;
    private MusicPlayer musicPlayer;
    private TextView musicName,artist,duration;
    private MyProgressBar myProgressBar;
    private MyHandler myHandler;
    NotificationManager notificationManager;
    RemoteViews rv;
    Notification notification;
    boolean isShowNotification;

    public static final int MESSAGE_UPDATE_PROGRESS = 0;

    private class MyHandler extends  Handler{
        WeakReference<PlayFragment> ref;

        MyHandler(PlayFragment context){
            ref = new WeakReference<PlayFragment>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PlayFragment  fragment = ref.get();
            if(fragment == null){
                return;
            }
            switch (msg.what){
                case MESSAGE_UPDATE_PROGRESS:
                    fragment.updateProgress(-1);
                    sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS,1000);
                    break;
                default:
                    break;
            }
        }



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i("VIND","play onCreateView"+getActivity());
        View view = inflater.inflate(R.layout.fragment_play,container,false);
        /*play = (ImageView) view.findViewById(R.id.playImage);
        next = (ImageView)view.findViewById(R.id.nextImage);
        musicName = (TextView)view.findViewById(R.id.musicName);
        artist = (TextView)view.findViewById(R.id.musicArtist);
        myProgressBar = (MyProgressBar)view.findViewById(R.id.progress);
        duration = (TextView)view.findViewById(R.id.musicDuration);*/
        return view;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicPlayer = MusicPlayer.getInstance(getActivity());
        myHandler = new MyHandler(PlayFragment.this);
        musicPlayer.setOnStatusChangeListenler(new OnStatusChangeListener() {
            @Override
            public void onStatusChange(int status) {
                Log.i("VIND","status change :"+status);
                updateView();
                if(myHandler != null){
                    if(status == MusicPlayer.PLAY){
                        myHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS,1000);
                    }else{
                        myHandler.removeMessages(MESSAGE_UPDATE_PROGRESS);
                    }
                }

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        rv =  new RemoteViews(getActivity().getPackageName(),R.layout.notification_view);
        musicImage = (ImageView) getActivity().findViewById(R.id.musicImage);
        play = (ImageView) getActivity().findViewById(R.id.playImage);
        play.setColorFilter(Color.YELLOW);

        next = (ImageView)getActivity().findViewById(R.id.nextImage);
        musicName = (TextView)getActivity().findViewById(R.id.musicName);
        artist = (TextView)getActivity().findViewById(R.id.musicArtist);
        duration = (TextView)getActivity().findViewById(R.id.musicDuration);
        myProgressBar = (MyProgressBar)getActivity().findViewById(R.id.progress);


        myProgressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(myHandler != null){
                    myHandler.removeMessages(MESSAGE_UPDATE_PROGRESS);
                }

                int progress = ((int) event.getX())*100/v.getWidth();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        updateProgress(progress);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateProgress(progress);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(musicPlayer != null ){
                            musicPlayer.setCurrentDuration(progress);
                        }
                        if(myHandler != null){
                            myHandler.sendEmptyMessage(MESSAGE_UPDATE_PROGRESS);
                        }
                        break;
                    case MotionEvent.ACTION_OUTSIDE:
                        if(myHandler != null && !myHandler.hasMessages(MESSAGE_UPDATE_PROGRESS)){
                            myHandler.sendEmptyMessage(MESSAGE_UPDATE_PROGRESS);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicPlayer.getStatus() != musicPlayer.PLAY){
                    musicPlayer.start();
                }else{
                    musicPlayer.pause();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicPlayer.next();
            }
        });



    }



    private void showNotification(){
        if(notificationManager != null){
            isShowNotification = true;
            if(notification == null){
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity());
                Intent pauseIntent = new Intent();
                pauseIntent.setAction("com.xd.apptest.PAUSE");
                rv.setOnClickPendingIntent(R.id.rv_playImage,PendingIntent.getBroadcast(getActivity(),0,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT));
                Intent nexIntent = new Intent();
                nexIntent.setAction("com.xd.apptest.NEXT");
                rv.setOnClickPendingIntent(R.id.rv_nextImage,PendingIntent.getBroadcast(getActivity(),0,nexIntent,PendingIntent.FLAG_UPDATE_CURRENT));
                rv.setProgressBar(R.id.progress,100,0,false);

                Intent in = new Intent(getActivity(),MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(),0,in, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContent(rv)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setContentTitle("MusicPlayer")
                        .setContentText("you're start music player")
                        .setSmallIcon(R.drawable.app_music);
                notification = mBuilder.build();
                notification.flags = Notification.FLAG_NO_CLEAR;
                notificationManager.notify(0,notification);
            }else{
                notificationManager.notify(0,notification);
            }
        }else{
            isShowNotification = false;
        }



    }

    private void hideNotification(){
        isShowNotification = false;
        if(notificationManager != null){
            notificationManager.cancel(0);
        }
    }

    public void updateView(){
        if(isShowNotification){
            if(musicPlayer.getStatus() != musicPlayer.PLAY){
                rv.setImageViewResource(R.id.rv_playImage,R.drawable.btn_play);
            }else{
                rv.setImageViewResource(R.id.rv_playImage,R.drawable.btn_suspend);
            }
        }else{
            if(musicPlayer.getStatus() != musicPlayer.PLAY){
                play.setImageResource(R.drawable.btn_play);
            }else{
                play.setImageResource(R.drawable.btn_suspend);
            }
        }

        setText();
    }

    public void setText(){
        Music music = musicPlayer.getCurrentMusic();
        Log.i("VIND","setText music = "+music);
        if(music != null){
            if(isShowNotification){
                rv.setTextViewText(R.id.rv_musicName,music.title);
                rv.setTextViewText(R.id.rv_musicArtist,music.artist);
                Target notificationTarget = new NotificationTarget(getActivity(),
                        rv,
                        R.id.rv_musicImage,
                        notification,
                        0);
                Glide.with(getActivity().getApplicationContext())
                        .load(getAlbumArt(music.albumId))
                        .asBitmap()
                        .placeholder(R.drawable.app_music)
                        .error(R.drawable.app_music)
                        .into(notificationTarget);
            }else{
                musicName.setText(music.title);
                artist.setText(music.artist);
                Glide.with(PlayFragment.this).load(getAlbumArt(music.albumId))
                        .placeholder(R.drawable.app_music)
                        .error(R.drawable.app_music)
                        .thumbnail(0.1f)
                        .crossFade(500)
                        .into(musicImage);
            }

           // BitmapDrawable bitmapDrawable = getMusicImage(music);

            /*if(bitmapDrawable != null){
                musicImage.setImageDrawable(bitmapDrawable);
            }else{
                musicImage.setImageResource(R.drawable.app_music);
            }*/
            updateProgress(-1);
        }
    }

    private void updateProgress(int progress){
        int curdur = -1;
        int totaldur = musicPlayer.getTotaleDuration();
        if(progress <0){
            curdur = musicPlayer.getCurrentDuration();
        }else{
            curdur = totaldur*progress/100;
        }

        int minutes = curdur/(60*1000);
        int seconds = (curdur/1000)%60;
        if(isShowNotification){
            rv.setTextViewText(R.id.rv_musicDuration,String.format("%02d",minutes)+":"+String.format("%02d",seconds));
            rv.setProgressBar(R.id.rv_progress,100,100*curdur/totaldur,false);
            notificationManager.notify(0,notification);
        }else{
            myProgressBar.updateProgress(100*curdur/totaldur);
            duration.setText(String.format("%02d",minutes)+":"+String.format("%02d",seconds));
        }
    }



    private BitmapDrawable getMusicImage(Music music){

        BitmapDrawable bitmapDrawable = null;
        String art = getAlbumArt(music.albumId);
        if(art != null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(art,options);

            options.outHeight= options.outHeight*50/options.outWidth;
            options.outWidth = 50;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(art,options);
            bitmapDrawable = new BitmapDrawable(bitmap);
        }
        return bitmapDrawable;
    }

    private String getAlbumArt(int id){
        String mUriAlbums = "content://media/external/audio/albums";
        Cursor cursor = null;
        String albunArt = null;
        try{
            cursor = getActivity().getContentResolver().query(Uri.parse(mUriAlbums+"/"+Integer.toString(id)),
                    new String[]{"album_art"},null,null,null);
            while (cursor.moveToNext()){
                albunArt = cursor.getString(0);
                break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor !=null){
                cursor.close();
                cursor = null;
            }
        }
        return albunArt;

    }

    @Override
    public void onStart() {
        Log.i("VIND","onstart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i("VIND","onResume");
        super.onResume();
        hideNotification();
        updateView();
    }

    @Override
    public void onPause() {
        Log.i("VIND","onPause");
        super.onPause();

    }

    @Override
    public void onStop() {
        Log.i("VIND","onStop");
        super.onStop();
        showNotification();
        updateView();
    }

    @Override
    public void onDestroy() {
        Log.i("VIND","onDestroy");
        super.onDestroy();

        if(musicPlayer != null){
            musicPlayer.setOnStatusChangeListenler(null);
            musicPlayer.destroy();
            musicPlayer =null;
        }
        if(myHandler !=null){
            myHandler.removeCallbacksAndMessages(null);
            myHandler = null;
        }
        if(rv!=null){
            rv.removeAllViews(R.layout.notification_view);
            rv=null;
        }
        notification=null;
        notificationManager = null;
       // instance = null;
    }

    @Override
    public void onDestroyView() {
        hideNotification();
        super.onDestroyView();
    }
}


