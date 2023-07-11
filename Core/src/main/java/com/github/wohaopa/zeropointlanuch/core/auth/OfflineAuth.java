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

import java.util.UUID;

import cn.hutool.json.JSONObject;

public class OfflineAuth extends Auth {

    public OfflineAuth() {
        type = "OFFLINE";
    }

    public OfflineAuth(String name) {
        this();

        auth_player_name = name;
        auth_uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + auth_player_name).getBytes())
            .toString()
            .replace("-", "");
        user_properties = "{}";
        user_type = "Offline";
    }

    @Override
    protected void login() {

        auth_access_token = UUID.randomUUID().toString().replace("-", "");

    }

    @Override
    public JSONObject saveInformation() {
        return new JSONObject().putOpt("type", type).putOpt("name", auth_player_name).putOpt("uuid", auth_uuid);
    }

    @Override
    public Auth loadInformation(JSONObject object) {
        auth_player_name = object.getStr("name");
        auth_uuid = object.getStr("uuid");
        user_properties = "{}";
        user_type = "Offline";
        return this;
    }

}
