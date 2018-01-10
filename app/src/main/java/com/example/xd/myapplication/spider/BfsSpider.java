package com.example.xd.myapplication.spider;

import android.util.Log;


import com.example.xd.myapplication.models.Music;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xd on 17-8-10.
 */

public class BfsSpider {

    LinkedList<String> unVisitList = new LinkedList<String>();
    LinkedList<String> visitList = new LinkedList<String>();

    private void initCrawlerWithSeeds(String[] seeds){
        for(String seed : seeds){
            /*if(seed.startsWith("http://muisc.baidu.com")){

                unVisitList.add(seed);
            }*/
            unVisitList.add(seed);
        }
    }

    public void crawling(String[] seeds){
        String s=null;
        initCrawlerWithSeeds(seeds);
        while(unVisitList.size()>0&&visitList.size()<100){
            String url = unVisitList.getFirst();
            unVisitList.removeFirst();
            if(url == null) continue;
            if(s==null){
                s=getNetContentByOK(url);
            } else {
                s+=getNetContentByOK(url);
            }

        }
        Log.i("VIND","net music s:"+s);
        parseJson(s);
    }

    public void parseJson(String str){
        if(!isJson(str))return;
        try {
            JSONObject jsonObject =  new JSONObject(str);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray jsonArray = data.getJSONArray("song");
            List<Music> list = new ArrayList<Music>();
            for(int i=0; i<jsonArray.length();i++){
                Music mus = new Music();
                JSONObject object = jsonArray.getJSONObject(i);
                mus.name = object.getString("songname");
                mus.id = Integer.parseInt(object.getString("songid"));
                mus.artist = object.getString("artistname");
                list.add(mus);
                Log.i("VIND","music id :"+mus.id);
            }
            Log.i("VIND","net music list:"+list.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getNetContentByOK(String str){
        String content =null;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                        .connectTimeout(60, TimeUnit.SECONDS)
                                        .readTimeout(20,TimeUnit.SECONDS)
                                        .build();
        Request.Builder builder = new Request.Builder().url(str).addHeader("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)");
        builder.method("GET",null);
        Request request = builder.build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            content = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
         *异步访问网络
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                content = response.body().toString();
            }
        });*/
        return content;
    }


    public boolean isJson(String str){
        if(str == null){return false;}
        try {
            JSONObject object = new JSONObject(str);
            return true;
        } catch (JSONException e) {
            try {
                JSONArray array =  new JSONArray(str);
                return true;
            } catch (JSONException e1) {
                return false;
            }
        }


    }

    public String getNetContent(String url){
        Log.i("VIND","getNetContent");
        String content = null;
        HttpURLConnection connection = null;
        StringBuilder builder =  new StringBuilder();
        String line = null;
        try {
            URL modeUrl = new URL(url);
            connection = (HttpURLConnection) modeUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(6000);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));


            while ((line=reader.readLine())!=null){
                    builder.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e){

        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }
        content = builder.toString();

        return content;
    }
}
