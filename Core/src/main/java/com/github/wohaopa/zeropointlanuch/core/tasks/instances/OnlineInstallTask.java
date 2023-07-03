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

package com.github.wohaopa.zeropointlanuch.core.tasks.instances;

import java.io.File;
import java.util.function.Consumer;

import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zeropointlanuch.core.download.DownloadProvider;
import com.github.wohaopa.zeropointlanuch.core.tasks.DownloadTask;

public class OnlineInstallTask extends ZplInstallTask {

    public OnlineInstallTask(File instanceDir, String name, Consumer<String> callback) {
        super(null, instanceDir, name, callback);

    }

    @Override
    public Instance call() throws Exception {
        zip = new File(instanceDir, name + ".zip");
        if (!zip.exists()) {
            new DownloadTask(DownloadProvider.getUrlForFile(zip), zip, callback).call(); // 过于复杂的逻辑，懒得分开写了
        }
        return super.call();

    }
}
