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

import java.util.*;

public class InstanceInstaller {

    // public static void installForZip(File zip, File instanceDir, Instance.Information information) {
    // Log.start("实例安装");
    // instanceDir.mkdirs();
    // File versionJson = new File(instanceDir, "version.json");
    //
    // Instance instance = Instance.newInstance();
    // instance.versionFile = versionJson;
    // instance.information = information;
    // instance.insDir = instanceDir;
    // instance.imageDir = FileUtil.initAndMkDir(instanceDir, "image");
    // instance.runDir = FileUtil.initAndMkDir(instanceDir, ".minecraft");
    //
    // Log.debug("开始解压：{}", zip);
    // long time1 = System.currentTimeMillis();
    // // ZipUtil.unCompress(zip, instance.imageDir); // 解压
    // long time2 = System.currentTimeMillis();
    // Log.debug("解压完成！用时：{}s", (time2 - time1) / 1000);
    //
    // Log.debug("正在生成校验文件");
    // instance.information.checksum = FileUtil.genChecksum(instance.imageDir); // 加载文件校验
    //
    // instance.information.includeMods = genModList(new File(instance.imageDir, "mods"), true); // 加载mods信息
    // instance.information.excludeMods = new ArrayList<>();
    //
    // Log.debug("正在生成映射配置");
    // List<String> list = new ArrayList<>();
    // for (File f : Objects.requireNonNull(instance.imageDir.listFiles())) {
    // if (f.getName()
    // .endsWith(".md")) {
    // list.add(f.getName());
    // }
    // }
    //
    // JSONObject json = Mapper.defaultJson();
    // JSONObject jsonObject = new JSONObject();
    // jsonObject.putOpt("name", instance.information.name);
    // jsonObject.putOpt("file", list);
    // json.get("exclude", JSONArray.class)
    // .add(jsonObject);
    // Mapper.saveConfigJson(instance.imageDir, json);
    //
    // Log.debug("正在生成保存文件");
    // instance.savaInformation(); // 保存文件
    //
    // Instance.put(instance.information.name, instance);
    // Log.end();
    // }
    //
    // /**
    // * 安装实例的公共方法 在执行本函数前准备好目录
    // *
    // * @param dir 实例文件夹
    // * @param name 实例名（唯一识别码）
    // * @param version 实例版本（不起作用）
    // * @param depVersion 依赖的实例名
    // */
    // @Deprecated
    // private static Instance install(File dir, String name, String version, String depVersion) {
    //
    // File versFile = new File(dir, "version.json");
    // if (versFile.exists()) throw new RuntimeException("已经存在：" + dir.getName());
    //
    // Instance inst = Instance.newInstance();
    // inst.information = new Instance.Information();
    //
    // inst.information.name = name; // 手动设置
    // inst.information.version = version;
    // inst.information.depVersion = depVersion;
    //
    // inst.insDir = dir;
    // inst.versionFile = versFile;
    // inst.imageDir = FileUtil.initAndMkDir(dir, "image");
    // inst.runDir = FileUtil.initAndMkDir(dir, ".minecraft");
    //
    // inst.information.sharer = "Common"; // 使用默认共享器
    //
    // Log.debug("正在生成校验文件");
    // inst.information.checksum = FileUtil.genChecksum(inst.imageDir); // 加载文件校验
    //
    // inst.information.includeMods = genModList(new File(inst.imageDir, "mods"), true); // 加载mods信息
    // inst.information.excludeMods = new ArrayList<>();
    //
    // inst.savaInformation(); // 保存版本信息
    //
    // Instance.put(name, inst);
    // return inst;
    // }
    //
    // /**
    // * 安装标准GTNH client压缩包 暂不支持mmc压缩包
    // *
    // * @param zip 压缩包全路径
    // * @param dir 实例文件夹
    // * @param name 实例名（唯一识别码）
    // * @param version 实例版本（不起作用）
    // */
    // @Deprecated
    // public static void installStandard(File zip, File dir, String name, String version) {
    //
    // // 准备好目录
    // File image = FileUtil.initAndMkDir(dir, "image");
    //
    // Log.debug("开始解压：{}", zip);
    // long time1 = System.currentTimeMillis();
    // // ZipUtil.unCompress(zip, image); // 解压
    // long time2 = System.currentTimeMillis();
    // Log.debug("解压完成！用时：{}s", (time2 - time1) / 1000);
    //
    // install(dir, name, version, "null"); // 创建实例
    //
    // // 生成默认config
    // List<String> list = new ArrayList<>();
    // for (File f : Objects.requireNonNull(image.listFiles())) {
    // if (f.getName()
    // .endsWith(".md")) {
    // list.add(f.getName());
    // }
    // }
    //
    // JSONObject json = Mapper.defaultJson();
    // JSONObject jsonObject = new JSONObject();
    // jsonObject.putOpt("name", name);
    // jsonObject.putOpt("file", list);
    // json.get("exclude", JSONArray.class)
    // .add(jsonObject);
    // Mapper.saveConfigJson(image, json);
    //
    // Log.info("实例 {} 安装完成！", name);
    // }
    //
    // /**
    // * 使用versionJsonFile安装实例，请提供除mods以外的资源文件
    // *
    // * @param versionJsonFile
    // * @param dir
    // */
    // @Deprecated
    // public static void installVersionJson(File versionJsonFile, File dir) {}
    //
    // /**
    // * 使用升级器升级实例
    // *
    // * @param updaterFile 更新器文件
    // * @param dir 工作目录
    // * @param name 实例名（唯一识别码）
    // * @param version 实例版本（不起作用）
    // * @param targetVersion 依赖的实例名
    // */
    // @Deprecated
    // public static void installUpdate(File updaterFile, File dir, String name, String version, String targetVersion)
    // {}
    //
    // @Deprecated
    // public static void installTranslation(File translationFile, File dir, String name, String version,
    // Instance targetVersion) {
    //
    // File image = FileUtil.initAndMkDir(dir, "image");
    //
    // Log.debug("开始解压：{}", translationFile);
    // long time1 = System.currentTimeMillis();
    // // ZipUtil.unCompress(translationFile, image); // 解压
    // long time2 = System.currentTimeMillis();
    // Log.debug("解压完成！用时：{}s", (time2 - time1) / 1000);
    //
    // Instance inst = install(dir, name, version, targetVersion.information.name);
    // File depImageDir = targetVersion.imageDir;
    //
    // for (File file : Objects.requireNonNull(inst.imageDir.listFiles())) {
    // if (file.isDirectory()) {
    // for (File file1 : Objects.requireNonNull(file.listFiles())) {
    // if (file.isDirectory()) {
    // File depFile = new File(depImageDir, file.getName() + "/" + file1.getName());
    // Log.debug("复制目录：{} 到：{}", depFile, file1);
    // FileUtil.copyDir(depFile, file1.getParentFile());
    // }
    // }
    // }
    // }
    // }

}
