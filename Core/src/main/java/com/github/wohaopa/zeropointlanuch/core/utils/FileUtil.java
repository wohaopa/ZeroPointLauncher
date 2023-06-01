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

package com.github.wohaopa.zeropointlanuch.core.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.Checksum;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.crypto.digest.DigestUtil;

import com.github.wohaopa.zeropointlanuch.core.Log;

public class FileUtil {

    /**
     * 获得并初始化目录
     *
     * @param parent 父文件夹对象
     * @param child  字文件夹路径
     * @return 文件夹对象
     */
    public static File initAndMkDir(File parent, String child) {
        File file = new File(parent, child);
        if (file.isFile()) if (!file.delete()) throw new RuntimeException("无法删除文件：" + file);
        if (!file.exists()) if (!file.mkdirs()) throw new RuntimeException("无法新建文件夹：" + file);
        return file;
    }

    /**
     * 获得所有子文件夹的文件对象
     *
     * @param file 目标文件夹
     * @return 子文件
     */
    public static List<File> fileList(File file) {
        List<File> res = new ArrayList<>();
        if (file.isFile()) {
            res.add(file);
            return res;
        }
        for (File file1 : Objects.requireNonNull(file.listFiles())) {
            if (file1.isFile()) res.add(file1);
            else res.addAll(fileList(file1));
        }
        return res;
    }

    /**
     * 读取文件
     *
     * @param file 文件对象
     * @return 文件内容
     */
    public static String fileRead(File file) {
        FileReader fr = new FileReader(file);
        return fr.readString();
    }

    /**
     * 写入文件（覆盖原内容）
     *
     * @param file    文件对象
     * @param content 写入内容
     */
    public static void fileWrite(File file, String content) {
        FileWriter fw = new FileWriter(file);
        fw.write(content);
    }

    /**
     * 获得文件夹及其子文件的所有校验
     *
     * @param dir 文件夹
     * @return 校验
     */
    public static Map<String, Long> genChecksum(File dir) {
        String parent = dir.toString() + "\\";
        Map<String, Long> res = new HashMap<>();
        List<File> fileList = FileUtil.fileList(dir);
        fileList.forEach(
            file -> res.put(
                file.toString()
                    .replace(parent, ""),
                FileUtil.getChecksum(file)
                    .getValue()));

        return res;
    }

    /**
     * 获得校验
     *
     * @param file 文件对象
     * @return 校验
     */
    public static Checksum getChecksum(File file) {
        return cn.hutool.core.io.FileUtil.checksum(file, null);
    }

    private static boolean adminFlag = false;

    /**
     * 创建symlink
     *
     * @param link   link文件
     * @param target 源文件
     */
    public static void genLink(Path link, Path target) {
        if (adminFlag) return;

        try {
            if (cn.hutool.core.io.FileUtil.isFile(link, true)) {
                Log.info("跳过文件：{} 文件已存在", link);
                return;
            }
            cn.hutool.core.io.FileUtil.mkdir(link.getParent());
            Files.createSymbolicLink(link, target);
        } catch (IOException e) {
            adminFlag = true;
            Log.info("无法创建文件链接，可能是没有管理员权限，文件：{} 目标：{}", link, target);
            throw new RuntimeException("缺少管理员权限，无法创建系统链接");
        }
    }

    /**
     * 将src->target
     *
     * @param src
     * @param target
     * @return 是否移动
     */
    public static boolean moveFile(File src, File target) {
        if (src.isFile()) {
            if (target.exists()) return false;
            if (!target.getParentFile()
                .exists())
                if (!target.getParentFile()
                    .mkdirs()) throw new RuntimeException("无法创建文件夹：" + target.getParentFile());
            return src.renameTo(target);
        }
        return false;
    }

    /**
     * 将src->target
     *
     * @param file
     * @param file1
     * @param cover 覆盖
     */
    public static void moveFile(File file, File file1, boolean cover) {
        if (cover && file1.exists()) delete(file1);
        moveFile(file, file1);
    }

    /**
     * 删除链接文件
     *
     * @param file 目标文件
     */
    public static boolean isSymLink(File file) {
        Path path = file.toPath();
        try {
            return !path.equals(path.toRealPath());
        } catch (IOException e) {
            FileUtil.delete(file);// 源文件失效，删除即可
            return false;
        }
    }

    public static void delete(File file) {
        cn.hutool.core.io.FileUtil.del(file);
    }

    public static void copyDir(File src, File target) {
        cn.hutool.core.io.FileUtil.copy(src, target, false);
    }

    /**
     * 检测文件sha1，hash不存在则为true，文件不存在则为false
     *
     * @param file 文件
     * @param hash sha1
     * @return
     */
    public static boolean checkSha1OfFile(File file, String hash) {
        if (!file.exists()) return false;
        if (hash == null || hash.isEmpty()) return true;
        return Objects.equals(DigestUtil.sha1Hex(file), hash);
    }
}
