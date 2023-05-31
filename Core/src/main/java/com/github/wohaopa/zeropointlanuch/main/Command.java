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

package com.github.wohaopa.zeropointlanuch.main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.github.wohaopa.zeropointlanuch.api.Core;
import com.github.wohaopa.zeropointlanuch.core.DirTools;
import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zeropointlanuch.core.Sharer;

@SuppressWarnings("unused") // 内部所有字段都会被反射注册
public class Command {

    public static ICommand help = new ICommand() {

        @Override
        public boolean execute(String[] args) {
            System.out.println(
                "在此输入指令，参数<>必选、[]可选，以回车结束。也可以在命令行直接输入指令（如：java -jar 本启动器全名 -login wohaopa -launch 2.3.2），命令行指令将不会做验证！\n"
                    + "在对目录："
                    + Main.workDir.toString()
                    + " 做出任何修改时请手动使用genRunDir使其他启动器正确识别（本启动器可以自动刷新）。\n");
            for (ICommand cmd : Main.commands.values()) {
                System.out.printf(cmd.usage() + "\n");
            }
            System.out.println(
                "quit - 退出\n\n" + "若要修改mod版本，请在对应实例文件的version.json中直接修改版本号，若Github中存在该版本，本启动器将自动下载。\n"
                    + "本启动器所有实例间共享mod等程序资源，config实例本独立存放，在share目录下的文件将被所有实例使用，其将会覆盖实例原有的重复内容\n"
                    + "本启动器处于开发阶段，微软登录等手段并非核心功能，请使用其他启动器完成。");
            return true;
        }

        @Override
        public String usage() {
            return "help - 获得指令帮助";
        }
    };

    public static ICommand lookup = new ICommand() {

        @Override
        public boolean execute(String[] args) {
            Core.refresh();
            return true;
        }

        @Override
        public String usage() {
            return "lookup - 刷新实例文件夹并安装zip文件夹压缩包";
        }
    };

    public static final ICommand downloadFile = new ICommand() {

        @Override
        public boolean execute(String[] args) throws IOException {
            if (args.length < 2) {
                System.out.println("用法：" + usage());
                return false;
            }
            Core.downloadFile(args[1]);
            return true;
        }

        @Override
        public String usage() {
            return "downloadFile <URL> - 下载文件";
        }
    };

    public static ICommand list = new ICommand() {

        @Override
        public boolean execute(String[] args) {
            List<Instance> list = (List<Instance>) Core.listInst();
            System.out.println("共有：" + list.size() + "个GTNH实例被识别");
            for (Instance instance : list)
                System.out.println("实例名：" + instance.information.name + " 版本：" + instance.information.version);
            System.out.println();
            return true;
        }

        @Override
        public String usage() {
            return "list - 获得实例列表";
        }
    };

    public static final ICommand installStandard = new ICommand() {

        @Override
        public boolean execute(String[] args) throws IOException {
            if (args.length < 4) {
                System.out.println("用法" + usage());
                return false;
            }
            String name = args[1];
            if (Instance.containsKey(name)) {
                System.out.println("错误：实例名重复:" + name + "\n");
                return false;
            }
            File instDir = new File(DirTools.instancesDir, name);
            if (instDir.exists()) {
                System.out.println("错误：目录重复:" + instDir.getPath() + "\n");
                return false;
            }
            String zip = args[2].replace("\"", "");
            String ver = args[3];
            File zipFile = new File(zip);
            if (!zipFile.exists()) {
                System.out.println("错误：压缩包不存在:" + zipFile.getPath() + "\n");
                return false;
            }
            // 校验完成
            Core.installStandard(zipFile, instDir, name, ver); // 执行
            return true; // 执行成功
        }

        @Override
        public String usage() {
            return "installStandard <实例名> <安装包文件绝对路径> <版本> - 安装一个GTNH实例";
        }
    };

