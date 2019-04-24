package com.kong.router;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;
import com.shopee.router.annotation.RequestCode;
import com.shopee.router.annotation.RouterParam;
import com.shopee.router.annotation.RouterUri;
import com.kong.router.interfaces.IAction;
import com.kong.router.interfaces.Interceptor;
import com.shopee.router.Constants;
import com.shopee.router.annotation.RouterPath;
import com.shopee.router.interfaces.IRouterPathMap;
import com.shopee.router.interfaces.IRouterTargetMap;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author kong
 */
public class Router {

    private Context mContext;
    private List<Interceptor> mInterceptors = new ArrayList<>();
    private Map<String, Class> mTargetMap = new HashMap<>();
    private Map<String, String> mPathMap = new HashMap<>();
    private Map<Class, Object> mParamsMap = new HashMap<>();
    private Object mProxy;
    private String mPath;

    private volatile static Router instance;

    private Router() {
        try {
            Class<?> routerTargetMapClass = Class.forName(Constants.ROUTER_MAP_PACKAGE_NAME + "." + Constants.ROUTER_TARGET_MAP_NAME);
            IRouterTargetMap iRouterMap = (IRouterTargetMap) routerTargetMapClass.newInstance();
            mTargetMap = iRouterMap.loadInfo();
        } catch (Exception e) {
            //路由map找不到 ，ignore
        }
        try {
            Class<?> routerPathMapClass = Class.forName(Constants.ROUTER_MAP_PACKAGE_NAME + "." + Constants.ROUTER_PATH_MAP_NAME);
            IRouterPathMap iRouterMap = (IRouterPathMap) routerPathMapClass.newInstance();
            mPathMap = iRouterMap.loadInfo();
        } catch (Exception e) {
            //路径map找不到 ，ignore
        }
    }

    public static Router get() {
        if (instance == null) {
            synchronized (Router.class) {
                if (instance == null) {
                    instance = new Router();
                }
            }
        }
        return instance;
    }

    /**
     * init
     * @param context
     */
    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public Router addInterceptor(Interceptor interceptor) {
        mInterceptors.add(interceptor);
        return this;
    }

    public Router addInterceptors(List<Interceptor> interceptors) {
        mInterceptors.addAll(interceptors);
        return this;
    }

    public <T> T create(Class<T> aClass) {
        return proxy(aClass);
    }

    public <T> Router addIRouter(Class<T> routerInterface) {
        mProxy = proxy(routerInterface);
        return this;
    }

    public Router path(String path) {
        mPath = path;
        return this;
    }

    public Router withInt(int value) {
        mParamsMap.put(int.class, value);
        return this;
    }

    public Router withString(String value) {
        mParamsMap.put(String.class, value);
        return this;
    }

    public Router withObject(Parcelable value) {
        mParamsMap.put(value.getClass(), value);
        return this;
    }

    public Router withObject(Serializable value) {
        mParamsMap.put(value.getClass(), value);
        return this;
    }

    public Router withLong(int value) {
        mParamsMap.put(long.class, value);
        return this;
    }

    public Router withFloat(int value) {
        mParamsMap.put(float.class, value);
        return this;
    }

    public Router withDouble(int value) {
        mParamsMap.put(double.class, value);
        return this;
    }

    public void navigation() {
        if(TextUtils.isEmpty(mPath)) {
            throw new RuntimeException("is not add path !");
        }
        if(mProxy == null) {
            throw new RuntimeException("is not add router interface !");
        }
        String methodName = mPathMap.get(mPath);
        if(methodName != null) {
            int count = mParamsMap.size();
            Class[] parameterTypes = new  Class[count];
            Object[] parameters = new Object[count];
            int index = 0;
            Iterator<Class> iterator = mParamsMap.keySet().iterator();
            while (iterator.hasNext() && index < count) {
                Class type = iterator.next();
                parameterTypes[index] = type;
                parameters[index] = mParamsMap.get(type);
                index ++;
            }
            Method method;
            try {
                method = mProxy.getClass().getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
                method.invoke(mProxy, parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private <T> T proxy(Class<T> aClass) {
        if(mContext == null) {
            throw new RuntimeException("context is not init !");
        }
        return (T)Proxy.newProxyInstance(aClass.getClassLoader(), new Class<?>[]{aClass},
                (proxy, method, args) -> {
                    boolean isUri;
                    RouterPath routerPath = method.getAnnotation(RouterPath.class);
                    String uri;
                    if (routerPath != null) {
                        isUri = false;
                        uri = routerPath.path();
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
                        Class activity = mTargetMap.get(uri);
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

