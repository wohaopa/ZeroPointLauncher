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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.wohaopa.zeropointlanuch.core.filesystem.MyDirectory;
import com.github.wohaopa.zeropointlanuch.core.filesystem.MyFileBase;

public class Sharer {

    private static final Map<String, Sharer> inst = new HashMap<>();

    static {
        new Sharer("Common", "null");
        new Sharer("Java8", "Common");
        new Sharer("Java17", "Common");
        new Sharer("HMCL", "Common");
    }

    public static Sharer get(String name) {
        if (inst.containsKey(name)) return inst.get(name);
        throw new RuntimeException("未知分享器：" + name);
    }

    public static Set<String> getNames() {
        return inst.keySet();
    }

    public final File rootDir;
    public final String name;
    public final String parent;
    private MyDirectory myDirectory;

    private Sharer(String name, String parent) {
        this.rootDir = new File(ZplDirectory.getShareDirectory(), name);
        this.name = name;
        this.parent = parent;

        inst.put(name, this);
    }

    public MyDirectory getMyDirectory() {
        if (myDirectory != null) return myDirectory;
        myDirectory = (MyDirectory) MyFileBase.getMyFileSystemByFile(rootDir, null);
        return myDirectory;
    }
}
