package com.example.xd.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.xd.myapplication.compoments.MyViewPager;
import com.example.xd.myapplication.controller.MusicPlayer;
import com.example.xd.myapplication.models.ClipHelper;
import com.example.xd.myapplication.models.MyClip;
import com.example.xd.myapplication.retrofit.GankApi;
import com.example.xd.myapplication.view.ListFragment;
import com.example.xd.myapplication.view.NetMusicFragment;
import com.example.xd.myapplication.view.PlayFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.color.holo_blue_bright;
import static android.R.color.holo_orange_dark;


public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    List<Fragment> list;
    List<Class> classList = new ArrayList<Class>();
    List<TextView> viewList = new ArrayList<TextView>();
    MyViewPager viewPager;
    FragmentPagerAdapter adapter;
    NotificationBroadcast notificationBroadcast;
    TextView tabList,tabPlay,tabNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        StateListDrawable selector = new StateListDrawable();
        selector.addState(new int[]{android.R.attr.state_pressed},new ColorDrawable(Color.GREEN));
        notificationBroadcast = new NotificationBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.xd.apptest.PAUSE");
        intentFilter.addAction("com.xd.apptest.NEXT");
        registerReceiver(notificationBroadcast,intentFilter);
        //ClipHelper.binder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://sug.music.baidu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GankApi api = retrofit.create(GankApi.class);
        Call<ResponseBody> call = api.getApiInfo();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String result = null;
                try {
                    result = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("VIND","retrofit result ="+result);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("VIND","retrofit failed");
            }
        });


    }

    private void initViewPager(){
        viewPager = (MyViewPager)findViewById(R.id.viewpager);
        list = new ArrayList<Fragment>();
        Fragment listFragment = new ListFragment();
        list.add(listFragment);
        classList.add(ListFragment.class);
        Fragment playFragment = new PlayFragment();
        list.add(playFragment);
        classList.add(PlayFragment.class);
        Fragment netFragment = new NetMusicFragment();
        list.add(netFragment);
        classList.add(NetMusicFragment.class);



        fragmentManager = getSupportFragmentManager();

        adapter = new FragmentPagerAdapter(fragmentManager) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public int getItemPosition(Object object) {
                if(object instanceof PlayFragment){
                    ((PlayFragment)object).updateView();
                }
                return super.getItemPosition(object);
            }

        };


        tabList = (TextView)findViewById(R.id.tab_list);
        viewList.add(0,tabList);
        tabList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        tabPlay = (TextView)findViewById(R.id.tab_play);
        viewList.add(1,tabPlay);
        tabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        tabNet = (TextView)findViewById(R.id.tab_net);
        viewList.add(2,tabNet);
        tabNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);

            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                cleanBackColor(viewList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        cleanBackColor(viewList.get(0));
    }

    private void cleanBackColor(View view){
        tabList.setBackgroundColor(getColor(holo_blue_bright));
        tabPlay.setBackgroundColor(getColor(holo_blue_bright));
        tabNet.setBackgroundColor(getColor(holo_blue_bright));
        view.setBackgroundColor(getColor(holo_orange_dark));
    }

    public void jump(@NonNull Class fragment){
        if(classList.contains(fragment)){
            viewPager.setCurrentItem(classList.indexOf(fragment));
            adapter.notifyDataSetChanged();
        }


    }
    

    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            List<String> permissionList = new ArrayList<String>();

            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if(permissionList.size()>0){
                requestPermissions(permissionList.toArray(new String[permissionList.size()]),0);
            }else {
                initViewPager();
            }
        }
    }

    class  NotificationBroadcast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("VIND","recieve notification action");
            MusicPlayer player = MusicPlayer.getInstance(MainActivity.this);
            if(action.equals("com.xd.apptest.PAUSE")){
                if(player.getStatus() != player.PLAY){
                    player.start();
                }else{
                    player.pause();
                }
            }else if(action.equals("com.xd.apptest.NEXT")){
                player.next();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case 0:
                if(grantResults.length > 0
                        &&(grantResults[0] != PackageManager.PERMISSION_GRANTED
                            ||grantResults[1] != PackageManager.PERMISSION_GRANTED)){
                    onDestroy();
                }else {
                    initViewPager();
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayer.getInstance(this).destroy();
        unregisterReceiver(notificationBroadcast);
    }




}
