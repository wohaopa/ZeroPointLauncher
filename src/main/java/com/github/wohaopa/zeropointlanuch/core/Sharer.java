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

import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;

public class Sharer {

    private static Map<String, Sharer> inst = new HashMap<>();

    static {
        inst.put("Common", new Sharer("Common", FileUtil.initAndMkDir(DirTools.shareDir, "Common"), null));
        inst.put("Java8", new Sharer("Java8", FileUtil.initAndMkDir(DirTools.shareDir, "Java8"), "Common"));
        inst.put("Java17", new Sharer("Java17", FileUtil.initAndMkDir(DirTools.shareDir, "Java17"), "Common"));
        inst.put("HMCL", new Sharer("HMCL", FileUtil.initAndMkDir(DirTools.shareDir, "HMCL"), "Common"));
    }

    public static Sharer get(String name) {
        return inst.get(name);
    }

    public File rootDir;
    public String name;
    public String parent;


    private Sharer(String name, File rootDir, String parent) {
        this.rootDir = rootDir;
        this.name = name;
        this.parent = parent;
        File file = new File(rootDir, "zpl_margi_config.json");
        if (!file.exists()) {
            FileUtil.fileWrite(
                file,
                Mapper.defaultJson()
                    .toJSONString(4));
        }
    }

    public static void execute(String name, Mapper mapper) {

        try {
            Sharer sharer = get(name);
            mapper.execute(sharer.name, sharer.rootDir);
            if (sharer.parent != null && !sharer.parent.isEmpty()) {
                Sharer sharerP = get(sharer.parent);
                mapper.execute(sharerP.name, sharerP.rootDir);
            }
        } catch (Exception e) {
            Log.LOGGER.error("未找到Sharer或其依赖：{}", name);
            throw new RuntimeException(e);
        }
    }
}
