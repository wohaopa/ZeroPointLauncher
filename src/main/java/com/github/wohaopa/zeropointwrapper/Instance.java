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

package com.github.wohaopa.zeropointwrapper;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import cn.hutool.core.io.resource.ResourceUtil;

import com.github.wohaopa.zeropointwrapper.utils.FileUtil;
import com.github.wohaopa.zeropointwrapper.utils.JsonUtil;
import com.github.wohaopa.zeropointwrapper.utils.ZipUtil;

public class Instance {

    public static Map<String, Instance> instances = new HashMap<>();
    public static Map<String, String> modidMap = new HashMap<>();

    static {
        String[] c = ResourceUtil.readStr("modidmap", Charset.defaultCharset())
            .split("\n");
        Arrays.stream(c)
            .forEach(s -> {
                String[] tmp = s.split("->");
                modidMap.put(tmp[1], tmp[0]);
            });

        // StringBuffer sb = new StringBuffer();
        // modidMap.entrySet().stream().forEach(stringStringEntry ->
        // sb.append(stringStringEntry.getValue()).append("->").append(stringStringEntry.getKey()).append("\n"));
        //
        // FileUtil.fileWrite(map, sb.toString());
    }

    public File versionFile;
    public File insDir;
    public File imageDir;
    public File runDir;
    public Information information;

    private Instance() {}