    public static ICommand translation = new ICommand() {

        @Override
        public boolean execute(String[] args) throws IOException {
            if (args.length < 3) {
                System.out.println("用法：" + usage());
                return false;
            }
            String depInstName = args[1];
            Instance depInst = Instance.get(depInstName);
            if (depInst == null) {
                System.out.println("错误：已安装的实例中不包含：\"" + depInstName + "\" 可以使用list指令查看已安装实例");
                return false;
            }
            String name = depInst + "-translation";
            if (Instance.containsKey(name)) {
                System.out.println("错误：已存在:" + name + "\n");
                return false;
            }
            File instDir = new File(DirTools.instancesDir, name);
            if (instDir.exists()) {
                System.out.println("错误：目录重复:" + instDir.getPath() + "\n");
                return false;
            }
            String zip = args[2].replace("\"", "");
            File zipFile = new File(zip);
            if (!zipFile.exists()) {
                System.out.println("错误：压缩包不存在:" + zipFile.getPath() + "\n");
                return false;
            }
            Core.installTranslation(zipFile, instDir, name, depInst);
            return true;
        }

        @Override
        public String usage() {
            return "translation <实例名> <汉化包路径> - 汉化实例";
        }
    };

    public static ICommand genRunDir = new ICommand() {

        @Override
        public boolean execute(String[] args) {
            if (args.length < 2) {
                System.out.println("用法" + usage());
                return false;
            }
            Instance inst = Instance.get(args[1]);
            if (inst == null) {
                System.out.println("错误：已安装的实例中不包含：\"" + args[1] + "\" 可以使用list指令查看已安装实例");
                return false;
            }
            Sharer sharer = null;
            if (args.length == 3) {
                sharer = Sharer.get(args[2]);
                if (sharer == null) {
                    System.out.println("错误：共享器不存在：" + args[2] + " 可以使用默认共享器：Java8、Java17、Common");
                    return false;
                }
            }
            System.out.println("dir=" + Core.genRuntimeDir(inst, sharer));
            return true;
        }

        @Override
        public String usage() {
            return "genRunDir <实例名> [共享器] - 生成指定版本的运行目录，在使用其他启动器时使用此命令";
        }
    };

    public static ICommand genTransferOld = new ICommand() {

        @Override
        public boolean execute(String[] args) throws IOException {
            if (args.length < 4) {
                System.out.println("用法：" + usage());
                return false;
            }

            Instance instance = Instance.get(args[1]);
            if (instance == null) {
                System.out.println("错误：已安装的实例中不包含：\"" + args[1] + "\" 可以使用list指令查看已安装实例");
                return false;
            }

            File outDir = new File(args[2].replace("\"", ""));
            if (outDir.exists()) {
                System.out.println("警告：" + outDir + "存在，将自动移动");
            }

            File minecraftDir = new File(args[3].replace("\"", ""));
            if (!minecraftDir.exists() || minecraftDir.isFile()) {
                System.out.println("错误：" + minecraftDir + "不存在/不是目录。");
                return false;
            }

            Core.genTransferOld(outDir, instance, minecraftDir);
            return true;
        }

        @Override
        public String usage() {
            return "genTransferOld <实例名> <输出文件夹路径> <.minecraft路径> - 将原来的GTNH迁移至ZPL对应版本的实例";
        }
    };

    public static ICommand genZPLInstance = new ICommand() {

        @Override
        public boolean execute(String[] args) throws IOException {
            if (args.length < 4) {
                System.out.println("用法：" + usage());
                return false;
            }
            String instName = args[1];

            File outDir = new File(args[2]);
            if (outDir.isFile()) outDir.delete();
            outDir.mkdirs();

            File srcDir = new File(args[3]);
            if (!srcDir.exists() || srcDir.isFile()) {
                System.out.println("错误：" + srcDir + "不存在/不是目录。");
                return false;
            }
            Core.genZPLInstance(outDir, instName, srcDir);

            return true;
        }

        @Override
        public String usage() {
            return "genZPLInstance <实例名> <输出文件夹路径> <目标文件夹路径> - 生成ZPL标准实例";
        }
    };
}
