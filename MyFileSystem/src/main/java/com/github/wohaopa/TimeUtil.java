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

package com.github.wohaopa;

import java.util.HashMap;
import java.util.Map;

public class TimeUtil {

    public static Map<String, TimeUnit> time = new HashMap<>();

    public static void start(String label) {
        long time1 = System.currentTimeMillis();
        try {
            time.get(label).time1 = time1;
        } catch (NullPointerException nullPointerException) {
            TimeUnit timeUnit = new TimeUnit();
            timeUnit.time1 = time1;
            time.put(label, timeUnit);
        }
    }

    public static void end(String label) {
        long time2 = System.currentTimeMillis();
        try {
            TimeUnit timeUnit = time.get(label);
            timeUnit.time += time2 - timeUnit.time1;
        } catch (NullPointerException nullPointerException) {}
    }

    public static class TimeUnit {

        private long time;
        private long time1;

        @Override
        public String toString() {
            return time * 0.001 + "s";
        }
    }
}
