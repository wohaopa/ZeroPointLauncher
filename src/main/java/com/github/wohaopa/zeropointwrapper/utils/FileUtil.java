package com.github.wohaopa.zeropointwrapper.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.Checksum;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;

import com.github.wohaopa.zeropointwrapper.Log;

public class FileUtil {

    public static File initAndMkDir(File parent, String child) {
        File file = new File(parent, child);
        if (file.isFile()) if (!file.delete()) throw new RuntimeException("无法删除文件：" + file);
        if (!file.exists()) if (!file.mkdir()) throw new RuntimeException("无法新建文件夹：" + file);
        return file;
    }

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

    public static String fileRead(File file) {
        FileReader fr = new FileReader(file);
        return fr.readString();
    }

    public static void fileWrite(File file, String content) {
        FileWriter fw = new FileWriter(file);
        fw.write(content);
    }

    public static Map<String, Long> genChecksum(File dir) {
        String parent = dir.toString();
        Map<String, Long> res = new HashMap<>();
        List<File> fileList = FileUtil.fileList(dir);
        for (File file : fileList) {
            res.put(
                file.toString()
                    .replace(parent, ""),
                FileUtil.getChecksum(file)
                    .getValue());
        }
        return res;
    }

    private static Checksum getChecksum(File file) {
        return cn.hutool.core.io.FileUtil.checksum(file, null);
    }

    public static boolean adminFlag = false;

    public static void genLink(Path link, Path target) {
        if (adminFlag) return;

        try {
            if (Files.exists(link)) {
                Log.LOGGER.info("跳过文件：{} 文件已存在", link);
                return;
            }
            Files.createSymbolicLink(link, target);
        } catch (IOException e) {
            adminFlag = true;
            Log.LOGGER.info("无法创建文件链接，可能是没有管理员权限，文件：{} 目标：{}", link, target);
            throw new RuntimeException("缺少管理员权限，无法创建系统链接");
        }
    }

    public static boolean moveFile(File src, File target) {
        if (src.isFile()) {
            if (target.exists()) return false;
            if (!target.getParentFile()
                .exists())
                if (!target.getParentFile()
                    .mkdir()) throw new RuntimeException("无法创建文件夹：" + target.getParentFile());
            return src.renameTo(target);
        }
        return false;
    }

    public static void delLink(File file) {
        Path path = file.toPath();
        try {
            if (!path.equals(path.toRealPath())) file.delete();
        } catch (IOException e) {
            Log.LOGGER.error("无法判断文件链接：{} 错误：{}", file.getPath(), e);
        }
    }

    public static boolean delete(File file) {
        return cn.hutool.core.io.FileUtil.del(file);
    }

    public static void copyDir(File src, File target) {
        cn.hutool.core.io.FileUtil.copy(src, target, false);
    }
}
