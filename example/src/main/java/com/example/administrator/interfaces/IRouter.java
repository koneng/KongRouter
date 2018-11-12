package com.example.administrator.interfaces;

import com.kong.router.RouterJumpCallback;
import com.kong.router.RouterParam;
import com.kong.router.RouterUri;

public interface IRouter {

    @RouterUri(uri = "kong://www.kong.com/b_activity")
    void jumpBActivity(@RouterParam("id") String id);

    @RouterUri(uri = "kong://www.kong.com/b_activity")
    void jumpBActivity(@RouterParam("id") String id, RouterJumpCallback callback);
}
