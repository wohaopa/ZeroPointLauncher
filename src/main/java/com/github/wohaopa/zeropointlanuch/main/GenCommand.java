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
import java.util.*;

import com.github.wohaopa.zeropointlanuch.core.Differ;
import com.github.wohaopa.zeropointlanuch.core.Identification;
import com.github.wohaopa.zeropointlanuch.core.Instance;

public class GenCommand {

    public static ICommand genDiff = new ICommand() {

        @Override
        public boolean execute(String[] args) throws IOException {
            if (args.length < 3) {
                System.out.println(usage());
                return false;
            }

            String name1 = args[1];
            Instance instance1 = Instance.get(name1);
            if (instance1 == null) {
                System.out.println("错误：未找到实例" + name1);
                return false;
            }

            String name2 = args[2];
            Instance instance2 = Instance.get(name2);
            if (instance2 == null) {
                System.out.println("错误：未找到实例" + name2);
                return false;
            }

            Map<String, Long> common = new HashMap<>();
            List<String> removed = new ArrayList<>();

            Map<String, Long> checksum1 = new HashMap<>(instance1.information.checksum);
            Map<String, Long> checksum2 = new HashMap<>(instance2.information.checksum);

            checksum1.forEach((s, aLong) -> {
                Long bLong = checksum2.get(s);
                if (bLong != null && Objects.equals(aLong, bLong)) removed.add(s);
            });

            removed.forEach(s -> {
                common.put(s, checksum1.remove(s));
                checksum2.remove(s);
            });

            Identification identification1 = new Identification(checksum1);
            Identification identification2 = new Identification(checksum2);
            Identification identification3 = new Identification(common);

            return true;
        }

        @Override
        public String usage() {
            return "genDiff <实例名1> <实例名2> - 用于生成实例名1到实例名2的升级脚本";
        }
    };
    public static ICommand diffDir = new ICommand() {

        @Override
        public boolean execute(String[] args) throws IOException {
            Instance inst = Instance.get("2.3.2");
            File file = new File("D:\\Minecraft\\ZPS3\\client\\.minecraft");

            Identification identification = new Identification(inst.information.checksum);
            Differ differ = Differ.diff(file, identification);

            return true;
        }

        @Override
        public String usage() {
            return null;
        }
    };
}
