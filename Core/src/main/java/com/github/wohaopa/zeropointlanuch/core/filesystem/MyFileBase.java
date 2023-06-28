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

package com.github.wohaopa.zeropointlanuch.core.filesystem;

import java.io.File;
import java.util.*;

import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;

public abstract class MyFileBase {

    public static final String separator = File.separator;

    public static final Map<String, List<String>> DEFAULT_FI = new HashMap<>();

    static {
        ArrayList<String> list = new ArrayList<>();
        list.add(".asm");
        list.add(".mixin.out");
        list.add("assets");
        list.add("libraries");
        list.add("crash-reports");
        list.add("saves");
        list.add("logs");
        list.add("versions");
        DEFAULT_FI.put("\\", list);
    }

    public static MyFileBase getMyFileSystemByFile(File file, Map<String, List<String>> exclude) {

        if (file.isFile()) {
            return new MyFile(null, file.getName()).setFile(file);
        } else {
            return new MyDirectory(null, file.getName() + separator).makeMyFileSystemInstance(file, exclude)
                .setFile(file)
                .setRootDir(file);
        }

    }

    public static MyFileBase getMyFileSystemByJson(String name, File file, File rootDir) {
        if (file.isFile()) {

            JSONObject json = (JSONObject) JsonUtil.fromJson(file);

            if (json.size() < 2) {
                for (String obj : json.keySet()) {
                    if (!obj.endsWith(separator)) {
                        return new MyFile(null, obj).setChecksum(json.getLong(obj))
                            .setRootDir(rootDir);
                    }
                }
            }

            return new MyDirectory(null, name + separator).makeMyFileSystemInstance(json)
                .setRootDir(rootDir);

        } else throw new RuntimeException(file + "不为文件！");
    }

    // public static MyFileBase getMyFileSystemByJson(String name, File file) {
    // return getMyFileSystemByJson(name, file, null);
    //
    // }

    public static void diff(MyFileBase myFileBase1, MyFileBase myFileBase2) {
        myFileBase1.diffWith(myFileBase2);
    }

    public static void marge(MyFileBase myFileBase1, MyFileBase myFileBase2, MargeInfo margeInfo) {
        myFileBase1.margeWith(myFileBase2, margeInfo);
    }

    public static void update(MyFileBase myFileBase1, File rootDir, File json) {
        myFileBase1.setRootDir(rootDir)
            .update(json.lastModified())
            .saveChecksumAsJson(json);

    }

    protected String name; // 文件名
    protected String path; // 相对路径
    protected MyDirectory parent; // 父文件
    private File file; // 文件对象
    private File rootDir; // 顶层文件

    // 映射
    protected boolean shade; // 映射此文件
    protected MyFileBase target; // 映射目标
    private List<MyFileBase> targets; // 目标文件系统的同级对象

    // 差异
    private Sate sate; // 文件状态

    protected File getRootDir() {
        if (rootDir != null) return rootDir;
        if (parent != null) return rootDir = parent.getRootDir();
        Log.error("无法找到root文件夹：{}", toString());
        throw new RuntimeException("无法找到root文件夹");
    }

    /** 用于差异系统的状态枚举 */
    public enum Sate {

        no_define("未定义"),
        equal("都有且相同"),
        no_equal("都有但不同"),
        only_me("只有此文件系统有"),
        only_other("只有目标文件系统有"),
        file_directory("此文件系统为文件，目标为目录"),
        directory_file("此文件系统为目录，目标为文件");

        final String desc;

        Sate(String desc) {
            this.desc = desc;
        }
    }

    // 成员方法，void方法返回this指针，便于流式调用
    public MyFileBase saveChecksumAsJson(File file) {
        FileUtil.fileWrite(file, JsonUtil.toJson(this.getChecksum()));
        return this;
    }

    public MyFileBase saveDiffAsJson(File file, Sate... sate) {

        FileUtil.fileWrite(file, JsonUtil.toJson(this.getDiff(sate)));
        return this;
    }

    MyFileBase setRootDir(File rootDir) {
        this.rootDir = rootDir;
        return this;
    }

    public List<Pair<String, String>> getMargeFileList() {
        List<Pair<String, String>> files = new LinkedList<>();
        getMargeFileList(files);
        return files;
    }

    protected MyFileBase(MyDirectory parent, String name) {
        this.parent = parent;
        this.name = name;
        if (parent == null) this.path = separator;
        else this.path = parent.path + name;
        this.sate = Sate.no_define;
    }

    public abstract boolean isFile();

    public abstract boolean isDirectory();

    protected abstract Object getChecksum();

    protected abstract void getMargeFileList(List<Pair<String, String>> list);

    public static class MargeInfo {

        private final List<MyFileBase> fails;
        private final List<String> include;
        private final List<String> exclude;

        public MargeInfo(List<String> include, List<String> exclude) {
            this.include = include;
            this.exclude = exclude;
            this.fails = new ArrayList<>();
        }

        public List<String> getFails() {
            return (List<String>) fails.stream()
                .map(myFileBase -> myFileBase.path);
        }

        public void addFail(MyFileBase myFileBase) {
            this.fails.add(myFileBase);
        }

        public boolean include(String path) {
            if (include == null || include.isEmpty()) return false;
            return include.contains(path);
        }

        public boolean exclude(String path) {
            if (exclude == null || exclude.isEmpty()) return false;
            return exclude.contains(path);
        }

    }

    // 映射
    protected abstract MyFileBase margeWith(MyFileBase other, MargeInfo margeInfo);

    protected MyFileBase addTarget(MyFileBase target) {
        if (this.target == null) this.target = target;
        if (this.targets == null) this.targets = new LinkedList<>();
        targets.add(target);

        return this;
    }

    protected abstract MyFileBase update(long time);

    // 差异
    protected abstract MyFileBase diffWith(MyFileBase other);

    protected abstract Object getDiff(Sate... sates);

    protected MyFileBase setSate(Sate sate) {
        this.sate = sate;
        return this;
    }

    protected Sate getSate() {
        return sate;
    }

    protected File getFile() {
        if (file == null) {
            if (parent == null) file = getRootDir();// 根目录
            else file = new File(parent.getRootDir(), path);
        }
        return file;
    }

    protected MyFileBase setFile(File file) {
        this.file = file;
        return this;
    }

    public MyFileBase setTargetForFile(File file) {
        MyFileBase otherFileBase;
        if (isFile()) otherFileBase = new MyFile(null, name);
        else otherFileBase = new MyDirectory(null, name);
        otherFileBase.setFile(file);
        this.shade = true;
        return addTarget(otherFileBase);
    }

    @Override
    public String toString() {
        return path + "(" + sate.desc + (shade ? "，映射" : "") + ")";
    }
}
