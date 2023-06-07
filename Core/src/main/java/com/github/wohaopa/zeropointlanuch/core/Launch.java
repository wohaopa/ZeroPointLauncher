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

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.wohaopa.zeropointlanuch.core.auth.Auth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Launch {

    private static Map<String, Launch> inst = new HashMap<>();

    static {
        new Launch("ZPL-Java8");
        new Launch("ZPL-Java17");
    }

    public static Launch getLauncher(String name) {
        return inst.get(name);
    }

    public static Set<String> getLaunches() {
        return inst.keySet();
    }

    String name;

    int maxMemory = 8192;
    int minMemory = 4096;

    String extraJvmArgs = "";
    String extraGameArgs = "";
    String javaPath;

    public Launch setJavaPath(String javaPath) {
        this.javaPath = javaPath;
        return this;
    }

    Version version;

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

    private List<String> parseArg(List<String> commands, File runDir) {
        commands.set(commands.indexOf("${game_directory}"), runDir.toString());
        return commands;
    }

    public void launch(Auth auth, File runDir) {

        try {
            version.verifyVersion();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        String[] commandLine = getLaunchArguments(auth, runDir);
        Log.debug("启动指令：{}", commandLine);

        Consumer<String> pump = new Consumer<>() {

//            static final Pattern pattern = Pattern.compile("<log4j:Message><!\\[CDATA\\[(.+)\\]\\]></log4j:Message>");

            @Override
            public void accept(String s) {
//                Matcher m = pattern.matcher(s);
//                if (m.find())
                System.out.println(s);

            }
        };

        ProcessBuilder pb = new ProcessBuilder();
        pb.command(commandLine);
        pb.directory(runDir);
        try {
            Process mc = pb.start();
            System.out.println(mc.pid());
            new Thread(new Pump(mc.getInputStream(), pump)).start();
            new Thread(new Pump(mc.getErrorStream(), pump)).start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}

class Pump implements Runnable {

    InputStream in;
    Consumer<String> callback;

    public Pump(InputStream in, Consumer<String> callback) {
        this.in = in;
        this.callback = callback;
    }

    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (Thread.currentThread()
                    .isInterrupted()) {
                    Thread.currentThread()
                        .interrupt();
                    break;
                }

                callback.accept(line);
            }
        } catch (IOException ignored) {

        }
    }
}
