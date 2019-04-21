package com.kong.router.manager;

import android.app.Activity;
import android.content.Context;

import com.kong.router.Router;
import com.kong.router.interfaces.IAction;
import com.kong.router.interfaces.Interceptor;

import java.util.ArrayList;
import java.util.List;


public class RouterManager {

    private Router mRouter;

    private RouterManager(Builder builder) {
        Context context = builder.mContext;
        Class tClass = builder.tClass;
        List<Interceptor> interceptors = builder.mInterceptors;
        if(mRouter == null) {
            mRouter = new Router();
        }
        mRouter.initRouter(context, tClass, interceptors);
    }

    public <T> T getIRouter() {
        if(mRouter == null) {
            throw new NullPointerException("IRouter is null !");
        }
        return mRouter.getIRouter();
    }

    public void startActivityForUri(String uri) {
        mRouter.startActivityForUri(uri);
    }

    public void startActivityForUri(String uri, IAction handler) {
        mRouter.startActivityForUri(uri, handler);
    }

    public void startActivityForUri(Activity context, String uri, int requestCode, IAction handler) {
        mRouter.startActivityForUri(context, uri, requestCode, handler);
    }

   public static class Builder {
        private Class tClass;
        private Context mContext;
        private List<Interceptor> mInterceptors = new ArrayList<>();

        public Builder(Context context) {
            mContext = context;
        }

        public <T> Builder create(Class<T> tClass) {
            this.tClass = tClass;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if(mInterceptors != null) {
                mInterceptors.add(interceptor);
            }
            return this;
        }

        public RouterManager build() {
            if(tClass == null) {
                throw new NullPointerException("create class is null !");
            }
            return new RouterManager(this);
        }
    }
}
