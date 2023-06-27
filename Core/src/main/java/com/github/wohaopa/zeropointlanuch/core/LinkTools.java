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
import java.io.IOException;

import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;

public class LinkTools {

    private static boolean enable;
    private static final File fileJar = new File(
        ZplDirectory.getWorkDirectory()
            .getParentFile(),
        "lib\\MappingTools.jar");
    private static final File cmdFile = new File(
        ZplDirectory.getWorkDirectory()
            .getParentFile(),
        "lib\\MappingTools.bat");

    static {
        if (!fileJar.isFile()) Log.warn("无法加载映射工具！");
        else {}
    }

    public static void doLink(File file) throws IOException {
        if (!cmdFile.exists()) {
            String str = """
                @echo off
                set cwd=%~dp0
                title ZeroPointLauncher

                >nul 2>&1 "%SYSTEMROOT%\\system32\\cacls.exe" "%SYSTEMROOT%\\system32\\config\\system"
                if '%errorlevel%' NEQ '0' (
                goto UACPrompt
                ) else ( goto gotAdmin )
                :UACPrompt
                echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\\getadmin.vbs"
                echo UAC.ShellExecute "%~s0", "", "", "runas", 1 >> "%temp%\\getadmin.vbs"
                "%temp%\\getadmin.vbs"
                exit /B
                :gotAdmin


                chcp 65001>nul
                echo 本程序需要使用管理员权限来完成文件链接的创建。本程序完全开源，不存在其他操作行为。
                cd /d %cwd%
                java -Dfile.encoding=utf8 -jar """ + fileJar + """
                 %1

                echo 按任意键退出
                pause>nul
                """;
            FileUtil.fileWrite(cmdFile, str);
        }
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(ZplDirectory.getWorkDirectory());
        processBuilder.command(cmdFile.toString(), file.toString());
        processBuilder.start();
    }

}
