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

import java.util.function.Consumer;

import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.auth.controller.MicrosoftAuthController;

public class MicrosoftAuth extends Auth {

    private final MicrosoftAuthController controller;

    public MicrosoftAuth() {
        user_properties = "{}";
        user_type = UserType.Microsoft;
        controller = new MicrosoftAuthController();
    }

    @Override
    protected void login() {
        controller.login();

        auth_player_name = controller.minecraftProfile.getStr("name");
        auth_uuid = controller.minecraftProfile.getStr("id");
        auth_access_token = controller.minecraftToken;
    }

    @Override
    public JSONObject saveInformation() {
        return new JSONObject().putOpt("refresh_token", controller.microsoftRefreshToken)
            .putOpt("access_token", controller.minecraftToken)
            .putOpt("name", auth_player_name)
            .putOpt("uuid", auth_uuid)
            .putOpt("type", user_type);
    }

    @Override
    public Auth loadInformation(JSONObject object) {
        controller.microsoftRefreshToken = object.getStr("refresh_token");
        auth_access_token = controller.minecraftToken = object.getStr("access_token");
        auth_uuid = object.getStr("uuid");
        auth_player_name = object.getStr("name");
        return this;
    }

    public void loginFist(Consumer<JSONObject> callback) {
        controller.loginFist(callback);
        auth_player_name = controller.minecraftProfile.getStr("name");
        auth_uuid = controller.minecraftProfile.getStr("id");
        auth_access_token = controller.minecraftToken;
    }
}
