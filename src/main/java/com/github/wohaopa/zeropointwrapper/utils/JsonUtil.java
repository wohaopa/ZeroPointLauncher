package com.github.wohaopa.zeropointwrapper.utils;

import cn.hutool.json.JSONUtil;

public class JsonUtil {

    /** 将POJO转换为JSON */
    public static <T> String toJson(T obj) {
        return JSONUtil.parse(obj)
            .toJSONString(4);
    }

    /** 将JSON转为POJO */
    public static <T> T fromJson(String json, Class<T> type) {
        return JSONUtil.toBean(json, type);
    }
}
