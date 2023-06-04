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

import com.github.wohaopa.zeropointlanuch.core.ModMaster;
import com.github.wohaopa.zeropointlanuch.core.ZplDirectory;
import com.github.wohaopa.zeropointlanuch.core.utils.DownloadUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;

public class ModDownloader {

    private static final String BASE_MOD_URL = DownloadProvider.getProvider()
        .getModsUrlBase();

    public static void downloadAll(List<String> needDownload) {

        List<String> urls = new ArrayList<>();

        for (String s : needDownload) {
            urls.add(BASE_MOD_URL + s);
        }

        DownloadUtil.submitDownloadTasks(urls, ZplDirectory.getTmpDirectory());
        List<File> files;

        try {
            files = DownloadUtil.takeDownloadResult();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        files.forEach(file -> FileUtil.moveFile(file, ModMaster.getModFullFileByFileName(file.getName())));

    }
}
