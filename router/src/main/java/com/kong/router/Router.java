package com.kong.router;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kong.router.annotation.RequestCode;
import com.kong.router.annotation.RouterParam;
import com.kong.router.annotation.RouterPath;
import com.kong.router.annotation.RouterUri;
import com.kong.router.interfaces.IAction;
import com.kong.router.interfaces.Interceptor;
import com.kong.router.manager.RouterManager;
import com.shopee.router.annotation.interfaces.Constants;
import com.shopee.router.annotation.interfaces.IRouterMap;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author kong
 */
public class Router {

    private Context mContext;
    private Object mProxy;
    private List<Interceptor> mInterceptors;
    private  Map<String, Class> routerMap = new HashMap<>();

    public <T> void initRouter(Context context, Class<T> tClass, List<Interceptor> interceptors) {
        mContext = context.getApplicationContext();
        try {
            Class<?> routerMapClass = Class.forName(Constants.ROUTER_MAP_PACKAGE_NAME + "." + Constants.ROUTER_MAP_NAME);
            IRouterMap iRouterMap = (IRouterMap) routerMapClass.newInstance();
            routerMap = iRouterMap.loadInfo();
        } catch (Exception e) {
            //路由map找不到 ，ignore
        }
        mInterceptors = interceptors;
        mProxy = create(tClass);
    }

    public <T> T getIRouter() {
        return (T) mProxy;
    }

    private <T> Object create(Class<T> aClass) {
        return Proxy.newProxyInstance(aClass.getClassLoader(), new Class<?>[]{aClass},
                (proxy, method, args) -> {
                    boolean isUri;
                    RouterPath routerPath = method.getAnnotation(RouterPath.class);
                    String uri;
                    if (routerPath != null) {
                        isUri = false;
                        uri = routerPath.value();
                    } else {
                        isUri = true;
                        RouterUri routerUri = method.getAnnotation(RouterUri.class);
                        uri = routerUri.value();
                    }
                    Intent source = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    ComponentName componentName = source.resolveActivity(mContext.getPackageManager());
                    IAction action = null;
                    Intent target = new Intent();
                    if (componentName != null) {
                        target.setComponent(componentName);
                        //uri接口跳转
                        if (isUri) {
                            parseUri(source, target);
                        }
                        startActivity(args, action, method, target);
                    } else {
                        Class activity = routerMap.get(uri);
                        if(activity != null) {
                            target.setClass(mContext, activity);
                            startActivity(args, action, method, target);
                        } else {
                            //没有找到activity，降级处理
                            for (Object arg : args) {
                                if (arg instanceof IAction) {
                                    action = (IAction) arg;
                                    action.onLost(uri);
                                }
                            }
                        }
                    }
                    return null;
                });
    }

    public void startActivityForUri(String uri) {
        startActivityForUri(uri, null);
    }

    public void startActivityForUri(String uri, IAction action) {
        startActivityForUri(null, uri, -1, action);
    }

    public void startActivityForUri(Activity context, String uri,
                                    int requestCode, IAction action) {
        if(!TextUtils.isEmpty(uri)) {
            Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            ComponentName componentName = in.resolveActivity(mContext.getPackageManager());
            if(componentName != null) {
                Intent intent = new Intent();
                intent.setComponent(componentName);
                parseUri(in, intent);
                Intent target = interceptProceed(intent);
                if(target != null) {
                    startActivity(context, target, requestCode, action);
                }
            }
        }
    }

    private void startActivity (Object[] args, IAction action, Method method, Intent target) {
        int requestCode = -1;
        Activity context = null;
        if (args != null && args.length > 0) {
            int index = 0;
            for (Object arg : args) {
                if (arg instanceof IAction) {
                    action = (IAction) arg;
                } else if (arg instanceof Activity) {
                    context = (Activity) arg;
                } else {
                    requestCode = parseParamsAnnotation(method, index, args, target);
                }
                index++;
            }
        }
        Intent intent = interceptProceed(target);
        if(intent != null) {
            startActivity(context, intent, requestCode, action);
        }
    }

    private Intent interceptProceed(Intent intent) {
        Intent oldIntent;
        if (mInterceptors != null && mInterceptors.size() > 0) {
            Interceptor first = mInterceptors.get(0);
            RealChain realChain = new RealChain(mInterceptors, intent);
            oldIntent = realChain.proceed(first);
        } else {
            oldIntent = intent;
        }
        return oldIntent;
    }

    private void startActivity(Activity context, Intent intent,
                               int requestCode, IAction action) {
        if(action != null) {
            action.onFound(intent);
        } else if (context != null) {
            // 需要activity上下文环境
            context.startActivityForResult(intent, requestCode);
        }else {
            mContext.startActivity(intent);
        }
    }

    private void parseUri(Intent sourceIntent, Intent targetIntent) {
        Uri u = sourceIntent.getData();
        assert u != null;
        Set<String> set = u.getQueryParameterNames();
        for (String key : set) {
            String value = u.getQueryParameter(key);
            targetIntent.putExtra(key, value);
        }
    }

    private int parseParamsAnnotation(Method method, int index, Object[] args, Intent intent) {
        int requestCode = -1;
        Annotation[][] parameterAnnotationsArray
                = method.getParameterAnnotations();//拿到参数注解
        Annotation[] annotations = parameterAnnotationsArray[index];
        if (annotations != null && annotations.length != 0) {
            Annotation annotation = annotations[0];
            if(annotation instanceof RouterParam) {
                RouterParam param = (RouterParam) annotation;
                String key = param.value();
                if (args[index] instanceof Parcelable) {
                    Parcelable value = (Parcelable) args[index];
                    intent.putExtra(key, value);
                } else if (args[index] instanceof Serializable) {
                    Serializable value = (Serializable) args[index];
                    intent.putExtra(key, value);
                } else {
                    String value = (String) args[index];
                    intent.putExtra(key, value);
                }
            } else if (annotation instanceof RequestCode){
                requestCode = (Integer) args[index];
            }
        }
        return requestCode;
    }
}

