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
