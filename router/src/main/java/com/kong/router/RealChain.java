package com.kong.router;

import android.content.Intent;
import com.kong.router.interfaces.Chain;
import com.kong.router.interfaces.Interceptor;
import java.util.List;

public class RealChain implements Chain {

    private List<Interceptor> mInterceptors;

    RealChain(List<Interceptor> interceptors) {
        mInterceptors = interceptors;
    }

    @Override
    public Intent proceed(Interceptor interceptor) {
        int index = mInterceptors.indexOf(interceptor);
        if (index < mInterceptors.size() - 1) {
            index++;
            interceptor.intercept();
            return proceed(mInterceptors.get(index));
        } else if (index == mInterceptors.size() - 1) {
            return mInterceptors.get(index).intercept();
        } else {
            return null;
        }
    }
}
