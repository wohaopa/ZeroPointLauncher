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

public class DownloadProvider {

    private static final DownloadProvider defaultProvider = new DownloadProvider();

    private static DownloadProvider provider;
    String assetsBase;

    String assetsIndexJsonUrl;
    String librariesIndexJsonUrl;

    public DownloadProvider() {
        this.assetsBase = "https://resources.download.minecraft.net/";
        this.assetsIndexJsonUrl = "https://launchermeta.mojang.com/v1/packages/1863782e33ce7b584fc45b037325a1964e095d3e/1.7.10.json";
        this.librariesIndexJsonUrl = "https://libraries.minecraft.net/";
    }

    public static DownloadProvider getProvider() {
        return defaultProvider;
    }

    public String getLibrariesBaseUrl() {
        return librariesIndexJsonUrl;
    }

    public String getAssetsIndexJsonUrl() {
        return assetsIndexJsonUrl;
    }

    public String getAssetsObjUrl(String hash) {
        return assetsBase + hash.substring(0, 2) + "/" + hash;
    }

    public String getModsUrlBase() {
        return "http\\://127.0.0.1/ZeroPointLaunch/mods/";
    }
}
