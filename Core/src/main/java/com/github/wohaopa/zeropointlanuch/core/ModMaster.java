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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.io.resource.ResourceUtil;

public class ModMaster {

    private static final Map<String, String> modiMap;

    static {
        modiMap = new HashMap<>();
        String mapStr = ResourceUtil.readStr("zpl-mod-repo.map", Charset.defaultCharset())
            .replace("\r", "");

        String[] mapStrLine = mapStr.split("\n");
        Arrays.stream(mapStrLine)
            .forEach(line -> {
                String[] tmp = line.split("->");
                modiMap.put(tmp[0], tmp[1]);
            });
    }

    public static String getModRepo(String modFileName) {
        for (String key : modiMap.keySet()) {
            if (modFileName.startsWith(key)) {
                return modiMap.get(key);
            }
        }
        return "_default";
    }

    public static String getModFileName(String mod) {
        return mod.substring(mod.lastIndexOf("\\") + 1);
    }

    public static File getModFile(String modName) {
        return null;
    }
}
