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

package com.github.wohaopa.zeropointlanuch.core.auth.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.auth.MSAuthenticationException;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.StringUtil;

public class MicrosoftAuthController {

    private static final String ZPL_CLIENT_ID = "d5b49040-8f93-484d-9b1b-5138159c2288";
    private static final int timeout = -1;

    private String deviceCode;
    private String microsoftAccessToken;
    public String microsoftRefreshToken;
    private String xboxUserToken;
    private String xboxXstsToken;
    private String xboxUhs;
    public String minecraftToken;

    public JSONObject minecraftProfile;

    private JSONObject getMicrosoftDeviceCode() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("client_id", ZPL_CLIENT_ID);
        paramMap.put("scope", "XboxLive.signin offline_access");
        HttpResponse response;
        try {
            response = HttpRequest.post("https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode")
                .form(paramMap)
                .timeout(timeout)
                .execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (response.isOk()) {
            JSONObject resultJson = (JSONObject) JsonUtil.fromJson(response.body());
            deviceCode = resultJson.getStr("device_code");
            return resultJson;
        }
        Log.error(response.body());
        throw new RuntimeException("失败：无法正确获取微软用户登录码");
    }

    private JSONObject getMicrosoftToken() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("client_id", ZPL_CLIENT_ID);
        paramMap.put("grant_type", "urn:ietf:params:oauth:grant-type:device_code");
        paramMap.put("device_code", deviceCode);

        HttpResponse response;
        try {
            response = HttpRequest.post("https://login.microsoftonline.com/consumers/oauth2/v2.0/token")
                .form(paramMap)
                .timeout(timeout)
                .execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (response.isOk() || response.getStatus() == 400) {

            JSONObject resultJson = (JSONObject) JsonUtil.fromJson(response.body());

            if (resultJson.containsKey("error")) {
                if ("authorization_pending".equals(resultJson.getStr("error"))) return null;
                throw new RuntimeException("无法获取微软access_token");
            }

            microsoftAccessToken = resultJson.getStr("access_token");
            microsoftRefreshToken = resultJson.getStr("refresh_token");
            return resultJson;
        }
        Log.error(response.body());
        throw new RuntimeException("失败：无法正确获取微软访问令牌");
    }

    private JSONObject refreshMicrosoftToken() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("client_id", ZPL_CLIENT_ID);
        paramMap.put("grant_type", "refresh_token");
        paramMap.put("refresh_token", microsoftRefreshToken);

        HttpResponse response;
        try {
            response = HttpRequest.post("https://login.microsoftonline.com/consumers/oauth2/v2.0/token")
                .form(paramMap)
                .timeout(timeout)
                .execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (response.isOk() || response.getStatus() == 400) {

            JSONObject resultJson = (JSONObject) JsonUtil.fromJson(response.body());

            if (resultJson.containsKey("error")) {
                if ("authorization_pending".equals(resultJson.getStr("error"))) return null;
                throw new RuntimeException("无法获取微软access_token");
            }

            microsoftAccessToken = resultJson.getStr("access_token");
            microsoftRefreshToken = resultJson.getStr("refresh_token");
            return resultJson;
        }
        Log.error(response.body());
        throw new RuntimeException("失败：无法正确获取微软访问令牌");
    }

