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

import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;

public class Config {

    private static Config config;
    private static File configFile = new File(
        ZplDirectory.getWorkDirectory()
            .getParentFile(),
        "config.json");

    static {
        loadConfig();
        Runtime.getRuntime()
            .addShutdownHook(new Thread(Config::saveConfig));
    }

    public static Config getConfig() {
        return config;
    }

    private static void loadConfig() {
        if (!configFile.exists()) config = new Config();
        else try {
            config = JsonUtil.fromJson(configFile)
                .toBean(Config.class);
        } catch (Exception e) {
            Log.warn("配置文件错误：{}", configFile.toString());
            configFile.delete();
            config = new Config();
        }
    }

    public static void saveConfig() {
        FileUtil.fileWrite(configFile, JsonUtil.toJson(config));
    }

    // private Config() {}

    private String java8Path;
    private String java17Path;

    public String getJava8Path() {
        return java8Path;
    }

    public String getJava17Path() {
        return java17Path;
    }

    public void setJava8Path(String java8Path) {
        this.java8Path = java8Path;
    }

    public void setJava17Path(String java17Path) {
        this.java17Path = java17Path;
    }

}
