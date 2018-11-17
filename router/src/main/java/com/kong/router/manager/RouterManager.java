package com.kong.router.manager;

import android.content.Context;
import com.kong.router.Router;
import com.kong.router.interfaces.Interceptor;
import com.kong.router.interfaces.RouterJumpHandler;

import java.util.ArrayList;
import java.util.List;

public class RouterManager<T> {

    private Router<T> mRouter;

    private RouterManager(Builder<T> builder) {
        Context context = builder.mContext;
        Class<T> tClass = builder.tClass;
        List<Interceptor> interceptors = builder.mInterceptors;
        if(mRouter == null) {
            mRouter = new Router<>();
        }
        mRouter.initRouter(context, tClass, interceptors);
    }

    public T getIRouter() {
        if(mRouter == null) {
            throw new NullPointerException("IRouter is null !");
        }
        return mRouter.getIRouter();
    }

    public void startActivityForUri(String uri) {
        mRouter.startActivityForUri(uri, null);
    }

    public void startActivityForUri(String uri, RouterJumpHandler handler) {
        mRouter.startActivityForUri(uri, handler);
    }

   public static class Builder<T> {
        private Class<T> tClass;
        private Context mContext;
        private List<Interceptor> mInterceptors = new ArrayList<>();

        public Builder(Context context) {
            mContext = context;
        }

        public Builder create(Class<T> tClass) {
            this.tClass = tClass;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            mInterceptors.add(interceptor);
            return this;
        }

        public RouterManager<T> build() {
            if(tClass == null) {
                throw new NullPointerException("create class is null !");
            }
            return new RouterManager<>(this);
        }
    }
}
