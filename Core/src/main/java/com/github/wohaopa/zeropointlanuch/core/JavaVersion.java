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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaVersion {

    private final static Pattern pattern = Pattern.compile("java version \"(.+)\"");

    private final static Map<File, JavaVersion> inst = new HashMap<>();

    static {
        // Log.start("JavaCheck");
        // Log.debug("正在查找Java");
        // File javaRoot = new File("C:\\Program Files\\java\\");
        // if (javaRoot.exists()) {
        // for (File file : Objects.requireNonNull(javaRoot.listFiles())) {
        // new JavaVersion(new File(file, "bin\\java.exe"));
        // }
        // }
        // Log.debug("找到：{}，个有效Java", inst.size());
        // Log.end();
    }

    public static JavaVersion getJava(File file) {
        return inst.get(file);
    }

    public static Collection<JavaVersion> getJavas() {
        return inst.values();
    }

    public final File javaExe;
    public String name;

    public final Java version;

    public JavaVersion(File javaExe) {

        Java version1 = null;

        this.javaExe = javaExe;
        Process process;
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(javaExe.toString(), "-version");

        try {
            process = processBuilder.start();
        } catch (IOException e) {
            version = Java.unknown;
            return;
        }

        try (BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(process.getErrorStream(), Charset.defaultCharset()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (Thread.currentThread().isInterrupted()) {
                    Thread.currentThread().interrupt();
                    break;
                }

                Matcher m = pattern.matcher(line);
                if (m.find()) {
                    name = m.group(1);
                    if (name.startsWith("1.8.0_")) {
                        version1 = Java.JAVA8;
                        Log.debug(line);
                        break;
                    }
                    String[] strs = name.split("\\.");
                    if (strs.length >= 1) {
                        version1 = Integer.parseInt(strs[0]) >= 17 ? Java.JAVA17 : Java.unknown;
                        Log.debug(line);
                        break;
                    }
                }
            }
        } catch (IOException ignored) {}

        if (version1 == null) {
            version = Java.unknown;
        } else {
            version = version1;
            inst.put(javaExe, this);
        }

    }

    @Override
    public String toString() {
        return name + " (" + javaExe.toString() + ")";
    }

    public enum Java {
        JAVA8,
        JAVA17,
        unknown
    }
}
