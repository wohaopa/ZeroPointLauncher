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

package com.github.wohaopa.zplui;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.DiscoverInstanceTask;
import com.github.wohaopa.zplui.dialog.DoneDialog;

public class Instances {

    private static final ObjectProperty<ObservableList<Object>> instances = new SimpleObjectProperty<>();

    private static Instance select;
    private static final StringProperty name = new SimpleStringProperty();
    private static final StringProperty version = new SimpleStringProperty();
    private static final StringProperty depInstance = new SimpleStringProperty();
    private static final StringProperty launcher = new SimpleStringProperty();
    private static final StringProperty sharer = new SimpleStringProperty();
    private static final BooleanProperty update = new SimpleBooleanProperty();
    private static final List<IChangeListener<Instance>> listeners = new ArrayList<>();

    static {
        new DiscoverInstanceTask(null).call();
        refresh();
    }

    public static ObjectProperty<ObservableList<Object>> instancesProperty() {
        return instances;
    }

    public static void refresh() {
        instances.setValue(FXCollections.observableArrayList(Instance.list()));
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

    public static StringProperty launcherProperty() {
        return launcher;
    }

    public static StringProperty sharerProperty() {
        return sharer;
    }

    public static BooleanProperty updateProperty() {
        return update;
    }

    public static void change(Instance newInstance) {
        if (select != newInstance && newInstance != null) {
            select = newInstance;

            name.setValue(newInstance.information.name);
            version.setValue(newInstance.information.version);
            depInstance.setValue(newInstance.information.depVersion);
            launcher.setValue(newInstance.information.launcher);
            sharer.setValue(newInstance.information.sharer);
            update.setValue(newInstance.information.update);

            try {
                for (IChangeListener<Instance> listener : listeners) {
                    listener.change(newInstance);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void addListener(IChangeListener<Instance> listener) {
        listeners.add(listener);
    }

    public static Instance getSelect() {
        if (select == null) {
            Log.warn("无实例！");
            var dialog = new DoneDialog("警告", "无实例！");
            dialog.show(ZplApplication.getRootPane());
        }
        return select;
    }
}
