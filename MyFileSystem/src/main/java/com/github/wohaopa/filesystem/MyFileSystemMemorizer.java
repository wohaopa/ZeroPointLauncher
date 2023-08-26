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
import java.nio.charset.Charset;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.github.wohaopa.Main;

public class MyFileSystemMemorizer {

    public static void save(File file, MyFileSystemBase myFileSystemBase) {
        JSONObject jsonObject = new JSONObject(Main.JSON_CONFIG);;
        if (myFileSystemBase.isFile())
            jsonObject.putOpt(myFileSystemBase.name, ((MyFile) myFileSystemBase).getCrc32NotNull());
        else save0(jsonObject, (MyDirectory) myFileSystemBase);

        FileWriter fw = new FileWriter(file);
        fw.write(jsonObject.toJSONString(4));
    }

    private static void save0(JSONObject jsonObject, MyDirectory myDirectory) {

        myDirectory.valueList().forEach(myFileSystemBase1 -> {
            if (myFileSystemBase1.isFile())
                jsonObject.putOpt(myFileSystemBase1.name, ((MyFile) myFileSystemBase1).getCrc32NotNull());
            else {
                JSONObject jsonObject1 = new JSONObject(Main.JSON_CONFIG);;
                save0(jsonObject1, (MyDirectory) myFileSystemBase1);
                jsonObject.putOpt(myFileSystemBase1.name, jsonObject1);
            }
        });
    }

    public static MyDirectory load(File file, String name) {

        JSONObject jsonObject = (JSONObject) JSONUtil.readJSON(file, Charset.defaultCharset());
        MyDirectory myDirectory = new MyDirectory(name);
        load0(jsonObject, myDirectory);
        return myDirectory;
    }

    private static void load0(JSONObject jsonObject, MyDirectory myDirectory) {
        for (String name : jsonObject.keySet()) {
            if (name.charAt(name.length() - 1) == MyFileSystemBase.separator) {
                MyDirectory myDirectory1 = new MyDirectory(name, myDirectory);
                load0(jsonObject.getJSONObject(name), myDirectory1);
                myDirectory.add(myDirectory1);
            } else {
                MyFile myFile1 = new MyFile(name, myDirectory);
                myFile1.setCrc32(jsonObject.getLong(name));
                myDirectory.add(myFile1);
            }
        }
    }
}
