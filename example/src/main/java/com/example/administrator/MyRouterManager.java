package com.example.administrator;

import android.content.Context;
import android.content.Intent;

import com.example.administrator.interfaces.IRouter;
import com.example.administrator.test.B_Activity;
import com.example.administrator.test.MainActivity;
import com.kong.router.interfaces.Interceptor;
import com.kong.router.manager.RouterManager;

public class MyRouterManager {

    private static MyRouterManager instance;
    private RouterManager mRouterManager;
    private Context mContext;

    private MyRouterManager() {
    }

    public static MyRouterManager get() {
        if(instance == null) {
            instance = new MyRouterManager();
        }
        return instance;
    }

    public void initRouter(Context context) {
        mContext = context.getApplicationContext();
        mRouterManager = new RouterManager
                .Builder(context)
                .create(IRouter.class)
                .build();
    }

    public RouterManager getRouterManager() {
        return mRouterManager;
    }

}
