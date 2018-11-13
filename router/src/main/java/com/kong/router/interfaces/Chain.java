package com.kong.router.interfaces;

import android.content.Intent;

public interface Chain {
    Intent proceed(Interceptor interceptor);
}
