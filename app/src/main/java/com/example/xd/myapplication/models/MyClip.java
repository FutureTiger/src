package com.example.xd.myapplication.models;

import android.content.ClipData;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by xd on 17-11-3.
 */

public class MyClip implements InvocationHandler {

    public Object mBase;
    public MyClip(IBinder base, Class<?> mStubClass) {
        try {
            Method asInterface = mStubClass.getDeclaredMethod("asInterface", IBinder.class);
            mBase = asInterface.invoke(null,base);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if("getPrimaryClip".equals(method.getName())){
            Log.i("VIND","getPrimaryClip");
            return ClipData.newPlainText(null,"我改了系统源码，哈哈哈");
        }
        //再拦截是否有复制的方法，放系统认为一直都有
        if("hasPrimaryClip".equals(method.getName())){
            return true;
        }
        //其他启动还是返回原有的
        return null;

    }
}
