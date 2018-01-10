package com.example.xd.myapplication.models;

import android.os.IBinder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by xd on 17-11-3.
 */

public class ClipHelper {
    public static void binder(){
        try {
            Class serviceManager = Class.forName("android.os.ServiceManager");
            Method getService = serviceManager.getDeclaredMethod("getService",String.class);
            IBinder iBinder = (IBinder) getService.invoke(null,"clipboard");
            IBinder myBinder = (IBinder) Proxy.newProxyInstance(serviceManager.getClassLoader(),
                    new Class[]{IBinder.class}
                    ,new MyClipProxy(iBinder)
            );
            //5.拿到ServiceManager中的数组
            Field field = serviceManager.getDeclaredField("sCache");
            field.setAccessible(true);
            Map<String, IBinder> map = (Map) field.get(null);
            //将我们的服务类存入map
            map.put("clipboard",myBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
