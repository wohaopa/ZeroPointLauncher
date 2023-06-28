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
        File lock = new File(file.getParentFile(), "mapping.lock");
        if (lock.exists()) lock.delete();
        if (!cmdFile.exists()) {
            String str = """
                %2 mshta vbscript:CreateObject("Shell.Application").ShellExecute("cmd.exe","/c %~s0 %1 ::","","runas",1)(window.close)&&exit
                @echo off
                cd /d %~dp0
                title ZeroPointLauncher
                java -jar MappingTools-0.2.0.jar %1
                echo Done!
                pause>nul
                """;
            FileUtil.fileWrite(cmdFile, str);
        }

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(ZplDirectory.getWorkDirectory());
        processBuilder.command(cmdFile.toString(), file.toString());
        Process process = processBuilder.start();
        Log.debug("映射进程id：{}", process.pid());

        byte times = 30;
        try {
            while (times-- > 0) {
                Log.debug("正在等待5s，剩余等待次数：{}", times);
                Thread.sleep(5000);
                if (!lock.exists()) break;
            }
        } catch (InterruptedException e) {
            Log.warn("链接执行错误：{}", e);
        }
        if (lock.exists()) Log.warn("等待时间过长，可能是映射程序出现问题！");

        Log.info("映射完成！");
    }

}
