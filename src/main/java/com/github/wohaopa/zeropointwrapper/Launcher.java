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
