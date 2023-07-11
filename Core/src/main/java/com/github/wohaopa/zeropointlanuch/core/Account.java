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

package com.github.wohaopa.zeropointlanuch.core;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.auth.Auth;
import com.github.wohaopa.zeropointlanuch.core.auth.MicrosoftAuth;
import com.github.wohaopa.zeropointlanuch.core.auth.OfflineAuth;

public class Account {

    private static List<Auth> auths;

    private static Auth cur;

    public static List<Auth> getAuths() {
        if (auths == null) {
            auths = new ArrayList<>();
            Config.getConfig();
        }
        return auths;
    }

    public static void add(Auth auth) {
        getAuths().add(auth);
    }

    public static void load(JSONArray array) {

        Iterable<JSONObject> it = array.jsonIter();
        for (JSONObject jsonObject : it) {
            switch (jsonObject.getStr("type")) {
                case "OFFLINE":
                    getAuths().add(new OfflineAuth().loadInformation(jsonObject));
                case "MICROSOFT":
                    getAuths().add(new MicrosoftAuth().loadInformation(jsonObject));
            }
        }
    }

    public static JSONArray save() {
        JSONArray array = new JSONArray();
        getAuths().forEach(auth -> array.add(auth.saveInformation()));
        return array;
    }

    public static void select(Auth value) {
        cur = value;
    }

    public static Auth getCur() {
        return cur;
    }
}
