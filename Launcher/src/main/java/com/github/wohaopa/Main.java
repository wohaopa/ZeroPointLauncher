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

package com.github.wohaopa;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("ZPL-LauncherWrapper start...");
        System.out.println("====================System Information======================");
        System.out.println("Java Version: " + System.getProperty("java.version")); // java版本号
        System.out.println("Java Vendor: " + System.getProperty("java.vendor")); // Java提供商名称
        System.out.println("Java Home: " + System.getProperty("java.home")); // Java，哦，应该是jre目录
        System.out.println("Java Class Version: " + System.getProperty("java.class.version")); // Java类版本号
        System.out.println("Java Class Path: " + System.getProperty("java.class.path")); // Java类路径
        System.out.println("Java Library Path: " + System.getProperty("java.library.path")); // Java lib路径
        System.out.println("OS Name: " + System.getProperty("os.name")); // 操作系统名称
        System.out.println("OS Arch: " + System.getProperty("os.arch")); // 操作系统的架构
        System.out.println("User Dir: " + System.getProperty("user.dir")); // 当前程序所在目录
        System.out.println("=============================================================");

        String javaPath;
        double classVersion = Double.parseDouble(System.getProperty("java.class.version"));
        if (classVersion < 61.0) {

            System.out.println("Waring: ZeroPointLauncher must run with Java17 or above!");
            System.out.println("警告: ZeroPointLauncher 必须使用Java17以上版本运行");
            System.out.println("Looking for suitable Java...");
            System.out.println("正在寻找符合要求的Java...");
            javaPath = getJava17();
            if (javaPath == null) {
                System.exit(1);
            }
            System.out.println("Find Java: " + javaPath);
            System.out.println("找到Java: " + javaPath);
        } else {
            javaPath = System.getProperty("java.home") + "\\bin\\java.exe";
        }

        File launcherConfig = new File("lib\\launcher.config");

        List<String> jvmArg = null;
        if (launcherConfig.exists()) {
            try (FileReader fileReader = new FileReader(launcherConfig)) {
                BufferedReader br = new BufferedReader(fileReader);
                String str = br.readLine();
                jvmArg = Arrays.asList(str.split(" "));

            } catch (IOException ignored) {}
        }
        if (jvmArg == null) {

            File libDir = new File("lib");
            String jarName = null;
            if (libDir.exists()) {
                for (String fileName : Objects.requireNonNull(libDir.list())) {
                    if (fileName.startsWith("UI")) {
                        jarName = "lib/" + fileName;
                        break;
                    }
                }
            }

            if (jarName == null) throw new RuntimeException("未找到");

            jvmArg = Arrays.asList(
                "--module-path",
                ".\\lib\\",
                "--add-modules",
                "javafx.controls,javafx.graphics,javafx.base,com.jfoenix,ZeroPointLaunch.Core",
                "--add-opens",
                "java.base/java.lang.reflect=com.jfoenix",
                "--add-opens",
                "java.base/java.lang.reflect=ALL-UNNAMED",
                "--add-exports",
                "javafx.controls/com.sun.javafx.scene.control.behavior=com.jfoenix",
                "--add-exports",
                "javafx.controls/com.sun.javafx.scene.control=com.jfoenix",
                "-jar",
                jarName);
        }

        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(new File(System.getProperty("user.dir")));

        List<String> command = new LinkedList<>();
        command.add(javaPath);
        command.addAll(jvmArg);

        pb.command(command);

        Process process;
        try {
            process = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!"true".equals(System.getProperty("launcher.debug"))) return;
        try (BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(process.getErrorStream(), Charset.defaultCharset()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (Thread.currentThread().isInterrupted()) {
                    Thread.currentThread().interrupt();
                    break;
                }

                System.out.println(line);
            }
        } catch (IOException ignored) {}
    }

    private static String getJava17() {

        try {
            List<String> res1 = queryRegisterValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft");

            List<String> res2 = new LinkedList<>();
            for (String str : res1) {
                if (str.startsWith("HKEY_LOCAL_MACHINE")) {
                    res2.addAll(queryRegisterValue(str));
                }
            }
            res2.removeAll(res1);
            List<String> res3 = new LinkedList<>();
            for (String str : res2) {
                if (str.startsWith("HKEY_LOCAL_MACHINE")) {
                    res3.addAll(queryRegisterValue(str));
                }
            }
            Set<String> res4 = new HashSet<>();
            for (String str : res3) {
                if (str.startsWith("    JavaHome")) {
                    res4.add(str.substring("    JavaHome    REG_SZ    ".length()));
                }
            }
            String res5 = null;
            int last = 16;
            for (String str : res4) {
                String version = str.substring(str.length() - 2);
                try {
                    int tmp = Integer.parseInt(version);
                    if (tmp > last) {
                        String tmpStr = str + "\\bin\\java.exe";
                        File file = new File(tmpStr);
                        if (file.exists()) {
                            res5 = tmpStr;
                            last = tmp;
                        }
                    }
                } catch (Exception ignored) {}
            }

            return res5;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> queryRegisterValue(String location) throws IOException {
        Process process = Runtime.getRuntime().exec(new String[] { "cmd", "/c", "reg", "query", location });

        List<String> res = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
            for (String line; (line = reader.readLine()) != null;) {
                if (!line.trim().isEmpty()) {
                    res.add(line);
                }
            }
        }
        return res;
    }
}
