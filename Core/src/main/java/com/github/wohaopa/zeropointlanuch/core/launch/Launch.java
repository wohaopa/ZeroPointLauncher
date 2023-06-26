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

package com.github.wohaopa.zeropointlanuch.core.launch;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.ZplDirectory;
import com.github.wohaopa.zeropointlanuch.core.auth.Auth;

public class Launch {

    private static final Map<String, Launch> inst = new HashMap<>();

    static {
        Log.debug("正在初始化Launcher");
        new Launch("ZPL-Java8");
        new Launch("ZPL-Java17");
    }

    public static Launch getLauncher(String name) {
        return inst.get(name);
    }

    public static Set<String> getLaunches() {
        return inst.keySet();
    }
    // 成员属性与方法

    private final String name;
    private int maxMemory = 8192;
    private int minMemory = 4096;
    private String extraJvmArgs = "";
    private String extraGameArgs = "";
    private String javaPath;

    private final Version version;

    public Launch setJavaPath(String javaPath) {
        this.javaPath = javaPath;
        return this;
    }

    public Launch setExtraJvmArgs(String extraJvmArgs) {
        this.extraJvmArgs = extraJvmArgs;
        return this;
    }

    public Launch setExtraGameArgs(String extraGameArgs) {
        this.extraGameArgs = extraGameArgs;
        return this;
    }

    public Launch setMaxMemory(int maxMemory) {
        this.maxMemory = maxMemory;
        return this;
    }

    public Launch setMinMemory(int minMemory) {
        this.minMemory = minMemory;
        return this;
    }

    private Launch(String name) {
        this.name = name;
        this.version = new Version(name, new File(ZplDirectory.getVersionsDirectory(), name + ".json"));
        inst.put(name, this);
    }

    public String[] getLaunchArguments(Auth auth, File runDir) {

        if (javaPath == null || javaPath.isEmpty()) throw new RuntimeException("java配置错误！");
        List<String> commandLine = new ArrayList<>();

        commandLine.add(javaPath);

        commandLine.add("-Xmx" + maxMemory + "M");
        commandLine.add("-Xms" + minMemory + "M");

        if (extraGameArgs != null && !extraGameArgs.isEmpty()) {
            commandLine.addAll(Arrays.asList(extraJvmArgs.split(" ")));
        }

        commandLine.addAll(version.getJvmArguments());

        commandLine.add(version.getMainClass());

        commandLine.addAll(parseArg(auth.parseArg(version.getGameArguments()), runDir));

        if (extraGameArgs != null && !extraGameArgs.isEmpty()) {
            commandLine.addAll(Arrays.asList(extraGameArgs.split(" ")));
        }

        return commandLine.toArray(new String[0]);
    }

    public void launch(Auth auth, File runDir) {

        Log.start("启动");

        try {
            version.verifyVersion();
        } catch (Exception e) {
            Log.warn("文件校验失败。原因：{}", e);
            Log.end();
            return;
        }

        String[] commandLine = getLaunchArguments(auth, runDir);
        Log.debug("启动指令：{}", commandLine);

        final boolean[] resume = { true };

        Consumer<String> pump = new Consumer<>() {

            static final Pattern pattern = Pattern.compile("\\[[0-9:]+\\] \\[Client thread/INFO\\]: LWJGL Version: .+");

            @Override
            public void accept(String s) {
                if (pattern.matcher(s)
                    .find()) resume[0] = false;

                System.out.println(s);

            }
        };

        ProcessBuilder pb = new ProcessBuilder();
        pb.command(commandLine);
        pb.directory(runDir);
        try {
            Process mc = pb.start();
            Log.debug("进程pid：{}", mc.pid());
            new Thread(new Pump(mc.getInputStream(), pump)).start();
            new Thread(new Pump(mc.getErrorStream(), pump)).start();

        } catch (IOException e) {
            Log.warn("无法启动，错误：{}", e);
        }

        try {
            while (resume[0]) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Log.warn("无法成功能识别游戏窗口，错误：{}", e);
        }

        Log.end();
    }

    private List<String> parseArg(List<String> commands, File runDir) {
        commands.set(commands.indexOf("${game_directory}"), runDir.toString());
        return commands;
    }
}
