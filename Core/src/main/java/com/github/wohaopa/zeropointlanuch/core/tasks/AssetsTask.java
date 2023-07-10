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

package com.github.wohaopa.zeropointlanuch.core.tasks;

import java.io.File;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;

public class AssetsTask extends Task<Boolean> {

    private final static List<String> verified = new ArrayList<>();

    private final File assetsDir;
    private final String versionString;

    public AssetsTask(File assetsDir, String versionString, Consumer<String> callback) {
        super(callback);
        this.assetsDir = assetsDir;
        this.versionString = versionString;
    }

    @Override
    public Boolean call() throws Exception {
        synchronized (verified) {
            if (verified.contains(versionString)) return true;
            verified.add(versionString);
            verifyAssets();
        }

        return true;
    }

    private void verifyAssets() throws Exception {

        Log.start("assets文件夹校验");

        File mcAssetsFile = new File(assetsDir, "indexes/" + versionString + ".json");
        File objectsDir = FileUtil.initAndMkDir(assetsDir, "objects");

        new CheckoutTask(mcAssetsFile, "", callback).call();

        JSONObject mcAssetsJsonObj = (JSONObject) JsonUtil.fromJson(mcAssetsFile).getByPath("objects");

        Set<File> files = new HashSet<>();

        mcAssetsJsonObj.forEach((s, o) -> {
            String hash = (String) ((JSONObject) o).get("hash");
            String path = hash.substring(0, 2) + "/" + hash;
            File file = new File(objectsDir, path);
            files.add(file);
        });

        List<Future<File>> futures = new ArrayList<>();
        if (files.size() != 0) {
            for (File file : files) {
                futures.add(Scheduler.submitTasks(new CheckoutTask(file, file.getName(), callback)));
            }

            for (Future<File> file : futures) {
                file.get();
            }
        }

        Log.debug("校验完成");
        Log.end();

    }
}
