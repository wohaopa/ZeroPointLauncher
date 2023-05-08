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
import java.nio.charset.Charset;
import java.util.*;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.ZipUtil;

public class InstanceInstaller {

    public static Map<String, String> modidMap;

    static {
        modidMap = new HashMap<>();
        String mapStr = ResourceUtil.readStr("zpl-mod-repo.map", Charset.defaultCharset())
            .replace("\r", "");

        String[] mapStrLine = mapStr.split("\n");
        Arrays.stream(mapStrLine)
            .forEach(line -> {
                String[] tmp = line.split("->");
                modidMap.put(tmp[1], tmp[0]);
            });
    }

    /**
     * 启动时默认初始已安装的实例
     *
     * @param versionFile 找到的版本文件
     */
    public static void addInst(File versionFile) {
        Instance inst = Instance.newInstance();
        inst.versionFile = versionFile; // 先准备version.json文件

        try {
            inst.information = inst.loadInformation();
        } catch (Exception e) {
            throw new RuntimeException("版本加载错误：" + e);
        }
        if (!inst.information.insDir.equals(versionFile.getParent())) {
            Log.info("实例文件被移动：{}", inst.information.insDir);
            inst.information.insDir = versionFile.getParent();
            inst.information.imageDir = inst.information.insDir + "\\image";
            inst.information.runDir = inst.information.insDir + "\\.minecraft";
            inst.savaInformation();
        }

        inst.insDir = new File(inst.information.insDir);
        inst.imageDir = new File(inst.information.imageDir);
        inst.runDir = new File(inst.information.runDir);

        Instance.put(inst.information.name, inst);
    }

    /**
     * 安装实例的公共方法 在执行本函数前准备好目录
     *
     * @param dir        实例文件夹
     * @param name       实例名（唯一识别码）
     * @param version    实例版本（不起作用）
     * @param depVersion 依赖的实例名
     */
    private static Instance install(File dir, String name, String version, String depVersion) {

        File versFile = new File(dir, "version.json");
        if (versFile.exists()) throw new RuntimeException("已经存在：" + dir.getName());

        Instance inst = Instance.newInstance();
        inst.information = new Instance.Information();

        inst.information.name = name; // 手动设置
        inst.information.version = version;
        inst.information.depVersion = depVersion;

        inst.insDir = dir;
        inst.versionFile = versFile;
        inst.imageDir = FileUtil.initAndMkDir(dir, "image");
        inst.runDir = FileUtil.initAndMkDir(dir, ".minecraft");

        inst.information.insDir = inst.insDir.toString();
        inst.information.imageDir = inst.imageDir.toString();
        inst.information.runDir = inst.runDir.toString();
        inst.information.sharer = "Common"; // 使用默认共享器

        Log.debug("正在生成校验文件");
        inst.information.checksum = FileUtil.genChecksum(inst.imageDir); // 加载文件校验

        inst.information.includeMods = genModList(new File(inst.imageDir, "mods")); // 加载mods信息
        inst.information.excludeMods = new ArrayList<>();

        inst.savaInformation(); // 保存版本信息

        Instance.put(name, inst);
        return inst;
    }

    /**
     * 安装标准GTNH client压缩包 暂不支持mmc压缩包
     *
     * @param zip     压缩包全路径
     * @param dir     实例文件夹
     * @param name    实例名（唯一识别码）
     * @param version 实例版本（不起作用）
     */
    public static void installStandard(File zip, File dir, String name, String version) {

        Log.start("标准实例安装");
        // 准备好目录
        File image = FileUtil.initAndMkDir(dir, "image");

        Log.debug("开始解压：{}", zip);
        long time1 = System.currentTimeMillis();
        ZipUtil.unCompress(zip, image); // 解压
        long time2 = System.currentTimeMillis();
        Log.debug("解压完成！用时：{}s", (time2 - time1) / 1000);

        install(dir, name, version, "null"); // 创建实例

        // 生成默认config
        List<String> list = new ArrayList<>();
        for (File f : Objects.requireNonNull(image.listFiles())) {
            if (f.getName()
                .endsWith(".md")) {
                list.add(f.getName());
            }
        }

        JSONObject json = Mapper.defaultJson();
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("name", name);
        jsonObject.putOpt("file", list);
        json.get("exclude", JSONArray.class)
            .add(jsonObject);

        File config = new File(image, "zpl_margi_config.json");
        FileUtil.fileWrite(config, JsonUtil.toJson(json));

        Log.info("实例 {} 安装完成！", name);
        Log.end();
    }

    /**
     * 使用versionJsonFile安装实例，请提供除mods以外的资源文件
     *
     * @param versionJsonFile
     * @param dir
     */
    public static void installVersionJson(File versionJsonFile, File dir) {}

    /**
     * 使用升级器升级实例
     *
     * @param updaterFile   更新器文件
     * @param dir           工作目录
     * @param name          实例名（唯一识别码）
     * @param version       实例版本（不起作用）
     * @param targetVersion 依赖的实例名
     */
    public static void installUpdate(File updaterFile, File dir, String name, String version, String targetVersion) {}

    public static void installTranslation(File translationFile, File dir, String name, String version,
        Instance targetVersion) {
        Log.start("汉化实例安装");

        File image = FileUtil.initAndMkDir(dir, "image");

        Log.debug("开始解压：{}", translationFile);
        long time1 = System.currentTimeMillis();
        ZipUtil.unCompress(translationFile, image); // 解压
        long time2 = System.currentTimeMillis();
        Log.debug("解压完成！用时：{}s", (time2 - time1) / 1000);

        Instance inst = install(dir, name, version, targetVersion.information.name);
        File depImageDir = targetVersion.imageDir;

        for (File file : Objects.requireNonNull(inst.imageDir.listFiles())) {
            if (file.isDirectory()) {
                for (File file1 : Objects.requireNonNull(file.listFiles())) {
                    if (file.isDirectory()) {
                        File depFile = new File(depImageDir, file.getName() + "/" + file1.getName());
                        Log.debug("复制目录：{} 到：{}", depFile, file1);
                        FileUtil.copyDir(depFile, file1.getParentFile());
                    }
                }
            }
        }
        Log.end();
    }

    private static List<String> genModList(File modsDir) {
        List<String> mods = new ArrayList<>();
        if (!modsDir.exists()) return mods;
        for (File mod : Objects.requireNonNull(modsDir.listFiles())) {
            if (mod.isFile()) {
                String modFileName = mod.getName();
                String modRepo = "_default";
                for (String key : modidMap.keySet()) {
                    if (modFileName.startsWith(key)) {
                        modRepo = modidMap.get(key);
                        break;
                    }
                }
                String path = modRepo + "/" + modFileName;
                mods.add(path);
                if (!FileUtil.moveFile(mod, new File(DirTools.modsDir, path))) {
                    Log.info("已在mod库中发现：{} 即将删除临时文件", modFileName);
                    if (mod.delete()) Log.warn("文件：{} 删除失败，可能被占用，请手动删除", mod.getPath());
                }
            }
        }
        return mods;
    }
}
