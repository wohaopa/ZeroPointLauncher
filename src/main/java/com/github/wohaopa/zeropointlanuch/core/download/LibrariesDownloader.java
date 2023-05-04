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
import java.util.List;

import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;

public class LibrariesDownloader {

    public static void verifyLibraries(File librariesDir, File versionJsonFile) {
        List<JSONObject> libs = ((JSONObject) JsonUtil.fromJson(versionJsonFile))
            .getBeanList("libraries", JSONObject.class);
    }

    // public static void verifyLibraries(File librariesDir, File versionJsonFile)
    // throws ExecutionException, InterruptedException {
    //
    // List<JSONObject> libs = ((JSONObject) JsonUtil.fromJson(versionJsonFile))
    // .getBeanList("libraries", JSONObject.class);
    // Map<File, File> nameToFile = new HashMap<>(); // 存储文件名与文件位置的映射
    // List<String> downloadQueue = new ArrayList<>(); // 下载队列
    //
    // libs.forEach(lib -> {
    // String name = (String) lib.get("name");
    // String path = LibrariesDownloader.transition(name);
    // File libraryFile = new File(librariesDir, path);
    //
    // _LibObj downloadObj = LibrariesDownloader.getDownloadUrl(path, lib);
    // String fileNameStr = downloadObj.url.substring(downloadObj.url.lastIndexOf('/') +
    // 1);
    //
    // if (!libraryFile.exists() || !FileUtil.checkSha1OfFile(libraryFile,
    // downloadObj.sha1)) {
    //
    // /* if (libraryFile.exists()) libraryFile.delete(); */
    // // 先删除错误的文件，没有则此项无效
    //
    // File file = new File(DirTools.tmpDir, fileNameStr);
    // nameToFile.put(file, libraryFile);
    // if (!(file.exists() && FileUtil.checkSha1OfFile(file, downloadObj.sha1))) {
    // downloadQueue.add(downloadObj.url);
    // }
    // }
    // });
    //
    // // 提交下载任务
    // DownloadUtil.submitDownloadTasks(downloadQueue, DirTools.tmpDir);
    // // 等待下载完成
    // DownloadUtil.takeDownloadResult();
    // // 移动文件
    // nameToFile.forEach((file, file2) -> FileUtil.moveFile(file, file2, true));
    // }
    //
    // private static _LibObj getDownloadUrl(String path, JSONObject entries) {
    // JSONArray rulesObj = entries.get("rules", JSONArray.class);
    // if(rulesObj!=null){
    // for (JSONObject jsonObject : rulesObj.jsonIter()){
    // if(jsonObject.get("action",String.class).equals("allow")){
    // if(!jsonObject.getByPath("os.osx",String.class).equals(OSUtil.getOS())){
    // return ;
    // }
    // }
    // }
    // }
    // _LibObj obj = entries.getByPath("downloads.artifact", _LibObj.class);
    // if (obj == null) obj = new _LibObj();
    // if (obj.path == null) obj.path = path; // 懒得写逻辑了，我知道mojang的网站没有，但我就是请求它的，诶嘿
    // if (obj.url == null) obj.url = DownloadProvider.getProvider()
    // .getLibrariesBaseUrl() + path;
    // /* if (obj.sha1 == null) */
    // // 那就空去吧，不想在这搞异步下载
    // return obj;
    // }

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
}

class DownloadObj {

    String name;
    String url;
    String path;
    String sha1;
}

class DownloadObjRuler extends DownloadObj {

    boolean allowWindows;
    boolean allowLinux;
    boolean allowOSX;
}

class DownloadObjExtract extends DownloadObjRuler {
}

class _LibObj {

    String path;
    String url;
    String sha1;
    int size;

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public String getSha1() {
        return sha1;
    }

    public int getSize() {
        return size;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
