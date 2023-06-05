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

package com.github.wohaopa.zeropointlanuch.core.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;

public class DownloadUtil {

    private static final Logger downloadLog = LogManager.getLogger("Download");

    private static int tasksCount = 0;

    static CompletionService<File> cs = ThreadUtil.newCompletionService(ThreadUtil.newExecutor(5));

    /**
     * 提交下载任务 线程安全，只在主线程调用
     *
     * @param list
     * @param downloadTmpDir
     */
    public static void submitDownloadTasks(List<String> list, File downloadTmpDir) {
        tasksCount += list.size();
        for (String url : list) {
            cs.submit(new DownloadTask(url, downloadTmpDir));
        }
    }

    /**
     * 提交下载任务 线程安全，只在主线程调用
     *
     * @param mcAssetsFileUrl
     * @param downloadTmpDir
     */
    public static void submitDownloadTasks(String mcAssetsFileUrl, File downloadTmpDir) {
        tasksCount += 1;
        cs.submit(new DownloadTask(mcAssetsFileUrl, downloadTmpDir));
    }

    /**
     * 获得所有的下载结果，
     *
     * @return 下载文件的列表
     */
    public static List<File> takeDownloadResult() throws ExecutionException, InterruptedException {
        List<File> list = new ArrayList<>();
        Future<File> result;
        int count = tasksCount;

        while (tasksCount != 0) {
            tasksCount--;
            downloadLog.debug("正在下载：{} 剩余：{}", count - tasksCount, tasksCount);
            result = cs.take();
            list.add(result.get());
        }

        return list;
    }

    static class DownloadTask implements Callable<File> {

        String url;
        File file;

        public DownloadTask(String url, File file) {
            this.url = url;
            this.file = file;
        }

        @Override
        public File call() throws Exception {
            try {
                return HttpUtil.downloadFileFromUrl(url, file);
            } catch (Exception e) {
                downloadLog.error("无法下载链接：{}", url);
            }
            return null;
        }
    }
}
