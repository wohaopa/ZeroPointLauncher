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
import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.tasks.DecompressTask;
import com.github.wohaopa.zeropointlanuch.core.tasks.Task;

public class ZplInstallTask extends Task<Instance> {

    protected File zip;
    protected File instanceDir;
    protected String name;

    public ZplInstallTask(File zip, File instanceDir, String name, Consumer<String> callback) {
        super(callback);
        this.zip = zip;
        this.instanceDir = instanceDir;
        this.name = name;
    }

    @Override
    public Instance call() throws Exception {

        Log.debug("开始解压：{}", zip);
        long time1 = System.currentTimeMillis();
        new DecompressTask(zip, instanceDir, callback).call();
        long time2 = System.currentTimeMillis();
        Log.debug("解压完成！用时：{}s", (time2 - time1) / 1000);

        Instance.Builder builder = new Instance.Builder(new File(instanceDir, "version.json"));
        builder.setName(name);
        return builder.build();
    }
}
