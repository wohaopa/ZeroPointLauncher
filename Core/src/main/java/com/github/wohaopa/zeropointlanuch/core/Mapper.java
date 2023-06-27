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
import java.io.IOException;
import java.util.*;

import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.filesystem.MyDirectory;
import com.github.wohaopa.zeropointlanuch.core.filesystem.MyFileBase;
import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;

/** 用于目录映射的核心类，execute()不会递归执行映射，需要在实例方法中调用 */
public class Mapper {

    private final Map<Sharer, MyDirectory> caches = new HashMap<>();
    private MyDirectory myDirectory;
    private final JSONObject config;
    private final Instance instance;

    public Mapper(File configFile, Instance instance) {
        if (configFile != null && configFile.isFile()) config = (JSONObject) JsonUtil.fromJson(configFile);
        else config = null;
        this.instance = instance;
    }

    public void update(Sharer sharer) {
        if (caches.containsKey(sharer)) {
            myDirectory = caches.get(sharer);
        } else myDirectory = (MyDirectory) MyFileBase.getMyFileSystemByFile(instance.runDir, null);
        caches.put(sharer, myDirectory);

        updateMods(); // 映射mod
        updateSharer(sharer);
        updateInstance();

    }

    private void updateInstance() {
        Instance instance1 = instance;
        while (instance1 != null) {
            MyDirectory instanceDirectory = instance1.getMyDirectory();
            MyFileBase.marge(myDirectory, instanceDirectory, getMargeInfo(instance1.information.name));
            instance1 = Instance.get(instance1.information.depVersion);
        }
    }

    private void updateSharer(Sharer sharer) {
        while (sharer != null) {
            MyDirectory sharerDirectory = sharer.getMyDirectory();
            MyFileBase.marge(myDirectory, sharerDirectory, getMargeInfo(sharer.name));
            sharer = Sharer.get(sharer.parent);
        }
    }

    private void updateMods() {
        String modsFileName = "mods" + MyFileBase.separator;
        MyDirectory myMods;
        if (!myDirectory.contains(modsFileName)) myMods = (MyDirectory) myDirectory.addSub(modsFileName);
        else myMods = (MyDirectory) myDirectory.getSub(modsFileName);

        List<String> excludeMods = instance.information.excludeMods;
        Instance instance1 = instance;

        while (instance1 != null) {
            instance1.information.includeMods.forEach(s -> {
                String modName = s.substring(s.lastIndexOf(MyFileBase.separator));
                if (excludeMods != null && !excludeMods.contains(s) && !myMods.contains(modName)) {
                    myMods.addSub(modName)
                        .setTargetForFile(ModMaster.getModFile(s));
                }
            });
            instance1 = Instance.get(instance1.information.depVersion);
        }
    }

    private MyFileBase.MargeInfo getMargeInfo(String name) {

        JSONObject object = config == null ? null : config.getJSONObject(name);
        List include = object == null ? null : object.get("include", List.class);
        List exclude = object == null ? null : object.get("exclude", List.class);
        return new MyFileBase.MargeInfo(include, exclude);
    }

    public void doLink() {
        File linkInfo = new File(instance.insDir, "linkInfo.txt");
        List<Pair<String, String>> list = myDirectory.getMargeFileList();
        StringBuilder stringBuilder = new StringBuilder();
        list.forEach(
            s -> stringBuilder.append(s.getKey())
                .append("->")
                .append(s.getValue())
                .append("\n"));
        FileUtil.fileWrite(linkInfo, stringBuilder.toString());

        try {
            LinkTools.doLink(linkInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
