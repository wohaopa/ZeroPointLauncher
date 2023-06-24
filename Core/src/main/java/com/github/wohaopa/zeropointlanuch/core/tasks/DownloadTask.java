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
import java.util.function.Consumer;

import cn.hutool.core.io.StreamProgress;
import cn.hutool.http.HttpUtil;

import com.github.wohaopa.zeropointlanuch.core.Log;

public class DownloadTask extends Task<File> {

    protected String url;
    protected final File file;

    public DownloadTask(String url, File file, Consumer<String> callback) {
        super(callback);
        this.url = url;
        this.file = file;
    }

    @Override
    public File call() throws Exception {

        if (url != null) try {
            Log.debug("开始下载：{}", url);
            return HttpUtil.downloadFileFromUrl(url, file, havaCallback() ? new StreamProgress() {

                byte timer = 0;

                long last = System.currentTimeMillis();

                @Override
                public void start() {
                    accept("开始下载");
                }

                @Override
                public void progress(long total, long progressSize) {
                    if (timer++ != 5) return;
                    timer = 0;
                    long cur = System.currentTimeMillis();
                    if (cur - last > 1000) {
                        accept(String.format("正在下载：%.2f %%", (double) progressSize / total * 100));
                        last = cur;
                    }
                }

                @Override
                public void finish() {
                    accept("开始完成");
                }
            } : null);
        } catch (Exception e) {
            Log.error("无法下载链接：{}", url);
        }
        return null;
    }

}
