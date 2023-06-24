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
import java.util.HashMap;
import java.util.Map;

import com.github.wohaopa.zeropointlanuch.core.ZplDirectory;

public class DownloadProvider {

    private static final DownloadProvider defaultProvider = new DownloadProvider();

    private static DownloadProvider provider = defaultProvider;

    private final Map<File, String> map = new HashMap<>();
    private final String url;

    public DownloadProvider() {
        this.url = "https:\\downloads.wohaopa.cn";

    }

    private String getUrlForPath(String path) {
        return url + path;
    }

    private void addUrlToMap0(String url, File file) {
        map.put(file, url);
    }

    private String getUrlForFile0(File file) {

        if (map.containsKey(file)) return map.get(file);

        if (file.getName()
            .equals("1.7.10.json"))
            return "https://piston-meta.mojang.com/v1/packages/1863782e33ce7b584fc45b037325a1964e095d3e/1.7.10.json";

        if (file.toString()
            .startsWith(
                ZplDirectory.getWorkDirectory()
                    .toString()))
            return getUrlForPath(
                file.toString()
                    .substring(
                        ZplDirectory.getWorkDirectory()
                            .toString()
                            .length()));
        return null;

    }

    public static void setProvider(DownloadProvider provider) {
        DownloadProvider.provider = provider;
    }

    public static void addUrlToMap(String url, File file) {
        provider.addUrlToMap0(url, file);
    }

    public static String getUrlForFile(File file) {
        return provider.getUrlForFile0(file);

    }
}
