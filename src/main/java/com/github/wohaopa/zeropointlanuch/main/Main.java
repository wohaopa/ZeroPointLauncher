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
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.api.Core;
import com.github.wohaopa.zeropointlanuch.core.DirTools;
import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.utils.DownloadUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;

public class Main {

    public static Map<String, ICommand> commands = new HashMap<>();
    public static File workDir;

    public static void main(String[] args) {

        long a = System.currentTimeMillis();
        Log.info("ZeroPoint启动器核心正在启动...");
        Main.init();
        long b = System.currentTimeMillis();
        Log.debug("ZeroPoint启动器核心启动成功，用时：{}ms", b - a);

        if (args.length == 0) {
            appProcess();
        } else {
            commandProcess(args);
        }

        Log.info("ZeroPoint启动器核心关闭");
        System.exit(0); // 关闭用于下载的线程池
    }

    private static void init() {

        // 用于本地测试，会在测试环境设置为："D:\\DevProject\\JavaProject\\ZeroPointLaunch\\Wrapper\\build\\libs\\.GTNH"
        String rootDirStr = System.getProperty("zpl.rootDir");

        if (rootDirStr == null) {
            rootDirStr = System.getProperty("user.dir") + "/.GTNH";
        }
        workDir = new File(rootDirStr);
        Core.initDirTools(workDir); // 目录工具初始化

        String skip = System.getProperty("zpl.skipUpdate");
        if (skip != null && !skip.equals("true") && !Core.launcherVersion.equals("内部测试版本")) {
            check_update();
        }

        Main.registerCommand(GenCommand.class); // 注册指令
        Main.registerCommand(Command.class);
    }

    private static void check_update() {

        File configProperties = new File(workDir.getParentFile(), "config.properties");
        if (!configProperties.exists()) return;
        Properties properties = new Properties();

        try {
            properties.load(Files.newInputStream(configProperties.toPath()));
        } catch (IOException e) {
            Log.info("无法加载config.properties，跳过更新检查。");
            return;
        }

        String DOWNLOAD_VERSION_URL = properties.getProperty("download-url");

        Logger updateLog = LogManager.getLogger("Update");
        Runnable runnable = () -> {
            File versionFile = new File(DirTools.tmpDir, "version.json");
            updateLog.info("开始检查更新");
            DownloadUtil.submitDownloadTasks(DOWNLOAD_VERSION_URL + "version.json", versionFile);
            try {
                DownloadUtil.takeDownloadResult();
            } catch (ExecutionException | InterruptedException e) {
                updateLog.info(e);
            }
            if (!versionFile.exists()) {
                updateLog.info("未能成功加载版本信息，跳过更新");
                return;
            }
            JSONObject versionJson = (JSONObject) JsonUtil.fromJson(versionFile);
            String latestVersion = (String) versionJson.get("latest");
            boolean flag = false;
            {
                String[] a = Core.launcherVersion.split("\\.");
                String[] b = latestVersion.split("\\.");
                for (int i = 0; i < a.length; i++) {
                    if (Integer.parseInt(b[i]) > Integer.parseInt(a[i])) {
                        flag = true;
                        break;
                    }
                }
            }
            if (flag) {
                updateLog.info("发现更新，正在下载更新");
                String downloadUrl = DOWNLOAD_VERSION_URL + versionJson.get("download-url");
                DownloadUtil.submitDownloadTasks(
                    downloadUrl,
                    new File(workDir.getParentFile(), "lib\\ZeroPointLaunch-Core.jar"));
            }
            versionFile.delete();
        };
        Thread thread = new Thread(runnable);
        thread.setName("update");
        thread.start();
    }

    private static void registerCommand(Class<?> commandClass) {
        Field[] fields = commandClass.getFields();
        try {
            for (Field field : fields) {
                commands.put(field.getName(), (ICommand) field.get(null));
            }
        } catch (IllegalAccessException e) {
            Log.error("无法注册指令，错误：{}", e.getMessage());
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
                System.out.println("start - " + cmdStr);
                if (cmdObj.execute(args)) System.out.println("success - " + cmdStr);
                else System.out.println("warn - " + cmdStr);
            } catch (Exception e) {
                System.out.println("error - " + cmdStr);
                e.printStackTrace();
            }

        } while (!exit);
    }
}
