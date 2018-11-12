package com.kong.router;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Set;

/**
 * @author kong
 */
public class Router {

    private static Router mRouter;
    private Context mContext;
    private Object proxy;

    private Router() {
    }

    public static Router get(){
        if (mRouter == null) {
            synchronized (Router.class){
                if (mRouter == null) {
                    mRouter = new Router();
                }
            }
        }
        return mRouter;
    }

    public void initRouter(Context context, Class tClass) {
        mContext = context.getApplicationContext();
        proxy = create(tClass);
    }

    public Object getIRouter() {
        return proxy;
    }

    private Object create(Class aClass) {
        return Proxy.newProxyInstance(aClass.getClassLoader(), new Class<?>[]{aClass},
                (proxy, method, args) -> {
                    RouterUri routerUri = method.getAnnotation(RouterUri.class);
                    String uri = routerUri.uri();
                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    ComponentName componentName = in.resolveActivity(mContext.getPackageManager());
                    if (componentName != null) {
                        Intent intent = new Intent();
                        intent.setComponent(componentName);
                        Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();//拿到参数注解
                        int index = 0;
                        boolean hasCallback = false;
                        RouterJumpCallback callback = null;
                        for (Object arg : args) {
                            if (arg instanceof RouterJumpCallback) {
                                hasCallback = true;
                                callback = (RouterJumpCallback) arg;
                            } else {
                                Annotation[] annotations = parameterAnnotationsArray[index];
                                if (annotations != null && annotations.length != 0) {
                                    RouterParam param = (RouterParam) annotations[0];
                                    String key = param.value();
                                    String value = (String) args[index];
                                    intent.putExtra(key, value);
                                }
                                index++;
                            }
                        }
                        if (hasCallback) {
                            callback.onStartActivity(intent);
                        } else {
                            mContext.startActivity(intent);
                        }
                    }
                    return null;
                });
    }

    public void startActivityForUri(String uri, RouterJumpCallback callback) {
        Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        ComponentName componentName = in.resolveActivity(mContext.getPackageManager());
        if (componentName != null) {
            Intent intent = new Intent();
            intent.setComponent(componentName);
            Uri u = in.getData();
            Set<String> set = u.getQueryParameterNames();
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = u.getQueryParameter(key);
                intent.putExtra(key, value);
            }
            if (callback != null) {
                callback.onStartActivity(intent);
            }else {
                mContext.startActivity(intent);
            }
        }
    }
}

