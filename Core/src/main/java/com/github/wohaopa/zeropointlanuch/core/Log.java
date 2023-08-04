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

import java.util.Stack;

import cn.hutool.log.LogFactory;

public class Log {

    private static final cn.hutool.log.Log myLogger = LogFactory.get("Main");

    private static String marker = "默认：";
    private static final Stack<String> old_marker = new Stack<>();

    public static void start(String session) {
        Log.old_marker.push(Log.marker);
        Log.marker = session + "：";
    }

    public static void end() {
        Log.marker = Log.old_marker.pop();
    }

    public static void debug(String s) {
        myLogger.debug(marker + s);
    }

    public static void debug(String s, Object o1) {
        myLogger.debug(marker + s, o1);
    }

    public static void debug(String s, Object o1, Object o2) {
        myLogger.debug(marker + s, o1, o2);
    }

    public static void debug(String s, Object o1, Object o2, Object o3) {
        myLogger.debug(marker + s, o1, o2, o3);
    }

    public static void debug(String s, Object o1, Object o2, Object o3, Object o4) {
        myLogger.debug(marker + s, o1, o2, o3, o4);
    }

    public static void info(String s) {
        myLogger.info(marker + s);
    }

    public static void info(String s, Object o1) {
        myLogger.info(marker + s, o1);
    }

    public static void info(String s, Object o1, Object o2) {
        myLogger.info(marker + s, o1, o2);
    }

    public static void info(String s, Object o1, Object o2, Object o3) {
        myLogger.info(marker + s, o1, o2, o3);
    }

    public static void info(String s, Object o1, Object o2, Object o3, Object o4) {
        myLogger.info(marker + s, o1, o2, o3, o4);
    }

    public static void warn(String s) {
        myLogger.warn(marker + s);
    }

    public static void warn(String s, Object o1) {
        myLogger.warn(marker + s, o1);
    }

    public static void warn(String s, Object o1, Object o2) {
        myLogger.warn(marker + s, o1, o2);
    }

    public static void warn(String s, Object o1, Object o2, Object o3) {
        myLogger.warn(marker + s, o1, o2, o3);
    }

    public static void warn(String s, Object o1, Object o2, Object o3, Object o4) {
        myLogger.error(marker + s, o1, o2, o3, o4);
    }

    public static void error(String s) {
        myLogger.error(marker + s);
    }

    public static void error(String s, Object o1) {
        myLogger.error(marker + s, o1);
    }

    public static void error(String s, Object o1, Object o2) {
        myLogger.error(marker + s, o1, o2);
    }

    public static void error(String s, Object o1, Object o2, Object o3) {
        myLogger.error(marker + s, o1, o2, o3);
    }

    public static void error(String s, Object o1, Object o2, Object o3, Object o4) {
        myLogger.error(marker + s, o1, o2, o3, o4);
    }
}
