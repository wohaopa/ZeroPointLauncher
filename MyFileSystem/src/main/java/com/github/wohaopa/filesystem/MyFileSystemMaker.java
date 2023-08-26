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

public class MyFileSystemMaker {

    public static MyFileSystemBase make(MyDirectory root, MyFileSystemPath path) {
        MyDirectory myDirectory1 = root;
        int index = 0;
        while (index < path.paths.size() - 1) {
            if (myDirectory1.contains(path.paths.get(index))) {
                myDirectory1 = (MyDirectory) myDirectory1.get(path.paths.get(index));
            } else {
                MyDirectory myDirectory2 = new MyDirectory(path.paths.get(index), myDirectory1);
                myDirectory1.add(myDirectory2);
                myDirectory1 = myDirectory2;
            }
            index++;
        }

        String name = path.paths.get(index);
        MyFileSystemBase myFileSystemBase;
        if (myDirectory1.contains(name)) {
            myFileSystemBase = myDirectory1.get(name);
        } else {
            myFileSystemBase = name.charAt(name.length() - 1) == MyFileSystemBase.separator
                ? new MyDirectory(name, myDirectory1, 1)
                : new MyFile(name, myDirectory1);

            myDirectory1.add(myFileSystemBase);
        }
        return myFileSystemBase;
    }

    public static boolean contains(MyDirectory root, MyFileSystemPath path) {
        MyDirectory myDirectory1 = root;
        int index = 0;
        while (index < path.paths.size()) {
            if (myDirectory1.contains(path.paths.get(index))) {
                myDirectory1 = (MyDirectory) myDirectory1.get(path.paths.get(index));
                index++;
            } else {
                return false;
            }
        }
        return true;
    }
}
