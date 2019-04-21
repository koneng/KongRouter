package com.kong.router.interfaces;

import android.content.Intent;

public interface IChain {
    Intent proceed(Interceptor interceptor);
}
