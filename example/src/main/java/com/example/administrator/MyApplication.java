package com.example.administrator;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyRouterManager.get().initRouter(this);
    }
}
