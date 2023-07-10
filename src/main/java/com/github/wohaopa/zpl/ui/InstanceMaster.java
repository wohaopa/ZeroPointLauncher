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

import java.util.*;
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

    private static Instance cur;

    private static final ObjectProperty<ObservableList<Object>> instances = new SimpleObjectProperty<>();
    private static final StringProperty name = new SimpleStringProperty();
    private static final StringProperty version = new SimpleStringProperty();
    private static final StringProperty depInstance = new SimpleStringProperty();
    private static final StringProperty launcher = new SimpleStringProperty();
    private static final StringProperty sharer = new SimpleStringProperty();
    private static final BooleanProperty update = new SimpleBooleanProperty();
    private static final ObjectProperty<TreeItem<String>> root = new SimpleObjectProperty<>();
    private static final ObjectProperty<TreeItem<ModItem>> mods = new SimpleObjectProperty<>();

    static {
        try {
            new DiscoverInstanceTask(null).call();
        } catch (Exception e) {
            Log.error("搜索错误：{}", e);
            throw new RuntimeException(e);
        }
        instances.setValue(FXCollections.observableArrayList(Instance.list()));
        cur = instances.getValue().size() == 0 ? null : (Instance) instances.getValue().get(0);
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
        mods.setValue(fillModsTree(instance, false));
    }

    public static void refresh() {
        mods.setValue(fillModsTree(cur, true));
    }

    private static final Map<Instance, TreeItem<ModItem>> cache1 = new HashMap<>();

    private static TreeItem<ModItem> fillModsTree(Instance instance, boolean update) {

        if (!update && cache1.containsKey(instance)) return cache1.get(instance);
        var root = new TreeItem<>(new ModItem("全部Mods（" + instance.information.name + "）"));
        cache1.put(instance, root);

        var cache2 = new HashMap<String, TreeItem<ModItem>>();

        Instance instance1 = instance;

        while (instance1 != null) {
            var list = instance1.information.includeMods;
            var instanceName = instance1.information.name;
            for (var mod : list) {
                var tmp = mod.split("\\\\");
                for (int i = 0; i < tmp.length; i++) {
                    TreeItem<ModItem> item = cache2.get(tmp[i]);
                    if (item == null) {
                        item = new TreeItem<>(new ModItem(tmp[i], i == tmp.length - 1 ? mod : null, instanceName));
                        cache2.put(tmp[i], item);
                        if (i != 0) {
                            TreeItem<ModItem> pItem = cache2.get(tmp[i - 1]);
                            pItem.getChildren().add(item);
                        } else {
                            root.getChildren().add(item);
                        }
                    }
                }
            }

            instance1 = Instance.get(instance.information.depVersion);
        }

        var it = instance.information.excludeMods.iterator();
        while (it.hasNext()) {
            var name = it.next();
            var modName = name.substring(name.lastIndexOf("\\") + 1);
            var item = cache2.get(modName);
            if (item != null) {
                item.getValue().setDisable(true);
                item.getParent().getValue().setDisable(true);
            } else {
                it.remove();
                hasChange();
            }
        }

        Comparator<TreeItem<ModItem>> cmp = (modItemTreeItem, t1) -> modItemTreeItem.getValue()
            .compareTo(t1.getValue());
        root.getChildren().sort(cmp);
        for (var item : cache2.values()) {
            if (item.getChildren().size() != 0) item.getChildren().sort(cmp);
        }
        return root;
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

    public static ObjectProperty<TreeItem<ModItem>> modsProperty() {
        return mods;
    }

    public static ObjectProperty<ObservableList<Object>> instancesProperty() {
        return instances;
    }

    public static void hasChange() {

        if (!cur.information.name.equals(name.getValue())) {
            String oV = cur.information.name;
            String nV = name.getValue();
            if (!Instance.containsKey(nV)) {
                Instance.rename(oV, nV);
                instances.setValue(FXCollections.observableArrayList(Instance.list()));
            }
        }

        cur.information.version = version.getValue();
        cur.information.depVersion = depInstance.getValue();
        cur.information.sharer = sharer.getValue();
        cur.information.launcher = launcher.getValue();
        cur.information.update = update.getValue();

        cur.savaInformation();
    }

    private static final Map<Instance, TreeItem<String>> cache0 = new HashMap<>();

    private static TreeItem<String> fillFileTree(Instance instance, boolean update) {

        if (!update && cache0.containsKey(instance)) return cache0.get(instance);
        var root = new TreeItem<>("运行目录（" + instance.information.name + "）");
        cache0.put(instance, root);

        Scheduler.submitTasks(new Task<Boolean>(null) {

            @Override
            public Boolean call() {
                var myDirectory = instance.getMapper().getMyDirectory();
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
        treeItem.getChildren().addAll(list);
    }
}
