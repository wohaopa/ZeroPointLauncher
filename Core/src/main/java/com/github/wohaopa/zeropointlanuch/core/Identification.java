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

package com.github.wohaopa.zeropointlanuch.core;

import java.util.*;

public class Identification {

    private final Map<String, Long> checksum;
    public IdentificationDirectoryItem item;

    public Identification(Map<String, Long> checksum) {

        this.checksum = checksum;
        this.item = new IdentificationDirectoryItem("", "");
        this.item.fullName = "";
        format();
    }

    public boolean contains(String name) {
        return item.contains(name);
    }

    private void format() {
        this.checksum.forEach((key, value) -> {
            String[] tmp = key.split("\\\\");
            this.item.add(tmp, 0, value);
        });
    }

    public abstract static class IdentificationItem {

        public String name;
        public String fullName;
        public boolean isDirectory = false;

        protected IdentificationItem(String name) {
            this.name = name;
        }
    }

    public static class IdentificationFileItem extends IdentificationItem {

        public long checksum;

        public IdentificationFileItem(String name, String parent, long value) {
            super(name);
            this.fullName = parent + name;
            checksum = value;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class IdentificationDirectoryItem extends IdentificationItem {

        public Map<String, IdentificationItem> subItem;

        public IdentificationDirectoryItem(String name, String parent) {
            super(name);
            this.fullName = parent + name + "\\";
            isDirectory = true;
            subItem = new HashMap<>();
        }

        public void add(String[] tmp, int index, long value) {
            String name = tmp[index];
            if (tmp.length - 1 == index) subItem.put(name, new IdentificationFileItem(name, fullName, value));
            else {
                IdentificationDirectoryItem identificationDirectoryItem = (IdentificationDirectoryItem) subItem
                    .get(name);

                if (identificationDirectoryItem == null) {
                    identificationDirectoryItem = new IdentificationDirectoryItem(tmp[index], fullName);
                    subItem.put(name, identificationDirectoryItem);
                }

                identificationDirectoryItem.add(tmp, index + 1, value);
            }
        }

        public boolean contains(String name) {
            return subItem.containsKey(name);
        }

        public IdentificationItem getFile(String name) {
            return subItem.get(name);
        }

        @Override
        public String toString() {
            return name + "\\";
        }
    }
}
