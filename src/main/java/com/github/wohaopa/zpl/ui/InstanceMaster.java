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

package com.github.wohaopa.zpl.ui;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.filesystem.MyDirectory;
import com.github.wohaopa.zeropointlanuch.core.tasks.Scheduler;
import com.github.wohaopa.zeropointlanuch.core.tasks.Task;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.DiscoverInstanceTask;

public class InstanceMaster {

    private static ObservableList<Instance> instances;
    private static Instance cur;

    private static StringProperty name = new SimpleStringProperty();
    private static StringProperty version = new SimpleStringProperty();
    private static StringProperty depInstance = new SimpleStringProperty();
    private static StringProperty launcher = new SimpleStringProperty();
    private static StringProperty sharer = new SimpleStringProperty();
    private static BooleanProperty update = new SimpleBooleanProperty();
    private static ObjectProperty<TreeItem<String>> root = new SimpleObjectProperty<>();

    static {
        try {
            new DiscoverInstanceTask(null).call();
        } catch (Exception e) {
            Log.error("搜索错误：{}", e);
            throw new RuntimeException(e);
        }
        instances = FXCollections.observableArrayList(Instance.list());
        cur = instances.size() == 0 ? null : instances.get(0);
    }

    public static ObservableList<Instance> getInstances() {
        return instances;
    }

    public static void change(Instance instance) {
        cur = instance;
        name.setValue(cur.information.name);
        version.setValue(cur.information.version);
        depInstance.setValue(cur.information.depVersion);
        sharer.setValue(cur.information.sharer);
        launcher.setValue(cur.information.launcher);
        update.setValue(cur.information.update);

        root.setValue(fillFileTree(instance, false));
    }

    public static Instance getCur() {
        return cur;
    }

    public static StringProperty nameProperty() {
        return name;
    }

    public static StringProperty versionProperty() {
        return version;
    }

    public static StringProperty depInstanceProperty() {
        return depInstance;
    }

    public static StringProperty sharerProperty() {
        return sharer;
    }

    public static StringProperty launcherProperty() {
        return launcher;
    }

    public static BooleanProperty updateProperty() {
        return update;
    }

    public static ObjectProperty<TreeItem<String>> rootProperty() {
        return root;
    }

    public static void hasChange() {
        boolean flag = false;
        if (!cur.information.name.equals(name.getValue())) {
            String oV = cur.information.name;
            String nV = name.getValue();
            if (!Instance.containsKey(nV)) {
                Instance.rename(oV, nV);
                instances = FXCollections.observableArrayList(Instance.list());
                flag = true;
            }
        }

        if (!cur.information.version.equals(version.getValue())) {
            cur.information.version = version.getValue();
            flag = true;
        }
        if (!cur.information.depVersion.equals(depInstance.getValue())) {
            cur.information.depVersion = depInstance.getValue();
            flag = true;
        }

        if (!cur.information.sharer.equals(sharer.getValue())) {
            cur.information.sharer = sharer.getValue();
            flag = true;
        }
        if (!cur.information.launcher.equals(launcher.getValue())) {
            cur.information.launcher = launcher.getValue();
            flag = true;
        }
        if (!cur.information.update == update.getValue()) {
            cur.information.update = update.getValue();
            flag = true;
        }

        if (flag) cur.savaInformation();
    }

    private static Map<Instance, TreeItem<String>> cache = new HashMap<>();

    private static TreeItem<String> fillFileTree(Instance instance, boolean update) {

        if (!update && cache.containsKey(instance)) return cache.get(instance);
        var root = new TreeItem<>("根："+instance.information.name);

        Scheduler.submitTasks(new Task<Boolean>(null) {

            @Override
            public Boolean call() throws Exception {
                var myDirectory = instance.getMapper()
                    .getMyDirectory();
                fillFileTree0(myDirectory, root);
                return true;
            }
        });

        return root;
    }

    private static void fillFileTree0(MyDirectory myDirectory, TreeItem<String> treeItem) {

        var list = new LinkedList<TreeItem<String>>();

        for (var name : myDirectory.list()) {
            var obj = myDirectory.getSub(name);
            var item = new TreeItem<>(obj.toString());
            if (obj.isDirectory()) fillFileTree0((MyDirectory) obj, item);
            list.add(item);
        }
        list.sort(Comparator.comparing(TreeItem::getValue));
        treeItem.getChildren()
            .addAll(list);
    }
}
