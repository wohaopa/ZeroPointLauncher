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

import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;

public class ZplDirectory {

    // private static boolean initialized = false;

    private static File workDirectory;
    private static File instancesDirectory;
    private static File librariesDirectory;
    private static File assetsDirectory;
    private static File modsDirectory;
    private static File zipDirectory;
    private static File shareDirectory;
    private static File versionsDirectory;

    private static File nativesRootDirectory;

    static {
        String rootDirStr = System.getProperty("zpl.rootDir");

        if (rootDirStr == null) {
            rootDirStr = System.getProperty("user.dir") + "/.GTNH";
        }

        init(new File(rootDirStr)); // 目录工具初始化
    }

    public static void init(File workDir) {
        Log.debug("正在初始化ZPL目录：{}", workDir.toString());
        ZplDirectory.workDirectory = workDir;
        ZplDirectory.instancesDirectory = FileUtil.initAndMkDir(workDir, "instances");
        ZplDirectory.librariesDirectory = FileUtil.initAndMkDir(workDir, "libraries");
        ZplDirectory.assetsDirectory = FileUtil.initAndMkDir(workDir, "assets");
        ZplDirectory.modsDirectory = FileUtil.initAndMkDir(workDir, "mods");
        ZplDirectory.zipDirectory = FileUtil.initAndMkDir(workDir, "zip");
        ZplDirectory.shareDirectory = FileUtil.initAndMkDir(workDir, "share");
        ZplDirectory.versionsDirectory = FileUtil.initAndMkDir(workDir, "versions");
        ZplDirectory.nativesRootDirectory = FileUtil.initAndMkDir(workDir, "natives");
    }

    public static File getWorkDirectory() {
        return workDirectory;
    }

    public static File getInstancesDirectory() {
        return instancesDirectory;
    }

    public static File getLibrariesDirectory() {
        return librariesDirectory;
    }

    public static File getAssetsDirectory() {
        return assetsDirectory;
    }

    public static File getModsDirectory() {
        return modsDirectory;
    }

    public static File getZipDirectory() {
        return zipDirectory;
    }

    public static File getShareDirectory() {
        return shareDirectory;
    }

    public static File getVersionsDirectory() {
        return versionsDirectory;
    }

    public static File getNativesRootDirectory() {
        return nativesRootDirectory;
    }
}
