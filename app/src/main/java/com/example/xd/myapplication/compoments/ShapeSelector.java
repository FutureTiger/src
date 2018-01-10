package com.example.xd.myapplication.compoments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

/**
 * Created by xd on 17-7-12.
 */

public class ShapeSelector {
    private int mShape;
    private int mDefaultBgColor;
    private int mDisableBgColor;
    private int mPressedBgColor;
    private int mFocusedBgColor;
    private int mSelectedBgColor;

    private int mStrokewidth;
    private int mDefaultStrokeColor;
    private int mDisableStrokeColor;
    private int mPressedStrokeColor;
    private int mFocusedStrokeColor;
    private int mSelectedStrokeColor;

    private boolean hasSetDisableBgColor = false;
    private boolean hasSetPressedBgColor = false;
    private boolean hasSetFocusedBgColor = false;
    private boolean hasSetSelectedBgColor = false;
    private boolean hasSetDisableStrokeColor = false;
    private boolean hasSetPressedStrokeColor = false;
    private boolean hasSetFocusedStrokeColor = false;
    private boolean hasSetSelectedStrokeColor = false;

    public  ShapeSelector(){
        mShape = GradientDrawable.RECTANGLE;
        mDefaultBgColor = Color.TRANSPARENT;
        mDisableBgColor = Color.TRANSPARENT;
        mPressedBgColor = Color.TRANSPARENT;
        mFocusedBgColor = Color.TRANSPARENT;
        mSelectedBgColor = Color.TRANSPARENT;

        mStrokewidth = 0;
        mDefaultStrokeColor = Color.TRANSPARENT;
        mDisableStrokeColor = Color.TRANSPARENT;
        mPressedStrokeColor = Color.TRANSPARENT;
        mFocusedStrokeColor = Color.TRANSPARENT;
        mSelectedStrokeColor = Color.TRANSPARENT;

    }

    private @interface Shape{}

    public ShapeSelector setShape(@Shape int mShape) {
        this.mShape = mShape;
        return  this;
    }

    public ShapeSelector setDefaultBgColor(int color) {

        this.mDefaultBgColor = color;
        if(!hasSetDisableBgColor)
            mDisableBgColor = color;
        if(!hasSetPressedBgColor)
            mDisableBgColor = color;
        if(!hasSetFocusedBgColor)
            mDisableBgColor = color;
        if(!hasSetSelectedBgColor)
            mDisableBgColor = color;

        return this;
    }

    public ShapeSelector setmDisableBgColor(int mDisableBgColor) {
        this.mDisableBgColor = mDisableBgColor;
        this.hasSetDisableBgColor = true;
        return this;
    }

    public ShapeSelector setmPressedBgColor(int mPressedBgColor) {
        this.mPressedBgColor = mPressedBgColor;
        this.hasSetPressedBgColor = true;
        return this;
    }

    public ShapeSelector setmFocusedBgColor(int mFocusedBgColor) {
        this.mFocusedBgColor = mFocusedBgColor;
        this.hasSetFocusedBgColor = true;
        return this;
    }

    public ShapeSelector setmSelectedBgColor(int mSelectedBgColor) {
        this.mSelectedBgColor = mSelectedBgColor;
        this.hasSetSelectedBgColor = true;
        return this;
    }

    public ShapeSelector setmStrokewidth(int mStrokewidth) {
        this.mStrokewidth = mStrokewidth;
        return this;
    }

    public ShapeSelector setmDefaultStrokeColor(int mDefaultStrokeColor) {
        this.mDefaultStrokeColor = mDefaultStrokeColor;
        if(!hasSetDisableStrokeColor)
            this.mDisableStrokeColor = mDefaultStrokeColor;
        if(!hasSetPressedStrokeColor)
            this.mPressedStrokeColor = mDefaultStrokeColor;
        if(!hasSetFocusedStrokeColor)
            this.mFocusedStrokeColor = mDefaultStrokeColor;
        if(!hasSetSelectedStrokeColor)
            this.mSelectedStrokeColor = mDefaultStrokeColor;
        return this ;
    }

    public ShapeSelector setmDisableStrokeColor(int mDisableStrokeColor) {
        this.mDisableStrokeColor = mDisableStrokeColor;
        this.hasSetDisableStrokeColor = true;
        return this;
    }

    public ShapeSelector setmPressedStrokeColor(int mPressedStrokeColor) {
        this.mPressedStrokeColor = mPressedStrokeColor;
        this.hasSetPressedStrokeColor =true;
        return this;
    }

    public ShapeSelector setmFocusedStrokeColor(int mFocusedStrokeColor) {
        this.mFocusedStrokeColor = mFocusedStrokeColor;
        this.hasSetFocusedStrokeColor = true;
        return this;
    }

    public ShapeSelector setmSelectedStrokeColor(int mSelectedStrokeColor) {
        this.mSelectedStrokeColor = mSelectedStrokeColor;
        this.hasSetSelectedStrokeColor = true;
        return this;
    }






}
