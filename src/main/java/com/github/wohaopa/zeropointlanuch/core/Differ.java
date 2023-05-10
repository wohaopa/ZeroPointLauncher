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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Checksum;

import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;

public class Differ {

    public static Differ diff(File dir, Identification identification, List<String> exclude) {

        Differ differ = new Differ(dir.getPath() + "\\", exclude);
        differ.search(dir, identification.item);
        return differ;
    }

    public final List<String> removed = new ArrayList<>();
    public final List<String> addition = new ArrayList<>();
    private final String rootStr;
    private List<String> exclude;

    public Differ(String rootStr, List<String> exclude) {
        this.rootStr = rootStr;
        this.exclude = exclude;
    }

    private void search(File dir, Identification.IdentificationDirectoryItem directoryItem) {

        File[] rootDir = dir.listFiles();
        if (rootDir == null || rootDir.length < 1) return;
        List<String> loaded = new ArrayList<>();
        for (File file : rootDir) {
            String fileName = file.getName();

            if (exclude != null) {
                boolean flag = false;
                for (String s : exclude) if (file.getPath()
                    .endsWith(s)) {
                        flag = true;
                        exclude.remove(s);
                        break;
                    }
                if (flag) continue;
            }

            if (directoryItem.contains(fileName)) { // 镜像内存在相应名字的文件或者目录
                loaded.add(fileName);
                Identification.IdentificationItem identificationItem = directoryItem.getFile(fileName);
                if (identificationItem.isDirectory) {
                    search(file, (Identification.IdentificationDirectoryItem) identificationItem);
                } else {
                    if (file.isFile()) { // 镜像为文件，目标目录也存在相应文件
                        Checksum checksum = FileUtil.getChecksum(file);
                        long a = checksum.getValue();
                        long b = ((Identification.IdentificationFileItem) identificationItem).checksum;
                        if (a != b) {
                            removed.add(identificationItem.fullName);
                            addition.add(identificationItem.fullName);
                        }
                    } else { // 镜像为文件，目标目录存在相应目录
                        removed.add(identificationItem.fullName);
                        addition.add(identificationItem.fullName + "\\");
                    }
                }
            } else { // 镜像内不存在相应名字的文件或者目录、
                if (file.isFile()) addition.add(
                    file.getPath()
                        .replace(rootStr, ""));
                else addition.add(
                    file.getPath()
                        .replace(rootStr, "") + "\\");
            }
        }
        directoryItem.subItem
            .forEach((s, identificationItem) -> { if (!loaded.contains(s)) removed.add(identificationItem.fullName); });
    }
}