    /**
     * 启动时默认初始已安装的实例
     *
     * @param versionFile 找到的版本文件
     */
    public static void addInst(File versionFile) {
        Instance inst = new Instance();
        inst.versionFile = versionFile;

        try {
            inst.information = inst.loadInformation();
        } catch (Exception e) {
            Log.LOGGER.error("版本加载错误：" + e);
            return;
        }

        inst.insDir = new File(inst.information.insDir);
        inst.imageDir = new File(inst.information.imageDir);
        inst.runDir = new File(inst.information.runDir);

        instances.put(inst.information.name, inst);
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

        Instance inst = new Instance();
        inst.information = new Information();

        inst.information.name = name;
        inst.information.version = version;
        inst.information.depVersion = depVersion;

        inst.insDir = dir;
        inst.versionFile = versFile;
        inst.imageDir = FileUtil.initAndMkDir(dir, "image");
        inst.runDir = FileUtil.initAndMkDir(dir, ".minecraft");

        inst.information.insDir = inst.insDir.toString();
        inst.information.imageDir = inst.imageDir.toString();
        inst.information.runDir = inst.runDir.toString();

        inst.information.loadMod = genModList(inst.imageDir); // 加载mods信息
        inst.information.checksum = FileUtil.genChecksum(inst.imageDir); // 加载文件校验
        inst.information.exclude = new ArrayList<>(); // 排除文件的名字
        inst.savaInformation(); // 保存版本信息

        instances.put(name, inst);
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
    public static void installStandard(File zip, File dir, String name, String version) throws IOException {
        // 准备好目录
        File image = FileUtil.initAndMkDir(dir, "image");

        Log.LOGGER.debug("[实例安装]开始解压：{}", zip);
        long time1 = System.currentTimeMillis();
        ZipUtil.unCompress(zip, image); // 解压
        long time2 = System.currentTimeMillis();
        Log.LOGGER.debug("[实例安装]解压完成！用时：{}s", (time2 - time1) / 1000);

        install(dir, name, version, "null"); // 创建实例

        Log.LOGGER.info("[实例安装]安装完成！");
    }

    /**
     * 使用升级器升级实例
     *
     * @param updaterFile   更新器文件
     * @param dir           工作目录
     * @param name          实例名（唯一识别码）
     * @param version       实例版本（不起作用）
     * @param targetVersion 依赖的实例名
     */
    public static void installUpdate(File updaterFile, File dir, String name, String version, String targetVersion) {
        if (updaterFile.getName()
            .endsWith(".json")) {
            String json = FileUtil.fileRead(updaterFile);
            // UpdaterClass updaterObj = JsonUtil.fromJson(json, UpdaterClass.class);
            Instance depInstance = Instance.instances.get(targetVersion);
            if (depInstance == null) {
                Log.LOGGER.error("无法找到前置版本：{}", targetVersion);
                return;
            }
        }
    }

    public static void installTranslation(File translationFile, File dir, String name, String version,
        String targetVersion) {

        Instance depInst = Instance.instances.get(targetVersion);
        if (depInst == null) {
            Log.LOGGER.error("无法找到前置版本：{}", targetVersion);
            throw new RuntimeException("无法找到前置版本：" + targetVersion);
        }

        File image = FileUtil.initAndMkDir(dir, "image");

        Log.LOGGER.debug("[实例安装]开始解压：{}", translationFile);
        long time1 = System.currentTimeMillis();
        ZipUtil.unCompress(translationFile, image); // 解压
        long time2 = System.currentTimeMillis();
        Log.LOGGER.debug("[实例安装]解压完成！用时：{}s", (time2 - time1) / 1000);

        Instance inst = install(dir, name, version, targetVersion);
        File depImageDir = depInst.imageDir;

        for (File file : Objects.requireNonNull(inst.imageDir.listFiles())) {
            if (file.isDirectory()) {
                for (File file1 : Objects.requireNonNull(file.listFiles())) {
                    if (file.isDirectory()) {
                        File depFile = new File(depImageDir, file.getName() + "/" + file1.getName());
                        Log.LOGGER.debug("[实例安装]复制目录：{} 到：{}", depFile, file1);
                        FileUtil.copyDir(depFile, file1.getParentFile());
                    }
                }
            }
        }
    }

    private static List<String> genModList(File tmpDir) {
        File modsDir = new File(tmpDir, "\\mods");
        List<String> mods = new ArrayList<>();
        if (!modsDir.exists()) return mods;
        for (File mod : Objects.requireNonNull(modsDir.listFiles())) {
            if (mod.isFile()) {
                String modFileName = mod.getName();
                String modid = "_default";
                for (String key : modidMap.keySet()) {
                    if (modFileName.startsWith(key)) {
                        modid = modidMap.get(key);
                        break;
                    }
                }
                String path = modid + "/" + modFileName;
                mods.add(path);
                if (!FileUtil.moveFile(mod, new File(DirTools.modsDir, path))) {
                    Log.LOGGER.info("[实例安装]已在mod库中发现：{} 即将删除临时文件", modFileName);
                    if (mod.delete()) Log.LOGGER.warn("[实例安装]文件：{} 删除失败，可能被占用，请手动删除", mod.getPath());
                }
            }
        }
        return mods;
    }

    private void savaInformation() {
        Log.LOGGER.debug("[实例安装]正在保存：{}", versionFile);
        FileUtil.fileWrite(versionFile, JsonUtil.toJson(information));
    }

    private Information loadInformation() {
        Log.LOGGER.debug("[实例加载]正在加载：{}", versionFile);
        return JsonUtil.fromJson(FileUtil.fileRead(versionFile), Information.class);
    }

    /** 生成实例的运行目录（.minecraft） 可被其他启动器直接启动 */
    public void genRuntimeDir() {
        // 删除目录中的系统链接文件
        for (File file : Objects.requireNonNull(this.runDir.listFiles())) {
            if (file.isDirectory()) for (File file1 : Objects.requireNonNull(file.listFiles())) FileUtil.delLink(file1);
            else FileUtil.delLink(file);
        }

        Sharer.copyFileAsLink(this.runDir); // 优先拷贝共享文件
        this.copyFileAsLink(this.runDir); // 拷贝本实例的共享文件

        // 处理依赖
        String depVersion = this.information.depVersion;
        while (!"null".equals(depVersion)) {
            Instance instance = Instance.instances.get(depVersion);
            if (instance != null) {
                instance.copyFileAsLink(this.runDir);
                depVersion = instance.information.depVersion;
            } else {
                Log.LOGGER.error("未知版本：" + depVersion);
                throw new RuntimeException("未知版本：" + depVersion);
            }
        }
    }

    /** 生成文件链接，由被被依赖的实例在genRuntimeDir()中调用 */
    public void copyFileAsLink(File minecraftDir) {
        // 拷贝本实例文件
        for (File file : Objects.requireNonNull(this.imageDir.listFiles())) {
            if (this.information.exclude.contains(file.getName())) continue;
            if (file.isDirectory()) {
                new File(minecraftDir, file.getName()).mkdir();
                for (File file1 : Objects.requireNonNull(file.listFiles())) {
                    if (this.information.exclude.contains(file.getName())) continue;
                    FileUtil.genLink(
                        new File(minecraftDir, file.getName() + "/" + file1.getName()).toPath(),
                        file1.toPath());
                }
            } else FileUtil.genLink(new File(minecraftDir, file.getName()).toPath(), file.toPath());
        }

        // 拷贝mod独立的文件
        File modsDir = DirTools.modsDir;
        for (String mods : this.information.loadMod) {
            if (this.information.exclude.contains(mods)) continue; // 排除拷贝
            File modsFile = new File(modsDir, mods);
            if (!modsFile.exists()) {
                Log.LOGGER.fatal("缺失mod：{}", modsFile);
                throw new RuntimeException("缺失mod：" + modsFile);
            }
            File modsRun = new File(minecraftDir, "mods");
            FileUtil.genLink(new File(modsRun, modsFile.getName()).toPath(), modsFile.toPath());
        }
    }

    @SuppressWarnings("unused")
    public static class Information {

        public String name;
        public String version;
        public String imageDir;
        public String insDir;
        public String runDir;
        public String depVersion;
        public List<String> loadMod;
        public List<String> exclude;
        public Map<String, Long> checksum;

        public List<String> getExclude() {
            return exclude;
        }

        public String getDepVersion() {
            return depVersion;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public String getImageDir() {
            return imageDir;
        }

        public List<String> getLoadMod() {
            return loadMod;
        }

        public String getInsDir() {
            return insDir;
        }

        public String getRunDir() {
            return runDir;
        }

        public Map<String, Long> getChecksum() {
            return checksum;
        }
    }
}
