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

package test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.wohaopa.zeropointlanuch.core.filesystem.MyDirectory;
import com.github.wohaopa.zeropointlanuch.core.filesystem.MyFileBase;

public class MyFileSystemMargeTest {

    public static void main(String[] args) {
        marge();
    }

    public static void marge() {
        MyDirectory myDirectory = (MyDirectory) MyFileBase
            .getMyFileSystemByFile(new File("D:\\ZeroPointServer\\Launcher\\copyTest\\test"), MyFileBase.DEFAULT_FI);

        MyDirectory imageDirectory = (MyDirectory) MyFileBase
            .getMyFileSystemByFile(new File("D:\\ZeroPointServer\\Launcher\\copyTest\\image"), MyFileBase.DEFAULT_FI);

        List<String> exclude = new ArrayList<>();
        exclude.add("\\mods\\1.7.10");
        MyFileBase.MargeInfo margeInfo = new MyFileBase.MargeInfo(null, exclude);
        MyFileBase.marge(myDirectory, imageDirectory, margeInfo);
    }
}
