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

package com.github.wohaopa.zeropointlanuch.core.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSONObject;

public abstract class Auth {

    protected String auth_player_name;
    protected String auth_uuid;
    protected String auth_access_token;
    protected String user_properties;
    protected UserType user_type;

    private final Map<String, String> var = new HashMap<>();

    public Auth() {}

    protected abstract void login();

    public abstract JSONObject saveInformation();

    public abstract Auth loadInformation(JSONObject object);

    public List<String> parseArg(List<String> args) {
        login();

        var.put("auth_player_name", auth_player_name);
        var.put("auth_uuid", auth_uuid);
        var.put("auth_access_token", auth_access_token);
        var.put("user_properties", user_properties);
        var.put("user_type", String.valueOf(user_type));

        for (String key : var.keySet()) {
            int index = args.indexOf("${" + key + "}");
            if (index >= 0) args.set(index, var.get(key));
        }

        return args;
    }

    public String getName() {
        return auth_player_name;
    }

    public UserType getUserType() {
        return user_type;
    }

    @Override
    public String toString() {
        return auth_player_name + "(" + user_type + ")";
    }
}
