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

package com.github.wohaopa.zeropointwrapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;

public class Main {

    public static String launcherVersion = "内部测试版本";
    public static boolean DEBUG = false;

    public static Map<String, ICommand> cmds = new HashMap<>();

    public static void main(String[] args) {

        if (DEBUG) {
            test();
            return;
        }

        if (!Boolean.getBoolean(System.getProperty("zp.skipLib"))) {
            librariesCheckAndDown();
        }

        Log.LOGGER.info("ZeroPoint启动器核心启动");

        DirTools.init(new File("D:\\ZeroPointServer\\Launcher\\Test"));
        Main.init();

        if (args.length == 0) {
            appProcess();
        } else {
            commandProcess(args);
        }

        Log.LOGGER.info("ZeroPoint启动器核心关闭");
    }

    private static void librariesCheckAndDown() {}

    public static void test() {

        // 当无法识别页面编码的时候，可以自定义请求页面的编码
        String result2 = HttpUtil.get("http://127.0.0.1", CharsetUtil.CHARSET_UTF_8);
        System.out.println(result2);
    }

    private static void init() {
        Class<Command> clazz = Command.class;
        Field[] fields = clazz.getFields();
        try {
            for (Field field : fields) {
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
            // "genRunDir <实例名/all> - 生成一个实例的.minecraft目录，其可被hmcl/pcl等启动器启动\n"
            // "genLwjgl3ify <实例名> - 生成Java17版本\n"
            // "launch <实例名> - 启动一个实例\n"
            // "login <offline> <用户名> - 登录账户\n"

            // translation 2.3.2 "D:\ZeroPointServer\Launcher\Test\汉化\2.3.0_complete.zip"
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
