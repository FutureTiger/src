package com.example.xd.myapplication.models;


import android.support.annotation.IntDef;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by xd on 17-8-1.
 */

public class MusicRecord {
    public Music music;
    public long startTime;
    public long endTime;

    public String toDateString(long milis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(calendar.getTime());
        return date;
    }
}
