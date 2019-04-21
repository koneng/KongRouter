package com.kong.router.interfaces;

import android.content.Intent;

/**
 * @author kong
 */
public interface IAction {

    void onFound(Intent intent);

    void onLost(String uri);
}
