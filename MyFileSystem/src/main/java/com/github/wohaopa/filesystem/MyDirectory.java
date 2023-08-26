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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MyDirectory extends MyFileSystemBase {

    private final Map<String, MyFileSystemBase> list;
    protected int quantity;

    protected MyDirectory(String name) {
        super(name, null);
        list = new HashMap<>(5);
    }

    protected MyDirectory(String name, MyDirectory parent) {
        this(name, parent, 0);
    }

    protected MyDirectory(String name, MyDirectory parent, int quantity) {
        super((name.charAt(name.length() - 1) == MyFileSystemBase.separator) ? name : name + separator, parent);
        this.quantity = quantity;
        list = new HashMap<>(quantity);
    }

    protected void add(MyFileSystemBase myFileSystemBase) {
        list.put(myFileSystemBase.name, myFileSystemBase);
    }

    protected MyFileSystemBase get(String name) {
        return list.get(name);
    }

    protected Set<String> nameList() {
        return list.keySet();
    }

    protected Collection<MyFileSystemBase> valueList() {
        return list.values();
    }

    protected boolean contains(String name) {
        return list.containsKey(name);
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }
}
