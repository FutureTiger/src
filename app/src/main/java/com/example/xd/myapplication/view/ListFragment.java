package com.example.xd.myapplication.view;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;


import com.example.xd.myapplication.MainActivity;
import com.example.xd.myapplication.R;
import com.example.xd.myapplication.compoments.NoScrollListView;
import com.example.xd.myapplication.controller.MusicPlayer;
import com.example.xd.myapplication.models.Music;
import com.example.xd.myapplication.spider.BfsSpider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xd on 17-7-26.
 */

public class ListFragment extends Fragment {

    NoScrollListView listView;
    SimpleCursorAdapter adapter;
    List<Music> musiclist ;
    MusicPlayer musicPlayer;
    LoaderManager loaderManager;
    Context cotext ;
    ContentObserver observer = new ContentObserver(new Handler()) {
        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            loaderManager.restartLoader(0,null,loaderCallbacks);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
        }
    };

    /*private static ListFragment instance;

    public static ListFragment getInstance(){
        if (instance == null){
            synchronized (ListFragment.class){
                if(instance == null){
                    instance = new ListFragment();
                }
            }
        }
        return instance;
    }*/

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader loader = new CursorLoader(cotext, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            return loader;
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            Log.i("VIND","reload");
            musiclist = new ArrayList<Music>();
            data.moveToPosition(-1);
            while(data.moveToNext()){
                Music music = new Music();
                music.id = data.getInt(data.getColumnIndex(MediaStore.Audio.Media._ID));
                music.name = data.getString(data.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                music.albums = data.getString(data.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                music.albumId = data.getInt(data.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                music.artist = data.getString(data.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                music.duration = data.getInt(data.getColumnIndex(MediaStore.Audio.Media.DURATION));
                music.path = data.getString(data.getColumnIndex(MediaStore.Audio.Media.DATA));
                music.title = data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE));
                music.size = data.getLong(data.getColumnIndex(MediaStore.Audio.Media.SIZE));
                musiclist.add(music);
            }
                musicPlayer.setMusicList(musiclist);
                adapter.changeCursor(data);
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
            adapter.swapCursor(null);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    Toolbar mToolbar;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.collapsing_toolbar,container,false);
        listView = (NoScrollListView)view.findViewById(R.id.fragment_list);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        float high = wm.getDefaultDisplay().getHeight();
        float wid = wm.getDefaultDisplay().getWidth();
        Log.i("VIND"," high = "+high+"  , wid = "+wid);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cotext = getActivity();
        musicPlayer = MusicPlayer.getInstance(cotext);
        cotext.getContentResolver().registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,true,observer);
        adapter = new SimpleCursorAdapter(cotext,R.layout.item,null,new String[]{MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST},new int[]{R.id.title,R.id.artist},0);
        listView.setAdapter(adapter);

        loaderManager = getLoaderManager();
        loaderManager.initLoader(0,null,loaderCallbacks);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                musicPlayer.start(musiclist.get(position));
                Log.i("VIND","path = "+musiclist.get(position).path);
                ((MainActivity)cotext).jump(PlayFragment.class);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Music music = musiclist.get(position);
                Log.i("VIND","path = "+music.path);
                boolean ret = deleteFile(music.path);
                if(ret){
                    MediaScannerConnection.scanFile(cotext, new String[]{music.path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String s, Uri uri) {
                            Log.i("VIND","delete file success");
                        }
                    });
                    /*ContentResolver contentResolver = cotext.getContentResolver();
                    contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,MediaStore.Audio.Media.DATA+"=?",
                            new String[]{music.path});*/
                }else{
                    Log.i("VIND","delete file failed");
                }

                return true;
            }
        });

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
//        mCollapsingToolbarLayout.setTitle("CollapsingToolbarLayout");
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.GREEN);
        new Thread(new Runnable() {
            @Override
            public void run() {
                BfsSpider bfsSpider = new BfsSpider();
                bfsSpider.crawling(new String[]{"http://sug.music.baidu.com/info/suggestion"});
            }
        }).start();
    }

    private boolean succ = false;
    public boolean deleteFile(String path){
        File file = new File(path);
        if(file.exists()){
            if(file.isFile()){
                succ = file.delete();
            }else if(file.isDirectory()){
                File[] childFiles = file.listFiles();
                for(File childFile : childFiles){
                    deleteFile(childFile.getAbsolutePath());
                }
            }
        }
        return  succ;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
    }

    @Override
    public void onDestroy() {
        Log.i("VIND","onDestroy");
        if(musicPlayer != null){
            musicPlayer.destroy();
            musicPlayer = null;
        }

        super.onDestroy();
        //instance = null;
    }
}
