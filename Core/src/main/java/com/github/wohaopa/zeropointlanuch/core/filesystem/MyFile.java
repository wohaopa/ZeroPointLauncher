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
import java.util.List;

import cn.hutool.core.io.FileUtil;

import com.github.wohaopa.zeropointlanuch.core.Log;

public class MyFile extends MyFileBase {

    private long CRC32;

    protected MyFile(MyDirectory parent, String name) {
        super(parent, name);
    }

    private long checksum() {
        if (CRC32 == 0) {
            if (file == null) {
                Log.warn("文件：{}为空", path);
                return 0L;
            }

            return CRC32 = FileUtil.checksumCRC32(file);
        }
        return CRC32;
    }

    @Override
    protected MyFileBase diffWith(MyFileBase other) {

        if (other.isDirectory()) {
            this.setSate(Sate.file_directory);
            other.setSate(Sate.directory_file);
            return this;
        }
        MyFile other1 = (MyFile) other;
        if (checksum() == other1.checksum()) {
            this.setSate(Sate.equal);
            other.setSate(Sate.equal);
        } else {
            this.setSate(Sate.no_equal);
            other.setSate(Sate.no_equal);
        }
        return this;
    }

    @Override
    protected Object getChecksum() {
        if (getSate() == Sate.only_other) return null; // 并不在本文件系统中
        return checksum();
    }

    @Override
    protected void getMargeFileList(List<File> list) {
        if (shade) {
            if (target.file != null) list.add(target.file);
            else throw new RuntimeException("映射文件为空！");
        }
    }

    @Override
    protected MyFileBase margeWith(MyFileBase other, MargeInfo margeInfo) {
        if (margeInfo.include(other.path)) this.target = other;
        else if (!margeInfo.exclude(other.path)) addTarget(other);
        return this;
    }

    @Override
    protected MyFileBase update(File rootDir, long time) {
        if (file == null) {
            file = new File(rootDir, path);
        }
        if (file.lastModified() >= time) {
            long t = CRC32;
            CRC32 = FileUtil.checksumCRC32(file);
            Log.debug("文件变更：{} -> {}", t, CRC32);
        }
        return this;
    }

    @Override
    protected Object getDiff(Sate... sates) {
        if (getSate() == Sate.no_define) return null;
        for (Sate s : sates) {
            if (getSate() == s) return getChecksum();
        }
        return null;
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

    public MyFile setChecksum(long l) {
        this.CRC32 = l;
        return this;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isFile() {
        return true;
    }

}
