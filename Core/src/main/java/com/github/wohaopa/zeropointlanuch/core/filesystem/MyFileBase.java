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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSONObject;

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
                .setFile(file);
        }

    }

    public static MyFileBase getMyFileSystemByJson(String name, File file) {
        if (file.isFile()) {

            JSONObject json = (JSONObject) JsonUtil.fromJson(file);

            if (json.size() < 2) {
                for (String obj : json.keySet()) {
                    if (!obj.endsWith(separator)) {
                        return new MyFile(null, obj).setChecksum(json.getLong(obj));
                    }
                }
            }

            return new MyDirectory(null, name + separator).makeMyFileSystemInstance(json);

        } else return null;

    }

    public static void diff(MyFileBase myFileBase1, MyFileBase myFileBase2) {
        myFileBase1.diffWith(myFileBase2);
    }

    public MyFileBase saveChecksumAsJson(File file) {
        FileUtil.fileWrite(file, JsonUtil.toJson(this.saveChecksum()));
        return this;
    }

    public MyFileBase saveDiffAsJson(File file, Sate... sate) {

        FileUtil.fileWrite(file, JsonUtil.toJson(this.saveDiff(sate)));

        return this;
    }

    String name;// 文件名
    String path;// 相对路径
    MyDirectory parent;// 父文件
    File file;// 文件对象

    private Sate sate;

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

    protected MyFileBase(MyDirectory parent, String name) {
        this.parent = parent;
        this.name = name;
        if (parent == null) this.path = separator;
        else this.path = parent.path + name;
        this.sate = Sate.no_define;
    }

    public abstract boolean isFile();

    public abstract boolean isDirectory();

    protected abstract MyFileBase diffWith(MyFileBase other);

    protected abstract Object saveChecksum();

    protected abstract Object saveDiff(Sate... sates);

    protected MyFileBase setSate(Sate sate) {
        this.sate = sate;
        return this;
    }

    protected MyFileBase setFile(File file) {
        this.file = file;
        return this;
    }

    protected Sate getSate() {
        return sate;
    }

    @Override
    public String toString() {
        return path + "(" + sate.desc + ")";
    }
}
