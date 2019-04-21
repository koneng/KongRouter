package com.example.administrator.interfaces;

import android.app.Activity;

import com.example.administrator.UserBean;
import com.kong.router.annotation.RequestCode;
import com.kong.router.interfaces.IAction;
import com.kong.router.annotation.RouterParam;
import com.kong.router.annotation.RouterPath;
import com.kong.router.annotation.RouterUri;

public interface IRouter {

    @RouterPath("b/activity")
    void jumpB_Activity(@RouterParam("id") String id);

    @RouterPath("kong://www.kong.com/b_activity")
    void jumpB_Activity(@RouterParam("id") String id, IAction actionProxy);

    @RouterPath("kong://www.kong.com/b_activity")
    void jumpB_Activity(@RouterParam("user") UserBean user);

    @RouterUri("kong://www.kong.com/b_activity?id=100000c")
    void jumpB_Activity();

    @RouterUri("kong://www.kong.com/b_activity?id=100000c")
    void jumpB_Activity(IAction actionProxy);

    @RouterPath("kong://www.kong.com/b_activity")
    void jumpB_Activity(Activity context, @RequestCode int requestCode);


    void jump(String url);



}
