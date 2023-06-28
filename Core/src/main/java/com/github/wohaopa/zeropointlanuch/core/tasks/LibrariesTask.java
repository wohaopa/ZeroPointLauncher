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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.download.DownloadProvider;

public class LibrariesTask extends Task<Boolean> {

    private boolean verified = false;

    private String classpath;
    private final File librariesDir;
    private final JSONArray librariesObj;
    private final File natives;
    private final String versionString;

    public LibrariesTask(File librariesDir, JSONArray librariesObj, File natives, String versionString,
        Consumer<String> callback) {
        super(callback);
        this.librariesDir = librariesDir;
        this.librariesObj = librariesObj;
        this.natives = natives;
        this.versionString = versionString;
    }

    @Override
    public Boolean call() throws Exception {

        synchronized (this) {
            if (verified) return true;
            verifyLibraries();
        }

        return true;
    }

    private void verifyLibraries() throws ExecutionException, InterruptedException {

        Log.start("Libraries校验");
        List<_LibraryBase> libraries = new ArrayList<>();

        for (JSONObject lib : librariesObj.jsonIter()) libraries.add(new _LibraryBase(lib));

        List<Future<File>> futures = new ArrayList<>();
        List<Future<File>> futuresNative = new ArrayList<>();
        for (_LibraryBase libraryBase : libraries) {
            if (libraryBase.enable) {
                Log.debug("正在校验：{},相对路径：{}", libraryBase.name, libraryBase.path);
                File file = new File(librariesDir, libraryBase.path);
                DownloadProvider.addUrlToMap(libraryBase.url, file);
                Future<File> future = Scheduler.submitTasks(new CheckoutTask(file, libraryBase.sha1, callback));
                if (libraryBase.natives) futuresNative.add(future);
                else futures.add(future);

            }
        }

        for (Future<File> file : futuresNative) {
            Log.debug(
                "解压本地库:{}",
                file.get()
                    .getName());
            Scheduler.submitTasks(
                new DecompressTask(
                    file.get(),
                    natives,
                    archiveEntry -> archiveEntry.getName()
                        .equals("META-INF"),
                    callback));
        }
        StringBuilder sb = new StringBuilder();
        String s = ";";
        for (Future<File> file : futures) {
            Log.debug(
                "校验完成:{}",
                file.get()
                    .getName());
            sb.append(file.get())
                .append(s);

        }

        classpath = sb.toString();
        verified = true;
        Log.debug("校验完成！");
        Log.end();

    }

    public String getClasspath() {
        if (verified) return classpath;
        Log.error("未初始化：{}", versionString);
        throw new RuntimeException("未初始化！请先执行Task");
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
