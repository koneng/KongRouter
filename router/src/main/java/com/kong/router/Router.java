package com.kong.router;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Set;

/**
 * @author kong
 */
public class Router {

    private static Router sRouter;
    private Context mContext;
    private Object mProxy;

    private Router() {
    }

    public static Router get(){
        if (sRouter == null) {
            synchronized (Router.class){
                if (sRouter == null) {
                    sRouter = new Router();
                }
            }
        }
        return sRouter;
    }

    public void initRouter(Context context, Class tClass) {
        mContext = context.getApplicationContext();
        mProxy = create(tClass);
    }

    public Object getIRouter() {
        return mProxy;
    }

    private Object create(Class aClass) {
        return Proxy.newProxyInstance(aClass.getClassLoader(), new Class<?>[]{aClass},
                (proxy, method, args) -> {
                    boolean isUri;
                    RouterPath routerPath = method.getAnnotation(RouterPath.class);
                    String uri;
                    if(routerPath != null) {
                        isUri = false;
                        uri = routerPath.value();
                    } else {
                        isUri = true;
                        RouterUri routerUri = method.getAnnotation(RouterUri.class);
                        uri = routerUri.value();
                    }
                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    ComponentName componentName = in.resolveActivity(mContext.getPackageManager());
                    if (componentName != null) {
                        Intent intent = new Intent();
                        intent.setComponent(componentName);

                        //uri接口跳转
                        if(isUri) {
                            Uri u = in.getData();
                            Set<String> set = u.getQueryParameterNames();
                            Iterator<String> iterator = set.iterator();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                String value = u.getQueryParameter(key);
                                intent.putExtra(key, value);
                            }
                        }

                        RouterJumpHandler handler = null;
                        if(args != null && args.length > 0) {
                            int index = 0;
                            for (Object arg : args) {
                                if (arg instanceof RouterJumpHandler) {
                                    handler = (RouterJumpHandler) arg;
                                } else {
                                    //path拼接接口跳转
                                    if(!isUri) {
                                        Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();//拿到参数注解
                                        Annotation[] annotations = parameterAnnotationsArray[index];
                                        if (annotations != null && annotations.length != 0) {
                                            RouterParam param = (RouterParam) annotations[0];
                                            String key = param.value();
                                            if(args[index] instanceof Parcelable) {
                                                Parcelable value = (Parcelable)args[index];
                                                intent.putExtra(key, value);
                                            }else if (args[index] instanceof Serializable) {
                                                Serializable value = (Serializable)args[index];
                                                intent.putExtra(key, value);
                                            }else {
                                                String value = (String) args[index];
                                                intent.putExtra(key, value);
                                            }
                                        }
                                        index++;
                                    }
                                }
                            }
                        }

                        if (handler != null) {
                            handler.handleStartActivity(intent);
                        } else {
                            mContext.startActivity(intent);
                        }
                    }
                    return null;
                });
    }
}

