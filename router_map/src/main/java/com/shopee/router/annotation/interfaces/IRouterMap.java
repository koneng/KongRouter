package com.shopee.router.annotation.interfaces;

import java.util.Map;

public interface IRouterMap {
    Map<String, Class> loadInfo();
}
