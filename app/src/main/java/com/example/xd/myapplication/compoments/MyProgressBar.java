package com.example.xd.myapplication.compoments;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.example.xd.myapplication.R;


/**
 * Created by xd on 17-7-28.
 */

public class MyProgressBar extends ProgressBar  {

    private int bgColor;
    private int progressColor;
    private int max;
    private int progress = 0;
    private Paint paint;
    private Canvas canvas;

    public MyProgressBar(Context context) {
        this(context,null);
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(20);
        Log.i("VIND","paint2:"+paint);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyProgressBar,defStyleAttr,0);
        bgColor = typedArray.getColor(R.styleable.MyProgressBar_bgColor,0xffff5f5f);
        progressColor = typedArray.getColor(R.styleable.MyProgressBar_progressColor,0xffffc641);
        max = typedArray.getInt(R.styleable.MyProgressBar_max,100);


    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int bgColor) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.bgColor = bgColor;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(bgColor);
        canvas.drawLine(0,getHeight()/2,getWidth(),getHeight()/2,paint);
        paint.setColor(progressColor);
        canvas.drawLine(0,getHeight()/2,(getWidth()/100)*progress,getHeight()/2,paint);

    }

    public synchronized  void updateProgress(int progress){
        if(progress >= 0 && progress <= max){
            this.progress =  progress;
            invalidate();
        }
    }

    public int getProgress(){
        return progress;
    }

}
