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

public class MyDirectory extends MyFileBase {

    private final Map<String, MyFileBase> subs;

    private int fileCount;

    protected MyDirectory(MyDirectory parent, String name) {
        super(parent, name);
        subs = new HashMap<>();
    }

    protected MyFileBase makeMyFileSystemInstance(File file) {
        return makeMyFileSystemInstance(file, null);
    }

    protected MyFileBase makeMyFileSystemInstance(File file, Map<String, List<String>> exclude) {
        Log.debug("Search file:{} ...", file.getName());

        List<String> exclude0 = exclude == null ? null : exclude.get(path);

        fileCount = 0;

        for (File file1 : Objects.requireNonNull(file.listFiles())) {
            if (FileUtil.isSymLink(file1) || !file1.exists()) continue;

            String name = file1.getName();

            if (exclude0 != null && exclude0.contains(name)) continue;

            if (file1.isFile()) {
                subs.put(name, new MyFile(this, name).setFile(file1));
                fileCount++;
            } else {
                MyDirectory tmp = (MyDirectory) new MyDirectory(this, name + separator)
                    .makeMyFileSystemInstance(file1, exclude)
                    .setFile(file1);
                fileCount += tmp.fileCount;
                subs.put(name + separator, tmp);
            }
        }

        return this;
    }

    protected MyFileBase makeMyFileSystemInstance(JSONObject json) {
        fileCount = 0;
        for (String filename : json.keySet()) {
            if (filename.endsWith(separator)) {
                MyDirectory tmp = (MyDirectory) new MyDirectory(this, filename)
                    .makeMyFileSystemInstance(json.getJSONObject(filename));
                subs.put(filename, tmp);
                fileCount += tmp.fileCount;
            } else {
                subs.put(filename, new MyFile(this, filename).setChecksum(json.getLong(filename)));
                fileCount++;
            }
        }

        return this;
    }

    @Override
    protected MyFileBase diffWith(MyFileBase other) {

        if (other.isFile()) {
            this.setSate(Sate.directory_file);
            other.setSate(Sate.file_directory);
            return this;
        }

        MyDirectory other1 = (MyDirectory) other;

        List<String> list = new ArrayList<>(other1.list());
        for (MyFileBase fileBase : subs.values()) {
            if (list.remove(fileBase.name)) {
                fileBase.diffWith(other1.getSub(fileBase.name));
            } else {
                fileBase.setSate(Sate.only_me);
                other1.addSub(fileBase.name).setSate(Sate.only_other);
            }
        }

        for (String name : list) {
            other1.getSub(name).setSate(Sate.only_me);
            this.addSub(name).setSate(Sate.only_other);
        }

        Sate tmpSate = Sate.equal;

        for (MyFileBase fileBase : subs.values()) {
            if (fileBase.getSate() != Sate.equal) {
                tmpSate = Sate.no_equal;
                break;
            }
        }
        this.setSate(tmpSate);

        tmpSate = Sate.equal;
        for (MyFileBase fileBase : other1.subs.values()) {
            if (fileBase.getSate() != Sate.equal) {
                tmpSate = Sate.no_equal;
                break;
            }
        }

        other1.setSate(tmpSate);

        return this;
    }

    @Override
    protected Object getChecksum() {
        if (getSate() == Sate.only_other) return null; // 并不在本文件系统中
        JSONObject objects = new JSONObject();
        for (MyFileBase fileBase : subs.values()) {
            objects.putOpt(fileBase.name, fileBase.getChecksum());
        }
        return objects;
    }

    @Override
    protected void getMargeFileList(List<Pair<String, String>> list) {
        if (shade) {

            Log.debug("影子目录：{}", path);
            list.add(new Pair<>(getFile().toString(), target.getFile().toString()));

        } else subs.values().forEach(myFileBase -> myFileBase.getMargeFileList(list));
    }

