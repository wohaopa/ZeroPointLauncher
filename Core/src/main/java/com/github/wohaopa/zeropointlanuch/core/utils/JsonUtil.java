/*
 * MIT License
 * Copyright (c) 2023 初夏同学
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.wohaopa.zeropointlanuch.core.utils;

import java.io.File;
import java.nio.charset.Charset;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;

public class JsonUtil {

    /**
     * 对象->字符串
     *
     * @param obj 对象
     * @return json字符串
     * @param <T> json对象类型
     */
    public static <T> String toJson(T obj) {
        return JSONUtil.parse(obj)
            .toJSONString(4);
    }

    /**
     * 字符串->对象
     *
     * @param json json字符串
     * @param type 类型
     * @return json对象
     * @param <T> json对象的类型
     */
    public static <T> T fromJson(String json, Class<T> type) {
        return JSONUtil.toBean(json, type);
    }

    /**
     * 加载json文件
     *
     * @param jsonFile json文件
     * @return JSON抽象对象
     */
    public static JSON fromJson(File jsonFile) {
        return JSONUtil.readJSON(jsonFile, Charset.defaultCharset());
    }
}
