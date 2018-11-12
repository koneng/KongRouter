package com.example.administrator;

import android.app.Application;
import com.example.administrator.interfaces.IRouter;
import com.kong.router.Router;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Router.get().initRouter(this, IRouter.class);
    }
}