    private JSONObject getXboxUserToken() {
        JSONObject param = new JSONObject();
        param.set(
            "Properties",
            new JSONObject().putOpt("AuthMethod", "RPS")
                .putOpt("SiteName", "user.auth.xboxlive.com")
                .putOpt("RpsTicket", "d=" + microsoftAccessToken));
        param.set("RelyingParty", "http://auth.xboxlive.com");
        param.set("TokenType", "JWT");

        HttpResponse response;
        try {
            response = HttpRequest.post("https://user.auth.xboxlive.com/user/authenticate")
                .body(param.toJSONString(0))
                .timeout(timeout)
                .execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (response.isOk()) {
            JSONObject resultJson = (JSONObject) JsonUtil.fromJson(response.body());

            xboxUserToken = resultJson.getStr("Token");
            return resultJson;
        }
        Log.error(param.toJSONString(0));
        Log.error(response.body());
        throw new RuntimeException("失败：无法正确获取xbox用户令牌");
    }

    private JSONObject getXboxXstsToken() {

        JSONObject param = new JSONObject();
        param.set(
            "Properties",
            new JSONObject().putOpt("SandboxId", "RETAIL").putOpt("UserTokens", new JSONArray().put(xboxUserToken)));
        param.set("RelyingParty", "rp://api.minecraftservices.com/");
        param.set("TokenType", "JWT");

        HttpResponse response;
        try {
            response = HttpRequest.post("https://xsts.auth.xboxlive.com/xsts/authorize")
                .body(param.toJSONString(0))
                .timeout(timeout)
                .execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (response.isOk()) {
            JSONObject resultJson = (JSONObject) JsonUtil.fromJson(response.body());

            xboxUhs = resultJson.getByPath("DisplayClaims.xui.0.uhs", String.class);
            xboxXstsToken = resultJson.getStr("Token");
            return resultJson;
        }
        Log.error(param.toJSONString(0));
        Log.error(response.body());
        throw new RuntimeException("失败：无法正确获取XboxXsts令牌");
    }

    private JSONObject getMinecraftToken() {

        JSONObject param = new JSONObject();
        param.set("identityToken", String.format("XBL3.0 x=%s;%s", xboxUhs, xboxXstsToken));

        HttpResponse response;
        try {
            response = HttpRequest.post("https://api.minecraftservices.com/authentication/login_with_xbox")
                .body(param.toJSONString(0))
                .timeout(timeout)
                .execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (response.isOk()) {
            JSONObject resultJson = (JSONObject) JsonUtil.fromJson(response.body());

            minecraftToken = resultJson.getStr("access_token");
            return resultJson;
        }
        Log.error(param.toJSONString(0));
        Log.error(response.body());
        throw new RuntimeException("失败：无法正确获取Minecraft令牌");
    }

    private JSONObject getMinecraftProfile() {

        HttpResponse response;
        try {
            response = HttpRequest.get("https://api.minecraftservices.com/minecraft/profile")
                .header(Header.AUTHORIZATION, String.format("Bearer %s", minecraftToken))
                .timeout(timeout)
                .execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (response.isOk()) {
            minecraftProfile = (JSONObject) JsonUtil.fromJson(response.body());

            return minecraftProfile;
        }
        Log.error(response.body());
        throw new RuntimeException("失败：无法正确获取Minecraft令牌");
    }

    public void login() {
        if (StringUtil.isNotEmpty(minecraftToken)) try {
            getMinecraftProfile();
            return;
        } catch (Exception ignored) {}

        if (StringUtil.isNotEmpty(microsoftRefreshToken)) try {
            refreshMicrosoftToken();
            getXboxUserToken();
            getXboxXstsToken();
            getMinecraftToken();
            getMinecraftProfile();
            return;
        } catch (Exception ignored) {}

        throw new MSAuthenticationException();
    }

    public void loginFist(Consumer<JSONObject> callback) {
        JSONObject resultMicrosoftDeviceCode = getMicrosoftDeviceCode();
        callback.accept(resultMicrosoftDeviceCode);

        long expireTime = TimeUnit.SECONDS.toMillis(resultMicrosoftDeviceCode.getInt("expires_in"))
            + System.currentTimeMillis();

        JSONObject resultMicrosoftToken;
        try {
            while (System.currentTimeMillis() < expireTime) {
                Thread.sleep(TimeUnit.SECONDS.toMillis(resultMicrosoftDeviceCode.getInt("interval")));
                resultMicrosoftToken = getMicrosoftToken();
                if (resultMicrosoftToken != null) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        getXboxUserToken();
        getXboxXstsToken();
        getMinecraftToken();
        getMinecraftProfile();
    }
}
