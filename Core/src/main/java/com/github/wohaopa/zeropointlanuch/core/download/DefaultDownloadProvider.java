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

public class DefaultDownloadProvider extends DownloadProvider {

    public DefaultDownloadProvider() {
        super("https://downloads.wohaopa.cn");
    }

    protected String getUrlForPath0(String path) {
        if (path.endsWith("/assets/indexes/1.7.10.json"))// 使用BMCL API
            return "https://bmclapi2.bangbang93.com/v1/packages/1863782e33ce7b584fc45b037325a1964e095d3e/1.7.10.json";
        if (path.startsWith("/assets/objects/"))
            return "https://bmclapi2.bangbang93.com/assets/" + path.substring("/assets/objects/".length());
        if (path.startsWith("/libraries/net/minecraft/client/1.7.10/client-1.7.10.jar"))
            return "https://bmclapi2.bangbang93.com/version/1.7.10/client.jar";
        if (path.startsWith("/libraries/") && !path.startsWith("/libraries/org/lwjgl/"))
            return "https://bmclapi2.bangbang93.com/maven/" + path.substring("/libraries/".length());

        return super.getUrlForPath0(path);
    }

}
