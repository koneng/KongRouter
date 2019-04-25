package com.shopee.router.interfaces;

import java.util.Map;

public interface IRouterMap {

    Map<String, Map<String, String>> loadPathFieldInfo();

    Map<String, Class> loadPathClassInfo();

}
