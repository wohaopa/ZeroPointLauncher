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

import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.download.DownloadProvider;
import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;

public class CheckoutTask extends DownloadTask {

    String sha1;

    public CheckoutTask(File file, String sha1, Consumer<String> callback) {
        super(null, file, callback);
        this.sha1 = sha1;
    }

    @Override
    public File call() throws Exception {
        if (!FileUtil.checkSha1OfFile(file, sha1)) {
            if (file.exists() && !file.delete()) {
                accept("无法删除文件：" + file);
                Log.error("无法删除文件：{}", file);
            }
            Log.error("检验失败，正准备下载文件：{}", file);
            accept("检验失败，正准备下载文件：" + file);
            url = DownloadProvider.getUrlForFile(file);
            return super.call();
        }
        Log.debug("检验成功！{}", file);
        accept("检验成功！ " + file);
        return file;
    }
}
