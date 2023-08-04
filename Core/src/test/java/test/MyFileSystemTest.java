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

package test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import com.github.wohaopa.zeropointlanuch.core.filesystem.MyDirectory;
import com.github.wohaopa.zeropointlanuch.core.filesystem.MyFileBase;

public class MyFileSystemTest {

    public static void main(String[] args) {
        // geneDiff();
        update();
    }

    static Map<String, File> map = new HashMap<>();

    public static void update() {
        File file = new File(
            "D:\\DevProject\\JavaProject\\ZeroPointLaunch\\TestResources\\.GTNH\\instances\\GTNH-2.3.0-zpl\\image");
        File json = new File(
            "D:\\DevProject\\JavaProject\\ZeroPointLaunch\\TestResources\\.GTNH\\instances\\GTNH-2.3.0-zpl\\checksum.json");

        // MyDirectory myFileBase = (MyDirectory) MyFileBase.getMyFileSystemByFile(file, null);
        // myFileBase.saveChecksumAsJson(json);
        MyDirectory myDirectory = (MyDirectory) MyFileBase.getMyFileSystemByJson("", json, file);
        MyFileBase.update(myDirectory, file, json);
    }

    public static void geneDiff() {

        File root = new File(System.getProperty("user.dir"));
        Scanner scanner = new Scanner(System.in);

        for (File file : Objects.requireNonNull(new File(root + File.separator + "versions").listFiles())) {
            map.put(file.getName().replace(".json", ""), file);
        }
        while (true) {
            System.out
                .println("使用指令：diff <版本> <目标.minecraft> - 来进行差异对比，diff指令的两个参数可以为版本字符串，也可以为.minecraft的绝对路径（不可包含中文）");
            System.out.println("quit退出");
            String command = scanner.nextLine();
            String[] args = command.split(" ");
            if (args[0].equals("quit")) break;
            if (!args[0].equals("diff")) {
                System.out.println("未知指令：" + command);
                continue;
            }
            if (args.length < 3) {
                System.out.println("指令参数缺失：" + command);
                continue;
            }
            MyFileBase my1, my2;

            if (map.containsKey(args[1])) {
                File file1 = map.get(args[1]);
                my1 = MyFileBase.getMyFileSystemByJson(args[1], file1, null);
            } else {
                File file1 = new File(args[1]);
                if (!file1.exists()) {
                    System.out.println("目录错误：" + file1);
                    continue;
                }
                my1 = MyFileBase.getMyFileSystemByFile(file1, MyFileBase.DEFAULT_FI);
            }

            if (map.containsKey(args[2])) {
                File file2 = map.get(args[2]);
                my2 = MyFileBase.getMyFileSystemByJson(args[2], file2, null);
            } else {
                File file2 = new File(args[2]);
                if (!file2.exists()) {
                    System.out.println("目录错误：" + file2);
                    continue;
                }
                my2 = MyFileBase.getMyFileSystemByFile(file2, MyFileBase.DEFAULT_FI);
            }
            if (my1 != null && my2 != null) {
                MyFileBase.diff(my1, my2);

                my1.saveDiffAsJson(
                    new File(root, "no_equal.json"),
                    MyFileBase.Sate.no_equal,
                    MyFileBase.Sate.file_directory,
                    MyFileBase.Sate.directory_file);
                my1.saveDiffAsJson(new File(root, "only_first.json"), MyFileBase.Sate.only_me);
                my2.saveDiffAsJson(new File(root, "only_second.json"), MyFileBase.Sate.only_me);
            }
        }
    }
}
