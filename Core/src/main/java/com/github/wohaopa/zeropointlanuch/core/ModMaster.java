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
import java.util.*;

import cn.hutool.core.io.resource.ResourceUtil;

import com.github.wohaopa.zeropointlanuch.core.download.DownloadProvider;
import com.github.wohaopa.zeropointlanuch.core.tasks.DownloadTask;
import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;

public class ModMaster {

    private static final Map<String, String> modiMap;
    private static final Map<String, String> cache = new HashMap<>();

    static {
        modiMap = new HashMap<>();
        String mapStr = ResourceUtil.readStr("zpl-mod-repo.map", Charset.defaultCharset()).replace("\r", "");

        String[] mapStrLine = mapStr.split("\n");
        Arrays.stream(mapStrLine).forEach(line -> {
            String[] tmp = line.split("->");
            modiMap.put(tmp[0], tmp[1]);
        });
    }

    public static String getModRepo(String modFileName) {
        if (cache.containsKey(modFileName)) return cache.get(modFileName);

        for (String key : modiMap.keySet()) {
            if (modFileName.startsWith(key)) {
                String tmp = modiMap.get(key);
                cache.put(modFileName, tmp);
                return tmp;
            }
        }
        return "_default";
    }

    public static List<String> coverModsList(File modsDir) {
        List<String> mods = new LinkedList<>();
        if (!modsDir.exists()) return mods;
        for (File mod : Objects.requireNonNull(modsDir.listFiles())) {
            if (mod.isFile() && !mod.getName().startsWith("MrTJPCore")) {
                String modFileName = mod.getName();
                String modRepo = ModMaster.getModRepo(modFileName);
                String path = modRepo + "\\" + modFileName;
                mods.add(path);
                if (!FileUtil.moveFile(mod, new File(ZplDirectory.getModsDirectory(), path))) {
                    Log.info("已在mod库中发现：{} 即将删除临时文件", modFileName);
                    if (!mod.delete()) Log.warn("文件：{} 删除失败，可能被占用，请手动删除", mod.getPath());
                }
            }
        }
        return mods;
    }

    public static File getModFile(String modFullName) throws Exception {
        File modFile = new File(ZplDirectory.getModsDirectory(), modFullName);
        if (!modFile.exists()) return getRemoteMod(modFile);
        return modFile;
    }

    private static File getRemoteMod(File modFile) throws Exception {
        return new DownloadTask(DownloadProvider.getUrlForFile(modFile), modFile, null).call();
    }
}
