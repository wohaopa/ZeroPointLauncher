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

package com.github.wohaopa.zeropointlanuch.core.download;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.ZplDirectory;
import com.github.wohaopa.zeropointlanuch.core.utils.DownloadUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;

public class AssetsDownloader {

    public static void verifyAssets(File assetsDir) throws ExecutionException, InterruptedException {
        Log.start("assets文件夹校验");
        List<File> fail = new ArrayList<>();
        File mcAssetsFile = new File(assetsDir, "indexes/1.7.10.json");
        File objectsDir = FileUtil.initAndMkDir(assetsDir, "objects");
        File tmpDir = ZplDirectory.getTmpDirectory();
        if (!mcAssetsFile.exists()) {
            Log.debug("正在下载indexes/1.7.10.json文件");
            String mcAssetsFileUrl = DownloadProvider.getProvider()
                .getAssetsIndexJsonUrl();
            DownloadUtil.submitDownloadTasks(mcAssetsFileUrl, tmpDir);
            List<File> res = DownloadUtil.takeDownloadResult();

            FileUtil.moveFile(res.get(0), mcAssetsFile);
        }

        JSONObject mcAssetsJson = (JSONObject) JsonUtil.fromJson(mcAssetsFile)
            .getByPath("objects");

        mcAssetsJson.forEach((s, o) -> {
            String hash = (String) ((JSONObject) o).get("hash");
            // int size = (int) ((JSONObject)o).get("size");
            String path = hash.substring(0, 2) + "/" + hash;
            File file = new File(objectsDir, path);
            if (!FileUtil.checkSha1OfFile(file, hash)) fail.add(file);
        });

        // 补全文件
        List<String> downloads = new ArrayList<>();
        for (File file : fail) {
            String name = file.getName();
            if (!FileUtil.checkSha1OfFile(new File(tmpDir, name), name)) {
                Log.debug("正在将{}文件加入下载队列", name);
                downloads.add(
                    DownloadProvider.getProvider()
                        .getAssetsObjUrl(name));
            }
        }

        DownloadUtil.submitDownloadTasks(downloads, tmpDir);
        Log.debug("正在补全缺失的{}个文件", downloads.size());
        DownloadUtil.takeDownloadResult();

        for (File file : fail) {
            FileUtil.moveFile(new File(tmpDir, file.getName()), file, true);
        }
        Log.end();
    }
}
