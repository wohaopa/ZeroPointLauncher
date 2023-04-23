package com.github.wohaopa.zeropointwrapper;

import java.io.File;

public class Sharer {

    public static final String[] specialDir = { "mods", "config", "scripts", "saves" };
    public static final String[] excludeDir = { "assets", "libraries", "versions" };
    public File rootDir;

    public Sharer(File rootDir) {
        this.rootDir = rootDir;
    }

    public static void copyFileAsLink(File runDir) {}
}
