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

package com.github.wohaopa.zeropointlanuch.core.tasks;

import java.io.File;
import java.util.function.Consumer;

import org.apache.commons.compress.archivers.ArchiveEntry;

import cn.hutool.core.lang.Filter;

import com.github.wohaopa.zeropointlanuch.core.utils.ZipUtil;

public class DecompressTask extends Task<File> {

    private final File file;
    private final File targetDir;
    private final Filter<ArchiveEntry> filter;

    public DecompressTask(File file, File targetDir, Consumer<String> callback) {
        this(file, targetDir, null, callback);
    }

    public DecompressTask(File file, File targetDir, Filter<ArchiveEntry> filter, Consumer<String> callback) {
        super(callback);
        this.file = file;
        this.targetDir = targetDir;
        this.filter = filter;
    }

    @Override
    public File call() throws Exception {
        ZipUtil.decompress(file, targetDir, filter);

        return targetDir;
    }
}
