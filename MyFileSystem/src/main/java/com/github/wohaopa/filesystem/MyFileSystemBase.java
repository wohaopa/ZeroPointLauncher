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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class MyFileSystemBase {

    public static final char separator = '/';

    protected String name;
    protected File file;
    protected MyFileSystemPath path;
    protected Object from;

    private final MyDirectory parent;
    private Map<String, Object> attributes;

    protected MyFileSystemBase(String name, MyDirectory parent) {
        this.name = name;
        this.parent = parent;
        if (parent == null) path = new MyFileSystemPath();
        else {
            path = new MyFileSystemPath(parent.path, name);
        }
    }

    public void setAttribute(String attribute, Object value) {
        if (attributes == null) attributes = new HashMap<>();
        attributes.put(attribute, value);
    }

    public Object getAttribute(String attribute) {
        if (attributes == null) return null;
        return attributes.get(attribute);
    }

    public Object getAttribute(String attribute, Object defaultValue) {
        if (attributes == null) attributes = new HashMap<>();
        if (attributes.containsKey(attribute)) return attributes.get(attribute);
        else {
            attributes.put(attribute, defaultValue);
            return defaultValue;
        }
    }

    public abstract boolean isFile();

    public abstract boolean isDirectory();

    @Override
    public String toString() {
        return name + " (" + (from != null ? from : "unknown") + ": " + path + ")";
    }
}
