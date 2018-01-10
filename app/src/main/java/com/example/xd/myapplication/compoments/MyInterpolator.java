package com.example.xd.myapplication.compoments;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by xd on 17-10-19.
 */

public class MyInterpolator  implements TimeInterpolator {
    @Override
    public float getInterpolation(float input) {
        float result;
        if(input <= 0.5f ){
           result = (float)(Math.sin(Math.PI*input)) /2;
        }else {
            result = (float)(2-Math.sin(Math.PI*input))/2;
        }
        return result;
    }
}
