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

package com.github.wohaopa.zpl.mappingtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class Mapping {

    public static void main(String[] args) throws IOException {
        if (args.length < 1 || args[0].endsWith("::")) throw new RuntimeException("缺失参数：文件名");

        File file = new File(args[0]);
        File lock = new File(file.getParentFile(), "mapping.lock");
        if (!lock.exists()) lock.createNewFile();

        String context;
        try {
            context = Objects.requireNonNull(readFileToString(file))
                .replace("\r", "");
        } catch (Exception e) {
            throw new RuntimeException("无效文件：" + e);
        }
        String[] lines = context.split("\n");

        Arrays.stream(lines)
            .forEach(s -> {
                String[] t = s.split("->", 2);
                if (t.length >= 2) doLink(t[0], t[1]);
                else System.out.println("错误：" + t + "文件：" + args[0]);
            });
        if (lock.delete()) System.out.println("[警告]无法通知启动器映射情况！请手动删除文件：" + lock);
        System.out.println("链接完成！按任意键退出");
    }

    public static void doLink(String file, String link) {
        Path filePath = Path.of(file);
        Path linkPath = Path.of(link);
        if (Files.exists(filePath)) {

            try {
                if (!filePath.equals(filePath.toRealPath())) {
                    Files.delete(filePath); // 无法追溯源文件
                    new File(file).delete();
                }
            } catch (IOException e) {
                try {
                    Files.delete(filePath); // 无法追溯源文件
                    new File(file).delete();
                } catch (IOException ignored) {}
                if (Files.exists(filePath)) throw new RuntimeException("无法删除文件：" + filePath);
            }
        }
        if (Files.exists(filePath) || !Files.exists(linkPath))
            throw new RuntimeException("链接错误！" + filePath + "->" + linkPath);
        try {
            Files.createDirectories(filePath.getParent());
            Files.createSymbolicLink(filePath, linkPath);
            System.out.println("链接：" + filePath + "->" + linkPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readFileToString(File file) {
        String encoding = "UTF-8";

        long filelength = file.length();
        byte[] filecontent = new byte[(int) filelength];

        try (FileInputStream in = new FileInputStream(file)) {
            in.read(filecontent);
            return new String(filecontent, encoding);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
