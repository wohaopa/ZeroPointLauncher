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

import com.github.wohaopa.zeropointlanuch.api.Core;
import com.github.wohaopa.zeropointlanuch.core.Log;

public class Main {

    public static Map<String, ICommand> commands = new HashMap<>();
    public static File workDir;

    public static void main(String[] args) {

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
            rootDirStr = System.getProperty("user.dir") + "/.GTNH";
        }
        workDir = new File(rootDirStr);

        Core.initDirTools(workDir); // 目录工具初始化
        Main.registerCommand(GenCommand.class); // 注册指令
        Main.registerCommand(Command.class);
    }

    private static void registerCommand(Class<?> commandClass) {
        Field[] fields = commandClass.getFields();
        try {
            for (Field field : fields) {
                commands.put(field.getName(), (ICommand) field.get(null));
            }
        } catch (IllegalAccessException e) {
            Log.LOGGER.error("无法注册指令，错误：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void commandProcess(String[] args) {}

    private static void appProcess() {
        Scanner sc = new Scanner(System.in, "UTF-8");
        System.out.println("GTNHLauncher - " + Core.launcherVersion + " by 初夏同学");

        boolean exit = false;

        try {
            commands.get("lookup")
                .execute(null);
            commands.get("help")
                .execute(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        do {
            String cmdStrLine = sc.nextLine(); // IDEA中的终端支持中文，cmd不支持，开摆

            String[] args = cmdStrLine.split(" ");
            String cmdStr = args[0];

            if (cmdStr.equals("quit")) {
                System.out.println("正在退出");
                exit = true;
                continue;
            }

            ICommand cmdObj = commands.get(cmdStr);
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
