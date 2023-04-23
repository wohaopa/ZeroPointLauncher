/*
 * The MIT License (MIT)
 * Copyright © 2023 初夏同学
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.wohaopa.zeropointwrapper;

import java.io.File;

import com.github.wohaopa.zeropointwrapper.utils.FileUtil;

public class DirTools {

    public static File workDir;
    public static File instancesDir;
    public static File librariesDir;
    public static File assetsDir;
    public static File modsDir;
    public static File zipDir;
    public static File shareDir;

    public static void init(File workDir) {
        Log.LOGGER.info("目录管理初始化开始");
        DirTools.workDir = workDir;
        DirTools.instancesDir = FileUtil.initAndMkDir(workDir, "instances");
        DirTools.librariesDir = FileUtil.initAndMkDir(workDir, "libraries");
        DirTools.assetsDir = FileUtil.initAndMkDir(workDir, "assets");
        DirTools.modsDir = FileUtil.initAndMkDir(workDir, "mods");
        DirTools.zipDir = FileUtil.initAndMkDir(workDir, "zip");
        DirTools.shareDir = FileUtil.initAndMkDir(workDir, "share");
        Log.LOGGER.info("目录管理初始化结束");
    }
}
