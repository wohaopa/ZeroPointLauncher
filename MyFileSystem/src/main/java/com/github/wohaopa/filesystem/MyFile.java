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

package com.github.wohaopa.filesystem;

import cn.hutool.core.io.FileUtil;

public class MyFile extends MyFileSystemBase {

    private Long crc32;

    protected MyFile(String name, MyDirectory parent) {
        super(name, parent);
    }

    protected boolean checksum(MyFile myFile) throws StatException {

        int stat = (this.file == null ? 0 : 1) + (myFile.file == null ? 0 : 1 << 1)
            + (this.crc32 == null ? 0 : 1 << 2)
            + (myFile.crc32 == null ? 0 : 1 << 3);
        boolean result;
        switch (stat) {
            case 15:
            case 14:
            case 13:
            case 12:
                result = crc32.equals(myFile.crc32);
                break;
            case 11:
            case 7:
            case 3:
                if (this.file.length() != myFile.file.length()) {
                    result = false;
                    break;
                }
                if (this.crc32 == null) this.crc32 = FileUtil.checksumCRC32(this.file);
                if (myFile.crc32 == null) myFile.crc32 = FileUtil.checksumCRC32(myFile.file);
                result = crc32.equals(myFile.crc32);
                break;
            case 9:
                this.crc32 = FileUtil.checksumCRC32(this.file);
                result = crc32.equals(myFile.crc32);
                break;
            case 6:
                myFile.crc32 = FileUtil.checksumCRC32(myFile.file);
                result = crc32.equals(myFile.crc32);
                break;
            default:
                throw new StatException(this, myFile, stat);
        }
        return result;
    }

    protected void setCrc32(long crc32) {
        this.crc32 = crc32;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    public Long getCrc32() {
        return crc32;
    }

    public Long getCrc32NotNull() {
        if (crc32 == null) crc32 = file == null ? 0 : FileUtil.checksumCRC32(file);
        return crc32;
    }
}
