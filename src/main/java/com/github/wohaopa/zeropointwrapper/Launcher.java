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
import java.util.ArrayList;
import java.util.List;

public class Launcher {

    private String javaPath;
    private File workDir;
    private Args jvmArgs;
    private String mainClass;
    private Args gameArgs;

    public Launcher(String javaPath, File workDir, Args jvmArgs, String mainClass, Args gameArgs) {
        this.javaPath = javaPath;
        this.workDir = workDir;
        this.jvmArgs = jvmArgs;
        this.mainClass = mainClass;
        this.gameArgs = gameArgs;
    }

    public void launch() {
        ProcessBuilder pb = new ProcessBuilder();
        List<String> cmds = new ArrayList<>();

        cmds.add(javaPath);
        cmds.add(jvmArgs.toString());
        cmds.add(mainClass);
        cmds.add(gameArgs.toString());

        pb.command(cmds);
        pb.directory(workDir);

        try {
            pb.start();
        } catch (IOException e) {
            Log.LOGGER.fatal(e);
            throw new RuntimeException(e);
        }
    }

    public static class Args {

        private final List<String> pram;

        public Args(List<String> pram) {
            this.pram = pram;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            pram.forEach(s -> {
                sb.append(s);
                sb.append(" ");
            });
            return sb.toString();
        }
    }
}
