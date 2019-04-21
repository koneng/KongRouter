package com.kong.router;

import android.content.Intent;

import com.kong.router.interfaces.IChain;
import com.kong.router.interfaces.Interceptor;

import java.util.List;


public class RealChain implements IChain {

    private List<Interceptor> mInterceptors;
    private Intent mPreIntent;

    RealChain(List<Interceptor> interceptors, Intent preIntent) {
        mInterceptors = interceptors;
        mPreIntent = preIntent;
    }

    @Override
    public Intent proceed(Interceptor interceptor) {
        if(mPreIntent == null) {
            return null;
        }
        int index = mInterceptors.indexOf(interceptor);
        int size = mInterceptors.size();
        if (index < size - 1) {
            index++;
            mPreIntent = interceptor.intercept(mPreIntent);
            Interceptor next = mInterceptors.get(index);
            return proceed(next);
        } else {
            return mInterceptors.get(index).intercept(mPreIntent);
        }
    }
}
