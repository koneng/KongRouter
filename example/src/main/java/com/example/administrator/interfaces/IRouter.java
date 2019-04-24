package com.example.administrator.interfaces;

import android.app.Activity;

import com.example.administrator.UserBean;
import com.shopee.router.annotation.RequestCode;
import com.kong.router.interfaces.IAction;
import com.shopee.router.annotation.RouterParam;
import com.shopee.router.annotation.RouterUri;
import com.shopee.router.annotation.RouterPath;

public interface IRouter {

    @RouterPath(path = "b/activity")
    void jumpB_Activity(@RouterParam("id") String id);

    @RouterPath(path = "kong://www.kong.com/b_activity")
    void jumpB_Activity(@RouterParam("id") String id, IAction actionProxy);

    @RouterPath(path = "kong://www.kong.com/b_activity")
    void jumpB_Activity(@RouterParam("user") UserBean user);

    @RouterUri("kong://www.kong.com/b_activity?xxx=dddd")
    void jumpB_Activity();

    @RouterUri("kong://www.kong.com/b_activity?id=100000c")
    void jumpB_Activity(IAction actionProxy);

    @RouterPath(path = "kong://www.kong.com/b_activity")
    void jumpB_Activity(Activity context, @RequestCode int requestCode);


    @RouterPath(path = "main/mainActivity")
    void jumpMainActivity(@RouterParam("user") UserBean user);

    @RouterPath(path = "main/mainActivity")
    void jumpMainActivity(@RouterParam("userId") int id);

}
