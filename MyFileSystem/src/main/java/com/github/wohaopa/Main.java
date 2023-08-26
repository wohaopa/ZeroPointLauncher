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

import java.io.File;
import java.util.*;

import cn.hutool.json.JSONConfig;

import com.github.wohaopa.filesystem.*;
import com.github.wohaopa.filesystem.filter.PathFilter;

public class Main {

    public static final JSONConfig JSON_CONFIG = JSONConfig.create().setKeyComparator((o1, o2) -> {
        boolean flag1 = o1.charAt(o1.length() - 1) == MyFileSystemBase.separator;
        boolean flag2 = o2.charAt(o2.length() - 1) == MyFileSystemBase.separator;
        if (flag1 == flag2) return o1.compareTo(o2);
        else return flag1 ? -1 : 1;
    });

    public static void main(String[] args) {
        System.out.println("欢迎使用ZPL-差异对比工具");

        System.out.println("输入指令以使用");
        Scanner scanner = new Scanner(System.in);

        System.out.println("list - 列出加载的文件树以及差异");
        System.out.println("new <目录> <文件树名称> - 创建新的文件树");
        System.out.println("diff <文件树名称> <文件树名称> - 对比文件树差异");
        System.out.println("save <文件树名称> - 保存文件树json信息");
        System.out.println("output-diff <目录> <差异名称> - 将差异文件输出至指定目录");

        Map<String, MyDirectory> myDirectoryList = new HashMap<>();
        File jsonDir = new File("json");
        if (jsonDir.isDirectory() && jsonDir.listFiles() != null) {
            for (File json : Objects.requireNonNull(jsonDir.listFiles())) {
                try {
                    String name = json.getName().substring(0, json.getName().length() - 5);
                    myDirectoryList.put(name, MyFileSystemMemorizer.load(json, name));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Map<String, MyFileSystemDiffer> myFileSystemDifferList = new HashMap<>();

        while (true) {
            String cmdLine = scanner.nextLine();
            String[] cmd = cmdLine.split(" ");

            switch (cmd[0]) {
                case "list": {
                    System.out.println("共计：" + myDirectoryList.size() + "文件树");
                    for (String name : myDirectoryList.keySet()) System.out.println(name);
                    System.out.println("共计：" + myFileSystemDifferList.size() + "差异");
                    for (String name : myFileSystemDifferList.keySet()) System.out.println(name);
                    break;
                }
                case "new": {
                    if (cmd.length != 3) {
                        System.out.println("指令错误：缺失参数或目录中含有空格！");
                        System.out.println(cmdLine);
                        System.out.println("正确用法：new <目录> <文件树名称> - 创建新的文件树");
                        break;
                    }
                    File file = new File(cmd[1]);
                    String name = cmd[2];
                    MyFileSystemBuilder builder = new MyFileSystemBuilder(name, name, file, new PathFilter(file));
                    System.out.println("文件树生成完成");
                    myDirectoryList.put(name, (MyDirectory) builder.build());
                    System.out.println("文件树生成完成");
                    break;
                }
                case "save": {
                    if (cmd.length != 2) {
                        System.out.println("指令错误：缺失参数或目录中含有空格！");
                        System.out.println(cmdLine);
                        System.out.println("正确用法：save <文件树名称> - 保存文件树json信息");
                        break;
                    }
                    MyDirectory myDirectory = myDirectoryList.get(cmd[1]);
                    if (myDirectory == null) {
                        System.out.println("未找到文件树：" + cmd[1]);
                        break;
                    }
                    System.out.println("文件树保存完成");
                    MyFileSystemMemorizer.save(new File("json/" + cmd[1] + ".json"), myDirectory);
                    System.out.println("文件树保存完成");
                    break;
                }
                case "diff": {
                    if (cmd.length != 3) {
                        System.out.println("指令错误：缺失参数或目录中含有空格！");
                        System.out.println(cmdLine);
                        System.out.println("正确用法：diff <文件树名称> <文件树名称> - 对比文件树差异");
                        break;
                    }
                    MyDirectory myDirectory1 = myDirectoryList.get(cmd[1]);
                    MyDirectory myDirectory2 = myDirectoryList.get(cmd[2]);
                    if (myDirectory1 == null) {
                        System.out.println("未找到文件树：" + cmd[1]);
                        break;
                    }
                    if (myDirectory2 == null) {
                        System.out.println("未找到文件树：" + cmd[2]);
                        break;
                    }
                    System.out.println("差异对比开始");
                    MyFileSystemDiffer myFileSystemDiffer = new MyFileSystemDiffer(myDirectory1, myDirectory2);
                    myFileSystemDifferList.put(cmd[1] + "-" + cmd[2], myFileSystemDiffer);
                    myFileSystemDiffer.diff();
                    System.out.println("差异对比完成");
                    break;
                }
                case "output-diff": {
                    if (cmd.length != 3) {
                        System.out.println("指令错误：缺失参数或目录中含有空格！");
                        System.out.println(cmdLine);
                        System.out.println("正确用法：output-diff <目录> <差异名称> - 将差异文件输出至指定目录");
                        break;
                    }
                    File outputFile = new File(cmd[1]);
                    MyFileSystemDiffer myFileSystemDiffer1 = myFileSystemDifferList.get(cmd[2]);
                    if (myFileSystemDiffer1 == null) {
                        System.out.println("未找到差异：" + cmd[2]);
                        break;
                    }
                    System.out.println("差异保存完成");
                    MyFileSystemCopier myFileSystemCopier1 = new MyFileSystemCopier(
                        new File(outputFile, "same"),
                        myFileSystemDiffer1.sameDirectory);
                    MyFileSystemCopier myFileSystemCopier2 = new MyFileSystemCopier(
                        new File(outputFile, "new"),
                        myFileSystemDiffer1.aDiffDirectory);
                    MyFileSystemCopier myFileSystemCopier3 = new MyFileSystemCopier(
                        new File(outputFile, "new"),
                        myFileSystemDiffer1.aOnlyDirectory);
                    MyFileSystemCopier myFileSystemCopier4 = new MyFileSystemCopier(
                        new File(outputFile, "old"),
                        myFileSystemDiffer1.bDiffDirectory);
                    MyFileSystemCopier myFileSystemCopier5 = new MyFileSystemCopier(
                        new File(outputFile, "old"),
                        myFileSystemDiffer1.bOnlyDirectory);
                    myFileSystemCopier1.copy();
                    myFileSystemCopier2.copy();
                    myFileSystemCopier3.copy();
                    myFileSystemCopier4.copy();
                    myFileSystemCopier5.copy();

                    myFileSystemDiffer1.save(new File("output"));

                    System.out.println("差异保存完成");
                    break;
                }
                default: {
                    System.out.println("list - 列出加载的文件树以及差异");
                    System.out.println("new <目录> <文件树名称> - 创建新的文件树");
                    System.out.println("diff <文件树名称> <文件树名称> - 对比文件树差异");
                    System.out.println("save <文件树名称> - 保存文件树json信息");
                    System.out.println("output-diff <目录> <差异名称> - 将差异文件输出至指定目录");
                }
            }
        }
    }
}
