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
    private RouterManager<IRouter> mRouterManager;
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
                .Builder<IRouter>(context)
                .create(IRouter.class)
                .addInterceptor(mInterceptor)
                .build();
    }

    public RouterManager<IRouter> getRouterManager() {
        return mRouterManager;
    }

    private Interceptor mInterceptor = new Interceptor() {
        @Override
        public Intent intercept() {
            if(false) {
                Intent intent = new Intent(mContext, B_Activity.class);
                intent.putExtra("id", "2000000000000000");
                return intent;
            }
            return null;
        }
    };
}
