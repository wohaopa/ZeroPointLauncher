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
import java.util.*;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;

/** 用于目录映射的核心类，execute()不会递归执行映射，需要在实例方法中调用 */
public class Mapper {

    private final Map<String, List<String>> exclude = new HashMap<>();
    private final Map<String, List<String>> include = new HashMap<>();

    // 第一个String是key，第二个List是value
    private final Map<String, _Item_Name> fileToName = new HashMap<>();
    private final Map<String, List<String>> nameToFile = new HashMap<>();

    private final List<String> loadedMod = new ArrayList<>();
    private List<String> all_exclude;
    private final List<String> exclude_mods;
    // private final List<String> all_include; // 逻辑上重复

    private Sharer sharer;

    private final File runDir;
    private final File mainConfig;
    private final Instance instance;

    public Mapper(Instance instance) {
        this.instance = instance;
        this.runDir = instance.runDir;
        this.mainConfig = new File(instance.imageDir, "zpl_margi_config.json");
        this.exclude_mods = instance.information.excludeMods;
        this.sharer = Sharer.get(instance.information.sharer);

        refresh(null);
    }

    public void refresh(Sharer sharer) {

        this.exclude.clear();
        this.include.clear();
        this.fileToName.clear();
        this.nameToFile.clear();
        this.loadedMod.clear();

        Sharer oldSharer = null;
        if (sharer != null) {
            oldSharer = this.sharer;
            this.sharer = sharer;
        }

        List<String> all = new ArrayList<>();
        all.add("zpl_margi_config.json");
        this.exclude.put("__zpl_all__", all);
        this.loadConfigJson();
        // this.all_include = include.getOrDefault("__zpl_all__", new ArrayList<>());
        this.all_exclude = exclude.get("__zpl_all__");

        this.genMapData();

        if (oldSharer != null) {
            this.sharer = oldSharer;
        }
    }

    public List<String> getLoadedMod() {
        return loadedMod;
    }

    private void genMapData() {

        // 先审查runDir的内容，需要清理所有symlink才可以
        List<String> runDirFiles = new ArrayList<>();
        String name = "__.minecraft__";
        int index = this.runDir.toString()
            .length() + 1;
        for (File file : Objects.requireNonNull(this.runDir.listFiles())) {
            if (!FileUtil.isSymLink(file)) {
                if (file.isDirectory()) {
                    for (File file1 : Objects.requireNonNull(file.listFiles())) {
                        String s = file1.toString()
                            .substring(index);
                        runDirFiles.add(s);
                        fileToName.put(s, new _Item_Name(name, file1));
                    }
                } else {
                    String s = file.toString()
                        .substring(index);
                    runDirFiles.add(s);
                    fileToName.put(s, new _Item_Name(name, file));
                }
            }
        }
        nameToFile.put(name, runDirFiles);
        // 执行sharer映射
        Sharer curSharer = sharer;
        do {
            genMapData0(curSharer.rootDir, curSharer.name);
            curSharer = Sharer.get(curSharer.parent);
        } while (curSharer != null);

        // 执行img映射
        String s = "null";
        Instance curr = instance;
        while (curr != null) {
            genMapData0(curr.imageDir, curr.information.name);
            genMapData1(curr.information.includeMods);
            s = curr.information.depVersion;
            curr = Instance.get(s);
        }
        if (!s.equals("null")) throw new RuntimeException("未找到实例：" + s);

        // 注入mod
        for (String mod : loadedMod) {
            String p = "mods\\" + ModMaster.getModFileName(mod);
            if (fileToName.containsKey(p)) throw new RuntimeException("不应在mods文件夹中存放GTNH的mod！");
            fileToName.put(p, new _Item_Name("__zpl_mod__", new File(DirTools.workDir, mod)));
        }
    }

    private void genMapData0(File imgDir, String name) {

        List<String> includeList = include.get(name);
        List<String> excludeList = exclude.get(name);
        if (excludeList != null && excludeList.size() == 0) return;// 排除版本

        File configFile = new File(imgDir, "zpl_margi_config.json");
        List shareDirList = ((JSONObject) JsonUtil.fromJson(configFile)).get("shareDir", List.class);

        List<String> runDirFiles = new ArrayList<>();
        int index = imgDir.toString()
            .length() + 1;
        for (File file : Objects.requireNonNull(imgDir.listFiles())) {
            String t = file.toString()
                .substring(index);
            if (all_exclude.contains(t) || (excludeList != null && excludeList.contains(t))) continue;

            if (file.isDirectory() && !shareDirList.contains(t)) {
                for (File file1 : Objects.requireNonNull(file.listFiles())) {
                    String t1 = file1.toString()
                        .substring(index);
                    if (all_exclude.contains(t1) || (excludeList != null && excludeList.contains(t1))) continue;

                    runDirFiles.add(t1);
                    _Item_Name tmpItem = fileToName.get(t1);
                    if (tmpItem == null || (includeList != null && includeList.contains(t1)))
                        this.fileToName.put(t1, new _Item_Name(name, file1));
                    else tmpItem.list.add(name);

                }
            } else {

                runDirFiles.add(t);
                _Item_Name tmpItem = fileToName.get(t);
                if (tmpItem == null || (includeList != null && includeList.contains(t)))
                    this.fileToName.put(t, new _Item_Name(name, file));
                else tmpItem.list.add(name);

            }
        }
        this.nameToFile.put(name, runDirFiles);

    }

    private void genMapData1(List<String> list) {
        list.forEach(s -> {
            if (!loadedMod.contains(s) && !exclude_mods.contains(s)) {
                loadedMod.add(s);
            }
        });
    }

    public void makeSymlink() {

        fileToName.forEach((s, item) -> {
            if (!"__.minecraft__".equals(item.list.get(0))) FileUtil.makeSymlink(new File(runDir, s), item.file);

        });

    }

    /** 用于解析json文件 */
    public void loadConfigJson() {
        JSONObject json = (JSONObject) JsonUtil.fromJson(mainConfig);
        List<_Config> exclude1 = json.getBeanList("exclude", _Config.class);
        exclude1.forEach(config -> {
            if (config.name == null) config.name = "__zpl_all__";
            List<String> list = exclude.computeIfAbsent(config.name, k -> new ArrayList<>());
            if (config.file != null) list.addAll(config.file);
        });

        List<_Config> include1 = json.getBeanList("include", _Config.class);
        include1.forEach(config -> {
            if (config.name == null) config.name = "__zpl_all__";
            List<String> list = include.computeIfAbsent(config.name, k -> new ArrayList<>());
            if (config.file != null) list.addAll(config.file);
        });
    }

    public static JSONObject defaultJson() {
        JSONObject object = new JSONObject();
        object.putOpt("include", new JSONArray());
        object.putOpt("exclude", new JSONArray());
        object.putOpt("shareDir", new ArrayList<>());
        return object;
    }

    public static void saveConfigJson(File image, JSONObject json) {
        File config = new File(image, "zpl_margi_config.json");
        FileUtil.fileWrite(config, JsonUtil.toJson(json));
    }

    private static class _Item_Name {

        List<String> list = new ArrayList<>();
        File file;

        public _Item_Name(String name, File file) {
            this.list.add(name);
            this.file = file;
        }

    }

    public static class _Config {

        String name;
        List<String> file;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getFile() {
            return file;
        }

        public void setFile(List<String> file) {
            this.file = file;
        }
    }
}
