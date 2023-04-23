/*
 * The MIT License (MIT)
 * Copyright © 2023 初夏同学
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.wohaopa.zeropointwrapper;

import java.util.*;

public class Differ {

    public static List<Map<String, Long>> diff(Map<String, Long> checksum1, Map<String, Long> checksum2) {
        List<Map<String, Long>> list = new ArrayList<>();

        Map<String, Long> inst1have = new HashMap<>();
        Map<String, Long> inst2have = new HashMap<>();
        list.add(inst1have);
        list.add(inst2have);

        for (Map.Entry<String, Long> entry : checksum1.entrySet()) {
            if (checksum2.containsKey(entry.getKey())
                && Objects.equals(checksum2.get(entry.getKey()), entry.getValue())) {
                continue;
            }
            inst1have.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Long> entry : checksum2.entrySet()) {
            if (checksum1.containsKey(entry.getKey())
                && Objects.equals(checksum1.get(entry.getKey()), entry.getValue())) {
                continue;
            }
            inst2have.put(entry.getKey(), entry.getValue());
        }

        return list;
    }
}
