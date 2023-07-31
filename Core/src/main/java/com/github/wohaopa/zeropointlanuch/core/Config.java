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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.auth.Auth;
import com.github.wohaopa.zeropointlanuch.core.auth.MicrosoftAuth;
import com.github.wohaopa.zeropointlanuch.core.auth.OfflineAuth;
import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;

public class Config {

    private static Config config;
    private static final File configFile = new File(ZplDirectory.getWorkDirectory().getParentFile(), "config.json");

    public static Config getConfig() {
        if (config == null) {
            config = new Config();
            loadConfig();
            Runtime.getRuntime().addShutdownHook(new Thread(Config::saveConfig));
        }

        return config;
    }

    private static void loadConfig() {
        Log.debug("正在加载设置文件：{}", configFile.toString());
        if (configFile.exists()) try {
            JSONObject object = JsonUtil.fromJson(configFile).toBean(JSONObject.class);

            config.libraries_url = object.getStr("libraries_url", "http://downloads.wohaopa.cn");
            config.launchConfig = object.getJSONObject("launchConfig");
            if (config.launchConfig == null) config.launchConfig = new JSONObject();
            config.auths = new ArrayList<>();

            Iterable<JSONObject> it = object.getJSONArray("account").jsonIter();
            for (JSONObject jsonObject : it) {
                switch (jsonObject.getStr("type")) {
                    case "offline" -> config.auths.add(new OfflineAuth().loadInformation(jsonObject));
                    case "microsoft" -> config.auths.add(new MicrosoftAuth().loadInformation(jsonObject));
                }
            }

        } catch (Exception e) {
            Log.warn("配置文件错误：{}", configFile.toString());
            configFile.delete();
            FileUtil.fileWrite(configFile, "{}");
        }
    }

    private static void saveConfig() {
        Log.debug("正在保存设置文件：{}", configFile.toString());

        JSONObject jsonObject = new JSONObject();
        jsonObject.set("libraries_url", config.libraries_url);
        jsonObject.set("launchConfig", config.launchConfig);
        JSONArray array = new JSONArray();
        config.auths.forEach(auth -> array.add(auth.saveInformation()));

        jsonObject.set("account", array);

        FileUtil.fileWrite(configFile, jsonObject.toJSONString(4));
    }

    public void addAccount(Auth auth) {
        auths.add(auth);
    }

    public List<Auth> getAuths() {
        return auths;
    }

    private Config() {}

    private String libraries_url;
    private JSONObject launchConfig;

    private List<Auth> auths;

    private Auth curAccount;

    public void select(Auth value) {
        curAccount = value;
    }

    public Auth getCur() {
        return curAccount;
    }

    public String getLibraries_url() {
        return libraries_url;
    }

    public void setLibraries_url(String libraries_url) {
        this.libraries_url = libraries_url;
    }

    public JSONObject getLaunchConfig(String name) {
        JSONObject jsonObject = launchConfig.getJSONObject(name);
        if (jsonObject == null) {
            jsonObject = new JSONObject();
            launchConfig.set(name, jsonObject);
        }
        return jsonObject;
    }
}
