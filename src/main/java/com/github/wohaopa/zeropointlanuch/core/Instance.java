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

import java.io.*;
import java.util.*;

import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;

public class Instance {

    private static final Map<String, Instance> instances = new HashMap<>();

    public File versionFile;
    public File insDir;
    public File imageDir;
    public File runDir;
    public Information information;

    private Instance() {}

    /**
     * 外部调用，仅创建与加载实例时使用
     *
     * @return 新实例
     */
    public static Instance newInstance() {
        return new Instance();
    }

    /**
     * 获得实例
     *
     * @param name 名
     * @return 实例
     */
    public static Instance get(String name) {
        return instances.get(name);
    }

    /**
     * 安装与加载实例的时候使用，用于注册实例
     *
     * @param name     名
     * @param instance 实例
     */
    public static void put(String name, Instance instance) {
        instances.put(name, instance);
    }

    public static void clear() {
        instances.clear();
    }

    public static boolean containsKey(String name) {
        return instances.containsKey(name);
    }

    public static Collection<Instance> list() {
        return instances.values();
    }

    /** 加载实例信息，仅在初始化阶段使用本方法来加载持久化的实例 */
    public Information loadInformation() {
        Log.LOGGER.debug("[实例加载]正在加载：{}", versionFile);
        return JsonUtil.fromJson(FileUtil.fileRead(versionFile), Information.class);
    }

    /** 保存实例信息 */
    public void savaInformation() {
        Log.LOGGER.debug("[实例安装]正在保存：{}", versionFile);
        FileUtil.fileWrite(versionFile, JsonUtil.toJson(information));
    }

    /** 生成实例的运行目录（.minecraft） 可被其他启动器直接启动 */
    public void genRuntimeDir() {
        // 删除目录中的系统链接文件
        for (File file : Objects.requireNonNull(this.runDir.listFiles())) {
            if (file.isDirectory()) for (File file1 : Objects.requireNonNull(file.listFiles())) FileUtil.delLink(file1);
            else FileUtil.delLink(file);
        }
        Mapper mapper = new Mapper(new File(this.imageDir, "zpl_margi_config.json"), this.runDir);

        Sharer.execute(this.information.sharer, mapper); // 优先共享文件

        this.copyFileAsLink(mapper, this.information.excludeMods); // 本实例的共享文件

        // 处理依赖
        String depVersion = this.information.depVersion;
        while (!"null".equals(depVersion)) {
            Instance instance = Instance.instances.get(depVersion);
            if (instance != null) {
                instance.copyFileAsLink(mapper, this.information.excludeMods);

                depVersion = instance.information.depVersion;
            } else {
                Log.LOGGER.error("未知版本：" + depVersion);
                throw new RuntimeException("未知版本：" + depVersion);
            }
        }
    }

    /** 生成文件链接，由被被依赖的实例在genRuntimeDir()中调用 */
    private void copyFileAsLink(Mapper mapper, List<String> excludeModsList) {
        // 处理本实例的image
        mapper.execute(this.information.name, this.imageDir);

        // 处理mod
        File modsDir = DirTools.modsDir;
        File modsRun = new File(mapper.getTargetDir(), "mods");

        for (String mods : this.information.includeMods) {
            if (excludeModsList.contains(mods)) continue; // 排除
            File modsFile = new File(modsDir, mods);
            if (!modsFile.exists()) {
                Log.LOGGER.fatal("缺失mod：{}", modsFile);
                throw new RuntimeException("缺失mod：" + modsFile);
            }
            FileUtil.genLink(new File(modsRun, modsFile.getName()), modsFile);
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
        public String sharer;
        public List<String> includeMods;
        public List<String> excludeMods;
        public Map<String, Long> checksum;

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

        public List<String> getIncludeMods() {
            return includeMods;
        }

        public List<String> getExcludeMods() {
            return excludeMods;
        }

        public String getInsDir() {
            return insDir;
        }

        public String getRunDir() {
            return runDir;
        }

        public String getSharer() {
            return sharer;
        }

        public Map<String, Long> getChecksum() {
            return checksum;
        }
    }
}
