package com.example.administrator.interfaces;

import android.content.Intent;

import com.kong.router.interfaces.RouterJumpHandler;
import com.kong.router.annotation.RouterParam;
import com.kong.router.annotation.RouterPath;
import com.kong.router.annotation.RouterUri;

public interface IRouter {

    @RouterPath("kong://www.kong.com/b_activity")
    void jumpBActivity(@RouterParam("id") String id);

    @RouterPath("kong://www.kong.com/b_activity")
    void jumpBActivity(@RouterParam("id") String id, RouterJumpHandler handler);

    @RouterPath("kong://www.kong.com/b_activity")
    void jumpBActivity(@RouterParam("user") Intent user);

    @RouterUri("kong://www.kong.com/b_activity?id=100000c")
    void jumpBActivity();

    @RouterUri("kong://www.kong.com/b_activity?id=100000c")
    void jumpBActivity(RouterJumpHandler handler);
}
