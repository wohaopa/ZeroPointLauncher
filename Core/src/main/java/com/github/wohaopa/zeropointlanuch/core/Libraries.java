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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.download.DownloadProvider;
import com.github.wohaopa.zeropointlanuch.core.utils.DownloadUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.ZipUtil;

public class Libraries {

    String classpath;
    private boolean verified;

    public void verifyLibraries(File librariesDir, JSONArray librariesObj, File natives)
        throws ExecutionException, InterruptedException {

        if (verified) return;

        Log.start("Libraries校验");
        List<_LibraryBase> libraries = new ArrayList<>();

        for (JSONObject lib : librariesObj.jsonIter()) libraries.add(new _LibraryBase(lib));

        List<File> libFiles = new ArrayList<>();
        List<_LibraryBase> failLib = new ArrayList<>();
        List<_LibraryBase> failNativeLib = new ArrayList<>();

        for (_LibraryBase lib : libraries) {
            if (lib.enable) {

                Log.debug("正在校验：{},相对路径：{}", lib.name, lib.path);

                File file = new File(librariesDir, lib.path);
                if (!FileUtil.checkSha1OfFile(file, lib.sha1)) {
                    file.delete();
                    failLib.add(lib);
                }

                if (lib.natives) {
                    if (file.exists()) {
                        Log.debug("解压natives:{}", lib.name);
                        ZipUtil.unCompress(file, natives);
                        if (lib.extract) lib.exclude.forEach(s -> new File(natives, s).delete());
                    } else failNativeLib.add(lib);

                } else libFiles.add(file);

            }
        }

        if (failLib.size() != 0) {
            Log.debug("缺失{}个文件", failLib.size());
            File tmpDir = ZplDirectory.getTmpDirectory();

            List<String> downloads = new ArrayList<>();
            for (_LibraryBase lib : failLib) {
                String name = lib.getFileName();
                if (!FileUtil.checkSha1OfFile(new File(tmpDir, name), name)) {
                    Log.debug("正在将{}文件加入下载队列", name);
                    downloads.add(
                        DownloadProvider.getProvider()
                            .getLibrariesUrl(lib.url));
                }
            }

            DownloadUtil.submitDownloadTasks(downloads, tmpDir);
            Log.debug("正在补全缺失的{}个文件", downloads.size());
            DownloadUtil.takeDownloadResult();

            for (_LibraryBase lib : failLib) {
                FileUtil.moveFile(new File(tmpDir, lib.getFileName()), new File(librariesDir, lib.path), true);
            }

            for (_LibraryBase lib : failNativeLib) {
                Log.debug("解压natives:{}", lib.name);
                ZipUtil.unCompress(new File(librariesDir, lib.path), natives);
                if (lib.extract) lib.exclude.forEach(s -> FileUtil.delete(new File(natives, s)));
            }
        }

        StringBuilder sb = new StringBuilder();
        String s = ";";
        libFiles.forEach(
            file -> sb.append(file.toString())
                .append(s));
        classpath = sb.toString();
        verified = true;
        Log.debug("校验完成！");
        Log.end();

    }

    private static String transition(String name) {
        String[] tmp = name.split(":");
        StringBuilder sb = new StringBuilder();

        sb.append(tmp[0].replace(".", "/"));
        sb.append('/');
        sb.append(tmp[1]);
        sb.append('/');
        sb.append(tmp[2]);
        sb.append('/');
        sb.append(tmp[1]);
        sb.append('-');
        sb.append(tmp[2]);
        if (tmp.length == 4) {
            sb.append('-');
            sb.append(tmp[3]);
        }
        sb.append(".jar");
        return sb.toString();
    }

    private static class _LibraryBase {

        String name;
        boolean enable = true;

        // enable
        String url;
        String sha1;

        boolean natives = false;
        // native library
        String nativesStr;
        boolean extract;
        List<String> exclude;

        // normal library
        String path;

        private _LibraryBase(JSONObject object) {
            name = object.getStr("name");
            natives = object.containsKey("natives");
            Log.debug("正在处理：{}", name);
            if (natives) {
                nativesStr = object.getByPath("natives.windows", String.class);
                if (nativesStr != null) {

                    if (object.containsKey("rules")) for (JSONObject rule : object.getJSONArray("rules")
                        .jsonIter())
                        if ("allow".equals(rule.get("action")) && rule.containsKey("os"))
                            enable = "windows".equals(rule.getByPath("os.name"));
                        else if ("disallow".equals(rule.get("action")) && rule.containsKey("os"))
                            enable = !"windows".equals(rule.getByPath("os.name"));

                    if (!enable) return;

                    nativesStr = nativesStr.replace("${arch}", "64");

                    extract = object.containsKey("extract");
                    if (extract) {
                        exclude = object.getByPath("extract.exclude", List.class);
                    }

                    JSONObject jsonObject = object.getByPath("downloads.classifiers." + nativesStr, JSONObject.class);
                    url = jsonObject.getStr("url");
                    path = jsonObject.getStr("path");
                    sha1 = jsonObject.getStr("sha1");
                } else enable = false;
            } else {
                if (object.containsKey("rules")) for (JSONObject rule : object.getJSONArray("rules")
                    .jsonIter())
                    if ("allow".equals(rule.get("action")) && rule.containsKey("os"))
                        enable = "windows".equals(rule.get("os.name"));
                    else if ("disallow".equals(rule.get("action")) && rule.containsKey("os"))
                        enable = !"windows".equals(rule.get("os.name"));

                if (!enable) return;

                JSONObject jsonObject = object.getByPath("downloads.artifact", JSONObject.class);
                path = jsonObject.getStr("path");
                sha1 = jsonObject.getStr("sha1");
                url = jsonObject.getStr("url");
            }
        }

        public String getFileName() {
            return url.substring(url.lastIndexOf('/') + 1);
        }
    }
}
