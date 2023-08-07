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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.github.wohaopa.zeropointlanuch.core.auth.Auth;
import com.github.wohaopa.zeropointlanuch.core.filesystem.MyDirectory;
import com.github.wohaopa.zeropointlanuch.core.filesystem.MyFileBase;
import com.github.wohaopa.zeropointlanuch.core.launch.Launch;
import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;

public class Instance {

    private static final Map<String, Instance> instances = new ConcurrentHashMap<>();

    public static void rename(String oV, String nV) {
        Instance instance = instances.remove(oV);
        instance.information.name = nV;
        instances.put(nV, instance);
    }

    public static class Builder {

        Information information;
        MyDirectory myImage;
        File versionFile;

        public Builder(String name) {
            information = new Information();
            information.name = name;
        }

        public Builder(File versionFile) {
            this.versionFile = versionFile;
            information = Information.formJson(versionFile);
        }

        public Builder setVersionFile(File versionFile) {
            this.versionFile = versionFile;
            return this;
        }

        public Builder setMyImage(MyDirectory myImage) {
            this.myImage = myImage;
            return this;
        }

        public Builder setName(String name) {
            information.name = name;
            return this;
        }

        public Builder setVersion(String version) {
            information.version = version;
            return this;
        }

        public Builder setDepVersion(String depVersion) {
            information.depVersion = depVersion;
            return this;
        }

        public Builder setSharer(String sharer) {
            information.sharer = sharer;
            return this;
        }

        public Builder setLauncher(String launcher) {
            information.launcher = launcher;
            return this;
        }

        public Builder setUpdate(boolean update) {
            information.update = update;
            return this;
        }

        public Builder setIncludeMods(List<String> includeMods) {
            information.includeMods = includeMods;
            return this;
        }

        public Builder setExcludeMods(List<String> excludeMods) {
            information.excludeMods = excludeMods;
            return this;
        }

        public Builder setChecksum(File image) {

            MyDirectory myDirectory = (MyDirectory) MyFileBase.getMyFileSystemByFile(image, null);
            File checksumFile = new File(versionFile.getParentFile(), "checksum.json");
            myDirectory.saveChecksumAsJson(checksumFile);

            return this;
        }

        public Builder saveConfig() {
            Information.toJson(information, versionFile);
            return this;
        }

        public Instance build() {
            if (instances.containsKey(information.name)) return null;
            Instance instance = new Instance(versionFile, information);
            instance.myImage = myImage;
            instances.put(information.name, instance);
            return instance;
        }

        public String getName() {
            return information.name;
        }
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

    public File versionFile; // 实例配置文件
    public File insDir; // 实例文件夹
    public File imageDir; // 镜像文件夹
    public File runDir; // 运行文件夹
    public File checksumFile; // 校验文件
    private MyDirectory myImage; // MyFileSystem对象
    private Mapper mapper;
    public Information information; // 实例信息，名称版本都在这

    private Instance(File versionFile, Information information) {
        assert (versionFile != null);

        this.versionFile = versionFile;
        this.insDir = versionFile.getParentFile();
        this.checksumFile = new File(insDir, "checksum.json");
        this.imageDir = FileUtil.initAndMkDir(insDir, "image");
        this.runDir = FileUtil.initAndMkDir(insDir, ".minecraft");
        this.information = information;

        assert (information.name != null); // 名字

        if (information.version == null) information.version = information.name; // 版本，用于更新
        if (information.depVersion == null) information.depVersion = "null"; // 父版本，空为null，特殊标记用
        if (information.launcher == null) information.launcher = "ZPL-Java8"; // 启动器，Java8/Java17
        if (information.sharer == null) information.sharer = "Common"; // 分享器
        if (information.excludeMods == null) information.excludeMods = new LinkedList<>(); // 排除mod
        if (information.includeMods == null) information.includeMods = new LinkedList<>(); // 包含mod
    }

    public void launchInstance(Auth auth, Consumer<String> callback) {
        Launch launch = Launch.getLauncher(information.launcher);
        if (launch != null) {
            launch.launch(auth, runDir, callback);
        } else throw new RuntimeException("无法找到启动器：" + information.launcher);
    }

    public void clean() {
        clean0(runDir);
    }

    private void clean0(File dir) {
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (FileUtil.isSymLink(file)) file.delete();
                else if (file.isDirectory()) clean0(file);
            }
        }
    }

    public Mapper getMapper() {
        Sharer sharer = Sharer.get(information.sharer); // 先拿到sharer
        if (sharer == null) Log.warn("未找到Sharer：{}", information.sharer);
        mapper = new Mapper(null, this);
        mapper.update(sharer); // 执行合并文件夹方法
        return mapper;
    }

    public void doLink() {
        mapper.doLink(); // 生成映射文件清单
    }

    public MyDirectory getMyDirectory() {
        if (myImage == null) {
            File file = new File(insDir, "image.json");
            if (file.isFile()) {
                myImage = (MyDirectory) MyFileBase.getMyFileSystemByJson(information.name, file, imageDir);
                if (file.lastModified() < imageDir.lastModified()) MyFileBase.update(myImage, imageDir, file);
            } else {
                myImage = (MyDirectory) MyDirectory.getMyFileSystemByFile(imageDir, null);
                myImage.saveChecksumAsJson(file);
            }
        }

        return myImage;
    }

    /** 从文件重新加载实例信息 */
    public void loadInformation() {
        this.information = Information.formJson(versionFile);
    }

    /** 保存实例信息到文件 */
    public void savaInformation() {
        if (checksumFile != null && !checksumFile.isFile()) {
            checksumFile.delete();
            getMyDirectory().saveChecksumAsJson(checksumFile);
        }

        Information.toJson(information, versionFile);
    }

    @Override
    public String toString() {
        return information.name + "(" + information.version + ")";
    }

    @SuppressWarnings("unused")
    public static class Information {

        public static Information formJson(File file) {
            Log.debug("正在加载：{}", file);
            return JsonUtil.fromJson(file).toBean(Information.class);
        }

        public static void toJson(Information information, File file) {
            Log.debug("正在保存：{}", file);
            FileUtil.fileWrite(file, JsonUtil.toJson(information));
        }

        public String name;
        public String version;
        public String depVersion;
        public String sharer;
        public String launcher;
        public boolean update;
        public List<String> includeMods;
        public List<String> excludeMods;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getDepVersion() {
            return depVersion;
        }

        public void setDepVersion(String depVersion) {
            this.depVersion = depVersion;
        }

        public String getSharer() {
            return sharer;
        }

        public void setSharer(String sharer) {
            this.sharer = sharer;
        }

        public String getLauncher() {
            return launcher;
        }

        public void setLauncher(String launcher) {
            this.launcher = launcher;
        }

        public boolean isUpdate() {
            return update;
        }

        public void setUpdate(boolean update) {
            this.update = update;
        }

        public List<String> getIncludeMods() {
            return includeMods;
        }

        public void setIncludeMods(List<String> includeMods) {
            this.includeMods = includeMods;
        }

        public List<String> getExcludeMods() {
            return excludeMods;
        }

        public void setExcludeMods(List<String> excludeMods) {
            this.excludeMods = excludeMods;
        }
    }
}
