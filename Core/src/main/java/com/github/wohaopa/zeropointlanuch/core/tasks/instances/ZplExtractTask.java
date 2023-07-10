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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.tasks.CompressTask;
import com.github.wohaopa.zeropointlanuch.core.tasks.Task;

public class ZplExtractTask extends Task<File> {

    private static final List<String> include = new ArrayList<>();
    static {
        include.add("image");
        include.add("checksum.json");
        include.add("version.json");
    }

    private final File instanceDir;
    private final File zip;

    public ZplExtractTask(File zip, File instanceDir, Consumer<String> callback) {
        super(callback);
        this.instanceDir = instanceDir;
        this.zip = zip;
    }

    @Override
    public File call() throws Exception {

        accept("正在导出");
        Log.debug("开始压缩：{}", zip);
        long time1 = System.currentTimeMillis();
        new CompressTask(zip, instanceDir, file -> !include.contains(file.getName()), callback).call();
        long time2 = System.currentTimeMillis();
        Log.debug("压缩完成！用时：{}s", (time2 - time1) / 1000);
        accept("导出完成");
        return zip;
    }
}
