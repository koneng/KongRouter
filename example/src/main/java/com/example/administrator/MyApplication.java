package com.example.administrator;

import android.app.Application;

import com.kong.router.ARouter;
import com.kong.router.Router;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Router.get().init(this);
        ARouter.get().init(this);
    }
}
