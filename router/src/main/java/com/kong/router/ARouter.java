package com.kong.router;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kong.router.interfaces.Interceptor;
import com.shopee.router.Constants;
import com.shopee.router.interfaces.IRouterMap;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author kong
 */
public class ARouter {

    private static final String URI_HOST = "airpay://com.shopee.airpay";

    private volatile static ARouter instance;

    private Context mContext;
    private List<Interceptor> mInterceptors = new ArrayList<>();
    private Map<String, Class> mTargetMap = new HashMap<>();
    private Map<String, Map<String, String>> mFieldMap = new HashMap<>();
    private Map<String, Object> mParamsMap = new HashMap<>();
    private String mPath;

    private ARouter() {
        try {
            Class<?> routerMapClass = Class.forName(Constants.ROUTER_MAP_PACKAGE_NAME + "." + Constants.ROUTER_MAP_NAME);
            IRouterMap iRouterMap = (IRouterMap) routerMapClass.newInstance();
            mFieldMap = iRouterMap.loadPathFieldInfo();
            mTargetMap = iRouterMap.loadPathClassInfo();
        } catch (Exception e) {
            //属性map找不到 ，ignore
        }
    }

    public static ARouter get() {
        if (instance == null) {
            synchronized (ARouter.class) {
                if (instance == null) {
                    instance = new ARouter();
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

    public ARouter addInterceptor(Interceptor interceptor) {
        mInterceptors.add(interceptor);
        return this;
    }

    //---------------------------------------------------------------------------------------
    //path调用跳转
    public ARouter path(String path) {
        mPath = path;
        return this;
    }

    //path调用跳转
    public ARouter uri(String uri) {
        if(!uri.contains(URI_HOST)) {
            uri =  URI_HOST + uri;
        }
        Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        Uri u = in.getData();
        mPath = u.getPath();
        Set<String> set = u.getQueryParameterNames();
        for (String key : set) {
            String value = u.getQueryParameter(key);
            mParamsMap.put(key, value);
        }
        return this;
    }

    public void startActivityForUri(String uri) {
        if(!TextUtils.isEmpty(uri)) {
            Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            ComponentName componentName = in.resolveActivity(mContext.getPackageManager());
            if(componentName != null) {
                Intent intent = new Intent();
                intent.setComponent(componentName);
                parseUri(in, intent);
                Intent target = interceptProceed(intent);
                if(target != null) {
                    mContext.startActivity(target);
                }
            }
        }
    }

    private void parseUri(Intent sourceIntent, Intent targetIntent) {
        Uri u = sourceIntent.getData();
        Set<String> set = u.getQueryParameterNames();
        for (String key : set) {
            String value = u.getQueryParameter(key);
            targetIntent.putExtra(key, value);
        }
    }

    public ARouter withInt(String key, int value) {
        mParamsMap.put(key, value);
        return this;
    }

    public ARouter withString(String key, String value) {
        mParamsMap.put(key, value);
        return this;
    }

    public ARouter withObject(String key, Parcelable value) {
        mParamsMap.put(key, value);
        return this;
    }

    public ARouter withObject(String key, Serializable value) {
        mParamsMap.put(key, value);
        return this;
    }

    public ARouter withLong(String key, int value) {
        mParamsMap.put(key, value);
        return this;
    }

    public ARouter withFloat(String key, int value) {
        mParamsMap.put(key, value);
        return this;
    }

    public ARouter withDouble(String key, int value) {
        mParamsMap.put(key, value);
        return this;
    }

    public void inject(Object mInject) {
        try {
            Map<String, String> fieldMap = mFieldMap.get(mPath);
            int count = mParamsMap.size();
            int index = 0;
            Iterator<String> iterator = mParamsMap.keySet().iterator();
            while (iterator.hasNext() && index < count) {
                String key = iterator.next();
                String fieldName = fieldMap.get(key);
                if(!TextUtils.isEmpty(fieldName)) {
                    Field field = mInject.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = mParamsMap.get(key);
                    field.set(mInject, value);
                }
                index++;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            instance.clearCache();
        }
    }

    public void navigation() {
        if(TextUtils.isEmpty(mPath)) {
            throw new RuntimeException("is not add path !");
        }
        Class activity = mTargetMap.get(mPath);
        if(activity != null) {
            Intent target = new Intent();
            Intent intent = interceptProceed(target);
            intent.setClass(mContext, activity);
            mContext.startActivity(intent);
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

    private void clearCache() {
        mParamsMap.clear();
        mPath = "";
    }
}

