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

    // 实例对象属性与方法

    public File versionFile;// 实例配置文件

    public File insDir;// 实例文件夹

    public File imageDir; // 镜像文件夹

    public File runDir;// 运行文件夹

    public Information information;// 实例信息，名称版本都在这

    private Mapper mapper; // 映射器，（private主要用于lazy初始化、mapper初始化会产生大量操作，单启动并不需要mapper）

    private Instance() {}

    /** 加载实例信息，仅在初始化阶段使用本方法来加载持久化的实例 */
    public Information loadInformation() {
        Log.debug("正在加载：{}", versionFile);
        return Information.formJson(versionFile);
    }

    /** 保存实例信息 */
    public void savaInformation() {
        Log.debug("正在保存：{}", versionFile);
        Information.toJson(information, versionFile);
    }

    private Mapper getMapper() {
        if (mapper == null) {
            mapper = new Mapper(this);
        }
        return mapper;
    }

    public void delSymlink() {
        for (File file : Objects.requireNonNull(this.runDir.listFiles())) {
            if (!FileUtil.isSymLink(file) && file.isDirectory()) {
                for (File file1 : Objects.requireNonNull(file.listFiles()))
                    if (FileUtil.isSymLink(file1)) FileUtil.delete(file1);
            } else if (FileUtil.isSymLink(file)) {
                FileUtil.delete(file);
            }
        }
    }

    /** 生成实例的运行目录（.minecraft） 可被其他启动器直接启动 */
    public void genRuntimeDir() {

        this.delSymlink(); // 删除目录中的系统链接文件

        this.getMapper()
            .makeSymlink();

    }

    @Override
    public String toString() {
        return information.name + "(" + information.version + ")";
    }

    public void loadMap() {
        getMapper().refresh(null);
    }

    @SuppressWarnings("unused")
    public static class Information {

        public static Information formJson(File file) {
            return JsonUtil.fromJson(FileUtil.fileRead(file), Information.class);
        }

        public static void toJson(Information information, File file) {
            FileUtil.fileWrite(file, JsonUtil.toJson(information));
        }

        public String name;
        public String version;
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

        public List<String> getIncludeMods() {
            return includeMods;
        }

        public List<String> getExcludeMods() {
            return excludeMods;
        }

        public String getSharer() {
            return sharer;
        }

        public Map<String, Long> getChecksum() {
            return checksum;
        }
    }
}