    @Override
    protected MyFileBase margeWith(final MyFileBase other, final MargeInfo margeInfo) {
        MyDirectory other1 = (MyDirectory) other;

        if (margeInfo.include(other1.path)) this.target = other1;
        else if (!margeInfo.exclude(other1.path)) other1.list().forEach(s -> {
            MyFileBase otherFileBase = other1.getSub(s);
            if (!otherFileBase.shade) {
                if (contains(s)) {
                    MyFileBase fileBase = getSub(s);
                    if (fileBase.isDirectory() && fileBase.shade) {
                        ((MyDirectory) fileBase).devolveShade().shade = false;
                    }
                    fileBase.margeWith(otherFileBase, margeInfo);
                } else {
                    MyFileBase fileBase = addSub(otherFileBase.name);
                    if (fileBase.isDirectory()) {
                        fileBase.addTarget(otherFileBase);
                        fileBase.shade = true;
                    } else fileBase.margeWith(otherFileBase, margeInfo);
                }
            }
        });

        return this;
    }

    @Override
    protected MyFileBase update(long time) {

        if (getFile().lastModified() >= time) {
            Log.debug("文件夹变更：\"{}\"", name);
            Set<String> list = new HashSet<>(list());
            for (File file1 : Objects.requireNonNull(getFile().listFiles())) {
                String name = file1.isFile() ? file1.getName() : file1.getName() + separator;
                if (subs.containsKey(name)) {
                    list.remove(name);
                    subs.get(name).update(time);
                } else {
                    Log.debug("文件夹\"{}\"：新增文件：\"{}\"", this.name, name);
                    if (file1.isFile()) {
                        subs.put(name, new MyFile(this, name).setFile(file1));
                        fileCount++;
                    } else {
                        MyDirectory tmp = (MyDirectory) new MyDirectory(this, name + separator)
                            .makeMyFileSystemInstance(file1)
                            .setFile(file1);
                        fileCount += tmp.fileCount;
                        subs.put(name + separator, tmp);
                    }
                }
            }
            for (String name : list) {
                Log.debug("文件夹\"{}\"：移除文件：\"{}\"", this.name, name);
                subs.remove(name);
            }
        } else {
            for (MyFileBase myFileBase : subs.values()) {
                myFileBase.update(time);
            }
        }

        return this;
    }

    /**
     * 下放映射
     *
     * @return
     */
    private MyDirectory devolveShade() {
        for (MyFileBase otherFileBase : ((MyDirectory) target).subs.values()) {
            MyFileBase fileBase = addSub(otherFileBase.name);
            fileBase.addTarget(otherFileBase);
            fileBase.shade = true;
        }
        return this;
    }

    @Override
    protected Object getDiff(Sate... sates) {
        if (getSate() == Sate.no_define) return null; // 未进行比较的无法保存
        if (getSate() == Sate.only_other) for (Sate s : sates) if (Sate.only_other == s) return Sate.only_other.desc;
        // only_other只是在本文件系统中有标识，特殊处理

        JSONObject objects = new JSONObject();
        for (MyFileBase fileBase : subs.values()) {
            objects.putOpt(fileBase.name, fileBase.getDiff(sates));
        }
        if (objects.size() > 0) return objects;
        return null;
    }

    @Override
    protected MyFileBase setSate(Sate sate) {
        super.setSate(sate);

        return this;
    }

    @Override
    protected Sate getSate() {

        if (super.getSate() == Sate.no_define) { // 文件的结果不需要被更改，只有未定义时拉取父类实例的状态
            if (parent != null && parent.getSate() != Sate.no_define) {
                Sate sate = parent.getSate();
                setSate(sate);
                return sate;
            }
        }

        return super.getSate();
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    public Set<String> list() {
        return subs.keySet();
    }

    public MyFileBase getSub(String name) {
        return subs.get(name);
    }

    public boolean contains(String name) {
        return subs.containsKey(name);
    }

    public MyFileBase addSub(String fileName) {
        MyFileBase myFileBase = fileName.endsWith(separator) ? new MyDirectory(this, fileName)
            : new MyFile(this, fileName);
        subs.put(fileName, myFileBase);

        return myFileBase;
    }
}
