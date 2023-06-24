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

package com.github.wohaopa.zeropointlanuch.api;

import java.util.Collection;

import com.github.wohaopa.zeropointlanuch.core.*;

public class Core {

    public static final String launcherVersion = "0.0.1";

    // /**
    // * 安装标准实例
    // *
    // * @param zipFile zip文件
    // * @param dir 实例文件夹
    // * @param name 实例名
    // * @param version 实例版本
    // */
    // public static void installStandard(File zipFile, File dir, String name, String version) {
    // Log.start("标准实例安装");
    //// InstanceInstaller.installStandard(zipFile, dir, name, version);
    // Log.end();
    // }

    // /** 检查实例文件夹 */
    // public static void refresh() {
    // Log.start("实例初始化");
    // Instance.clear();
    // for (File file : Objects.requireNonNull(
    // ZplDirectory.getInstancesDirectory()
    // .listFiles())) {
    // if (file.isDirectory()) {
    // File version = new File(file, "version.json");
    // if (version.exists()) {
    // Log.debug("发现实例" + file.getName());
    // InstanceInstaller.addInst(version);
    // }
    // }
    // }
    // Log.end();
    // // Log.start("文件初始化");
    // // for (File file : Objects.requireNonNull(DirTools.zipDir.listFiles())) {
    // // if (file.isFile() && file.getName()
    // // .endsWith(".zip")) {
    // // String name = file.getName();
    // // // name = name.replace("GT_New_Horizons_", "");
    // // name = name.replace("_Client.zip", "");
    // // if (!Instance.containsKey(name)) {
    // // Log.debug("发现安装包 {}，名为 {}", file.getName(), name);
    // // InstanceInstaller.installStandard(file, new File(DirTools.instancesDir, name), name, name);
    // // }
    // // }
    // // }
    // // Log.end();
    // }

    /**
     * 实例列表
     *
     * @return 实例列表
     */
    public static Collection<Instance> listInst() {
        return Instance.list();
    }

    /**
     * 安装翻译
     *
     * @param translationFile
     * @param dir
     * @param name
     * @param targetVersion
     */
    // public static void installTranslation(File translationFile, File dir, String name, Instance targetVersion) {
    // Log.start("汉化实例安装");
    // String version = targetVersion.information.version;
    // InstanceInstaller.installTranslation(translationFile, dir, name, version, targetVersion);
    // Log.end();
    // }

    /**
     * 根据实例生成.minecraft差异
     *
     * @param outDir   输出目录
     * @param instance 实例
     * @param file     .minecraft
     */
    // public static void genTransferOld(File outDir, Instance instance, File file) {
    // Log.start(".minecraft对比");
    // List<String> exclude = new ArrayList<>();
    // for (File file1 : Objects.requireNonNull(file.listFiles())) {
    // if (file1.getName()
    // .startsWith(".")) exclude.add(file1.getPath());
    // }
    // exclude.add(file.getPath() + "\\assets");
    // exclude.add(file.getPath() + "\\libraries");
    // exclude.add(file.getPath() + "\\logs");
    // exclude.add(file.getPath() + "\\versions");
    //
    // long a = System.currentTimeMillis();
    // Identification identification = new Identification(instance.information.checksum);
    // Differ differ = Differ.diff(file, identification, exclude);
    // long b = System.currentTimeMillis();
    // Log.debug("对比用时：{}s", (b - a) / 1000.0);
    //
    // if (outDir.exists()) {
    // File oldOutDir = new File(outDir.getParentFile(), outDir.getName() + ".backup");
    // FileUtil.delete(oldOutDir);
    // if (!outDir.renameTo(oldOutDir)) FileUtil.delete(outDir);
    // } else {
    // outDir.mkdir();
    // }
    //
    // File imageDir = new File(outDir, "image");
    //
    // for (String s : differ.addition) {
    // File f1 = new File(file, s);
    // if (f1.isDirectory()) {
    // File[] fileList = f1.listFiles();
    // if (fileList == null || fileList.length == 0) continue;
    // }
    // File f2 = new File(imageDir, s).getParentFile();
    // if (!f2.exists()) f2.mkdirs();
    // FileUtil.copyDir(f1, f2);
    // }
    //
    // File versionJsonFile = new File(outDir, "version.json");
    // Instance.Information information = new Instance.Information();
    //
    // information.name = instance.information.name = "-Changed";
    // information.version = instance.information.version;
    // information.depVersion = instance.information.name;
    // information.sharer = instance.information.sharer;
    // information.includeMods = instance.information.includeMods;
    // information.excludeMods = instance.information.excludeMods;
    // information.checksum = null;
    //
    // Instance.Information.toJson(information, versionJsonFile);
    // Log.end();
    // }

}
