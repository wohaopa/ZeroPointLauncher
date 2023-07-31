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

import com.github.wohaopa.zeropointlanuch.core.Config;
import com.github.wohaopa.zeropointlanuch.core.ZplDirectory;

public class DownloadProvider {

    private static DownloadProvider provider;

    public static void setProvider(DownloadProvider provider) {
        DownloadProvider.provider = provider;
    }

    protected final Map<File, String> map = new HashMap<>();
    protected boolean useMap = false;

    protected final String url;

    public DownloadProvider(String baseUrl, boolean useMap) {
        this.url = baseUrl;
        this.useMap = useMap;
    }

    protected void addUrlToMap0(String url, File file) {
        map.put(file, url);
    }

    protected String getUrlForPath0(String path) {
        return url + path.replace("%", "%25");
    }

    private String getUrlForFile0(File file) {

        if (useMap && map.containsKey(file)) return map.get(file);

        if (file.toString().startsWith(ZplDirectory.getWorkDirectory().toString())) return getUrlForPath0(
            file.toString().substring(ZplDirectory.getWorkDirectory().toString().length()).replace("\\", "/"));
        return null;
    }

    private static DownloadProvider getProvider() {
        if (provider == null) {
            String mirrorUrl = Config.getConfig().getLibraries_url();
            if (mirrorUrl.endsWith("/")) mirrorUrl = mirrorUrl.substring(0, mirrorUrl.length() - 1);
            if (mirrorUrl.startsWith("http")) provider = new DownloadProvider(mirrorUrl, true);
            else provider = new DefaultDownloadProvider();
        }

        return provider;
    }

    public static void addUrlToMap(String url, File file) {
        getProvider().addUrlToMap0(url, file);
    }

    public static String getUrlForFile(File file) {
        return getProvider().getUrlForFile0(file);
    }
}
