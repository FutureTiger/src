package com.example.xd.myapplication.models;

import android.os.IBinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by xd on 17-11-3.
 */

public class MyClipProxy implements InvocationHandler {

    private final IBinder mBase;
    public MyClipProxy(IBinder binder){
        mBase = binder;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if("queryLocalInterface".equals(method.getName())){
            Class<?> mStubClass = Class.forName("android.content.IClipboard$Stub");
            //2.在拿到IClipboard本地对象类
            Class<?> mIClipboard = Class.forName("android.content.IClipboard");
            //3.创建我们自己的代理
            return Proxy.newProxyInstance(mStubClass.getClassLoader(),
                    new Class[]{mIClipboard},
                    new MyClip(mBase,mStubClass));
        }
        return method.invoke(mBase,args);
    }
}
