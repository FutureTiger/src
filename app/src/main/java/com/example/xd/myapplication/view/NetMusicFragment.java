package com.example.xd.myapplication.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.xd.myapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xd on 17-8-3.
 */

public class NetMusicFragment extends Fragment {
    WebView webView;
    ImageView imageView;
    LoaderManager loaderManager;
    Context context;
    List<String> pathList = new ArrayList<String>();

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] mediaColumns = { MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.MIME_TYPE,
                    MediaStore.Video.Media.DISPLAY_NAME };
            CursorLoader loader = new CursorLoader(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    mediaColumns,null,null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            pathList.clear();
            while (data.moveToNext()){
                pathList.add(data.getString(data.getColumnIndex(MediaStore.Video.Media.DATA)));
            }
            Log.i("VIND","pathList.size() :"+pathList.size());
            if(pathList.size()>0){
                Log.i("VIND","pathList.get(0) :"+pathList.get(0));
                Glide.with(NetMusicFragment.this).load(Uri.fromFile( new File( pathList.get(0) ) ))
                        .placeholder(R.drawable.app_music)
                        .into(imageView);
            }



        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webview_layout,container,false);
        webView = (WebView)view.findViewById(R.id.webview);
        imageView =(ImageView)view.findViewById(R.id.glideImage);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        loaderManager = getLoaderManager();
        loaderManager.initLoader(0,null,loaderCallbacks);

        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl("https://www.baidu.com");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
