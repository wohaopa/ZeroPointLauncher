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
import com.github.wohaopa.zeropointlanuch.core.ModMaster;
import com.github.wohaopa.zeropointlanuch.core.tasks.DecompressTask;
import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;

public class StandardInstallTask extends ZplInstallTask {

    private String version;

    public StandardInstallTask(File zip, File instanceDir, String name, String version, Consumer<String> callback) {
        super(zip, instanceDir, name, callback);

        this.version = version;
    }

    @Override
    public Instance call() throws Exception {

        File image = FileUtil.initAndMkDir(instanceDir, "image");

        Log.debug("开始解压：{}", zip);
        long time1 = System.currentTimeMillis();
        new DecompressTask(zip, image, callback).call();
        long time2 = System.currentTimeMillis();
        Log.debug("解压完成！用时：{}s", (time2 - time1) / 1000);

        Instance.Builder builder = new Instance.Builder(name);
        builder.setVersionFile(new File(instanceDir, "version.json"))
            .setVersion(version)
            .setDepVersion("null")
            .setChecksum(new File(instanceDir, "image"))
            .setIncludeMods(ModMaster.coverModsList(new File(instanceDir, "image/mods")))
            .setExcludeMods(null)
            // .setMyImage(myDirectory)
            .saveConfig();

        return builder.build();
    }
}
