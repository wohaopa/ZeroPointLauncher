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

import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.download.DownloadProvider;
import com.github.wohaopa.zeropointlanuch.core.tasks.AssetsTask;
import com.github.wohaopa.zeropointlanuch.core.tasks.CheckoutTask;
import com.github.wohaopa.zeropointlanuch.core.tasks.LibrariesTask;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;

public class Version {

    public final String name;
    private AssetsTask assetsTask;
    private LibrariesTask libraries;
    private final JSONObject versionJsonObj;

    File versionJar;
    File natives;

    private boolean verified;

    public Version(String name, File versionJsonFile) {
        this.name = name;
        versionJsonObj = ((JSONObject) JsonUtil.fromJson(versionJsonFile));
        natives = new File(ZplDirectory.getNativesRootDirectory(), name);
    }

    public void verifyVersion() throws Exception {

        if (verified) return;

        Log.start("校验" + name);
        Log.debug("正在校验版本：{}", name);

        JSONObject downloadsObj = versionJsonObj.getByPath("downloads.client", JSONObject.class);

        String url = downloadsObj.getStr("url");
        String sha1 = downloadsObj.getStr("sha1");
        String path = "net/minecraft/client/1.7.10/client-1.7.10.jar";

        versionJar = new File(ZplDirectory.getLibrariesDirectory(), path);
        DownloadProvider.addUrlToMap(url, versionJar);
        new CheckoutTask(versionJar, sha1, null).call();

        assetsTask = new AssetsTask(ZplDirectory.getAssetsDirectory(), getAssetsIndexName(), null);
        assetsTask.call();
        libraries = new LibrariesTask(
            ZplDirectory.getLibrariesDirectory(),
            versionJsonObj.getJSONArray("libraries"),
            natives,
            getAssetsIndexName(),
            null);
        libraries.call();

        verified = true;
        Log.end();
    }

    public String getMainClass() {
        return versionJsonObj.getStr("mainClass");
    }

    private String getAssetsIndexName() {
        return versionJsonObj.getStr("assets");
    }

    private String getVersionName() {
        return name;
    }

    private String getClasspath() {
        return libraries.getClasspath() + versionJar.toString();
    }

    public List<String> getJvmArguments() {
        List<String> args = new ArrayList<>();
        args.add("-Dlog4j2.formatMsgNoLookups=true");

        // args.add("-Dlog4j.configurationFile="+logXml.toString());
        args.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
        args.add("-Dfml.ignorePatchDiscrepancies=true");
        args.add("-Dminecraft.client.jar=" + versionJar.toString());
        args.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
        args.add("-Djava.library.path=" + natives.toString());
        args.add("-Dminecraft.launcher.brand=ZPL");

        if (versionJsonObj.containsKey("arguments")) {
            List<String> as = versionJsonObj.getByPath("arguments.jvm", List.class);
            as.forEach(o -> {
                if (o.equals("${classpath}")) {
                    args.add(getClasspath());
                } else args.add(o);
            });
        } else {
            args.add("-cp");
            args.add(getClasspath());
        }
        return args;

    }

    public List<String> getGameArguments() {

        List<String> args = new ArrayList<>();

        if (versionJsonObj.containsKey("arguments")) {
            List as = versionJsonObj.getByPath("arguments.game", List.class);
            for (Object obj : as) {
                if (obj instanceof String) {
                    if (obj.equals("${version_name}")) args.add(getVersionName());
                    else if (obj.equals("${assets_root}")) args.add(
                        ZplDirectory.getAssetsDirectory()
                            .toString());
                    else if (obj.equals("${assets_index_name}")) args.add(getAssetsIndexName());
                    else args.add((String) obj);
                }
            }
        } else {
            String cmds = versionJsonObj.getStr("minecraftArguments");

            for (String cmd : cmds.split(" ")) {
                if (!cmd.isEmpty()) {
                    switch (cmd) {
                        case "${version_name}" -> args.add(getVersionName());
                        case "${assets_root}" -> args.add(
                            ZplDirectory.getAssetsDirectory()
                                .toString());
                        case "${assets_index_name}" -> args.add(getAssetsIndexName());
                        default -> args.add(cmd);
                    }
                }
            }
        }
        return args;
    }
}
