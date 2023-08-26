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
import java.util.HashSet;
import java.util.Set;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONObject;

import com.github.wohaopa.Main;

public class MyFileSystemDiffer {

    private final MyDirectory aDirectory;
    private final MyDirectory bDirectory;
    public final MyDirectory aOnlyDirectory;
    public final MyDirectory bOnlyDirectory;
    public final MyDirectory aDiffDirectory;
    public final MyDirectory bDiffDirectory;
    public final MyDirectory sameDirectory;

    public MyFileSystemDiffer(MyDirectory aDirectory, MyDirectory bDirectory) {
        this.aDirectory = aDirectory;
        this.bDirectory = bDirectory;
        this.aOnlyDirectory = new MyDirectory(aDirectory.name + "-Only");
        this.bOnlyDirectory = new MyDirectory(bDirectory.name + "-Only");
        this.aDiffDirectory = new MyDirectory(aDirectory.name + "-Diff");
        this.bDiffDirectory = new MyDirectory(bDirectory.name + "-Diff");
        this.sameDirectory = new MyDirectory(aDirectory.name + ", " + bDirectory.name + "-Same");
    }

    public void diff() {
        diff0(aDirectory, bDirectory);
    }

    private void diff0(MyDirectory aDirectory, MyDirectory bDirectory) {
        Set<String> aList = new HashSet<>(aDirectory.nameList());

        try {
            for (String name : bDirectory.nameList()) {
                if (aList.contains(name)) { // a,b 都有
                    aList.remove(name);
                    MyFileSystemBase aMyFileSystemBase = aDirectory.get(name);
                    MyFileSystemBase bMyFileSystemBase = bDirectory.get(name);
                    if (aMyFileSystemBase.isFile()) { // 为文件

                        if (((MyFile) aMyFileSystemBase).checksum((MyFile) bMyFileSystemBase)) { // 协同
                            make(sameDirectory, aMyFileSystemBase, "same");
                        } else { // 不同
                            make(aDiffDirectory, aMyFileSystemBase, "diff");

                            make(bDiffDirectory, bMyFileSystemBase, "diff");
                        }

                    } else { // 为目录
                        diff0((MyDirectory) aMyFileSystemBase, (MyDirectory) bMyFileSystemBase);
                    }
                } else { // a 中没有
                    make(bOnlyDirectory, bDirectory.get(name), "only");
                }
            }
        } catch (StatException e) {

        }

        for (String name : aList) { // b 中没有
            make(aOnlyDirectory, aDirectory.get(name), "only");
        }
    }

    /**
     * 将 myFileSystemBase 中的部分属性复制到 directory 中的对应元素
     *
     * @param directory        目标根目录
     * @param myFileSystemBase 被复制的对象
     * @param value            信息
     */
    private static void make(MyDirectory directory, MyFileSystemBase myFileSystemBase, String value) {
        MyFileSystemBase myFileSystemBase1 = MyFileSystemMaker.make(directory, myFileSystemBase.path);
        myFileSystemBase1.file = myFileSystemBase.file;
        myFileSystemBase1.setAttribute("diff-source", myFileSystemBase);
        myFileSystemBase1.setAttribute("diff-result", value);
    }

    public void save(File file) {
        save0(new File(file, aOnlyDirectory.name + ".json"), aOnlyDirectory);
        save0(new File(file, aDiffDirectory.name + ".json"), aDiffDirectory);
        save0(new File(file, bOnlyDirectory.name + ".json"), bOnlyDirectory);
        save0(new File(file, bDiffDirectory.name + ".json"), bDiffDirectory);
        save0(new File(file, sameDirectory.name + ".json"), sameDirectory);
    }

    private void save0(File file, MyDirectory myDirectory) {
        JSONObject jsonObject = new JSONObject(Main.JSON_CONFIG);
        save0(jsonObject, myDirectory);

        FileWriter fw = new FileWriter(file);
        fw.write(jsonObject.toStringPretty());;
    }

    private void save0(JSONObject jsonObject, MyDirectory myDirectory) {
        MyDirectory myDirectory1 = (MyDirectory) myDirectory.getAttribute("diff-source");
        if (myDirectory1 == null) myDirectory1 = myDirectory;

        myDirectory1.valueList().forEach(myFileSystemBase1 -> {
            if (myFileSystemBase1.isFile()) {
                MyFile myFile = (MyFile) myFileSystemBase1.getAttribute("diff-source");
                if (myFile == null) myFile = (MyFile) myFileSystemBase1;
                jsonObject.putOpt(myFileSystemBase1.name, myFile.getCrc32NotNull());

            } else {
                JSONObject jsonObject1 = new JSONObject(Main.JSON_CONFIG);;
                save0(jsonObject1, (MyDirectory) myFileSystemBase1);
                jsonObject.putOpt(myFileSystemBase1.name, jsonObject1);
            }
        });
    }
}
