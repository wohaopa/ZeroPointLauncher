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

package com.github.wohaopa.zeropointlanuch.main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import com.github.wohaopa.zeropointlanuch.core.DirTools;
import com.github.wohaopa.zeropointlanuch.core.Log;

public class Main {

    public static String launcherVersion = "内部测试版本";
    public static boolean DEBUG = true;

    public static Map<String, ICommand> cmds = new HashMap<>();

    public static void main(String[] args) {

        if (DEBUG) {
            // DownLoadUtil.downloadFile(
            // "http://127.0.0.1/ZPL/standard/GT_New_Horizons_2.3.0_Client.zip",
            // new File("D:\\test\\file"));
            return;
        }

        long a = System.currentTimeMillis();
        Log.LOGGER.debug("ZeroPoint启动器核心正在启动...");
        Main.init();
        long b = System.currentTimeMillis();
        Log.LOGGER.debug("ZeroPoint启动器核心启动成功，用时：{}ms", b - a);

        if (args.length == 0) {
            appProcess();
        } else {
            commandProcess(args);
        }

        Log.LOGGER.info("ZeroPoint启动器核心关闭");
        System.exit(0); // 关闭用于下载的线程池
    }

    private static void init() {

        String rootDirStr = System.getProperty("zpl.rootdir");

        if (rootDirStr == null) {
            rootDirStr = System.getProperty("user.dir");
        }
        rootDirStr = "D:\\ZeroPointServer\\Launcher\\Test";

        DirTools.init(new File(rootDirStr));

        Class<Command> clazz = Command.class;
        Field[] fields = clazz.getFields();
        try {
            for (Field field : fields) {
                cmds.put(field.getName(), (ICommand) field.get(null));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        Class<GenCommand> clazz1 = GenCommand.class;
        Field[] fields1 = clazz1.getFields();
        try {
            for (Field field : fields1) {
                cmds.put(field.getName(), (ICommand) field.get(null));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void commandProcess(String[] args) {}

    private static void appProcess() {
        Scanner sc = new Scanner(System.in, "UTF-8");
        System.out.println("GTNHLauncher - " + launcherVersion + " by 初夏同学");

        boolean exit = false;

        try {
            Command.lookup.execute(null);
            Command.help.execute(null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        do {

            // "download <链接/标准版本名> - 下载一个版本\n"
            // "incremental <实例名> <版本号/更新程序路径> -
            // 增量更新版本，将会自动下载所需文件。本启动器独有的更新方式，支持版本有限，敬请期待后续更新。\n"
            // "update <实例名> <版本号> - 升级一个实例\n"
            // "translate <实例名> - 汉化一个实例 \n"
            // "genLwjgl3ify <实例名> - 生成Java17版本\n"
            // "launch <实例名> - 启动一个实例\n"
            // "login <offline> <用户名> - 登录账户\n"

            String cmdStrLine = sc.nextLine(); // IDEA中的终端支持中文，cmd不支持，我吐了！！！

            String[] args = cmdStrLine.split(" ");
            String cmdStr = args[0];

            if (cmdStr.equals("quit")) {
                System.out.println("正在退出");
                exit = true;
                continue;
            }

            ICommand cmdObj = cmds.get(cmdStr);
            if (cmdObj == null) {
                System.out.println("未知指令：" + cmdStr);
                continue;
            }

            try {
                System.out.println(cmdStr + " - start");
                if (cmdObj.execute(args)) System.out.println(cmdStr + " - success");
                else System.out.println(cmdStr + " - warn");
            } catch (Exception e) {
                System.out.println(cmdStr + " - error");
                e.printStackTrace();
            }

        } while (!exit);
    }
}
