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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyFileSystemPath {

    protected final List<String> paths;

    public MyFileSystemPath(MyFileSystemPath p, String name) {
        List<String> tmpList = new ArrayList<>(p.paths);
        tmpList.add(name);
        this.paths = Collections.unmodifiableList(tmpList);
    }

    public MyFileSystemPath(String p) {
        List<String> tmpList = new ArrayList<>();
        int tmp = 0;
        for (int i = 1; i < p.length(); i++) {
            if (p.charAt(i) == MyFileSystemBase.separator) {
                if (tmp < i) {
                    tmpList.add(p.substring(tmp + 1, i + 1));
                }
                tmp = i;
            }
        }
        if (tmp != p.length() - 1) {
            tmpList.add(p.substring(tmp + 1));
        }
        this.paths = Collections.unmodifiableList(tmpList);
    }

    public MyFileSystemPath(String... p) {
        this.paths = Collections.unmodifiableList(Arrays.asList(p));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MyFileSystemBase.separator);
        paths.forEach(sb::append);
        return sb.toString();
    }
}
