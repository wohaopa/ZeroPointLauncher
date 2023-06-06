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

import java.util.ArrayList;
import java.util.List;

public class ZPLDownloadProvider extends DownloadProvider {

    List<String> librariesUrlList = new ArrayList<>();

    public ZPLDownloadProvider() {
        String baseUrl = "http://127.0.0.1/";
        this.assetsBase = baseUrl;
        this.assetsIndexJson = baseUrl + "1.7.10.json";
        this.librariesBase = baseUrl;
        this.modsBase = baseUrl;

        librariesUrlList.add("https://maven.minecraftforge.net/");
        librariesUrlList.add("http://jenkins.usrv.eu:8081/nexus/content/groups/public/");
        librariesUrlList.add("https://build.lwjgl.org/");
        librariesUrlList.add("https://libraries.minecraft.net/");
        librariesUrlList.add("https://files.prismlauncher.org/maven/");
        librariesUrlList.add("https://launcher.mojang.com/");
    }

    @Override
    public String getLibrariesUrl(String url) {
        for (String s : librariesUrlList) if (url.startsWith(s)) return url.replace(s, librariesBase);

        return url;
    }

}
