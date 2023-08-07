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

package com.github.wohaopa.zplui.scene;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zeropointlanuch.core.ZplDirectory;
import com.github.wohaopa.zeropointlanuch.core.tasks.Scheduler;
import com.github.wohaopa.zeropointlanuch.core.tasks.Task;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.NewSubInstanceTask;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.RefreshInstanceTask;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.ZplExtractTask;
import com.github.wohaopa.zplui.Instances;
import com.github.wohaopa.zplui.ZplApplication;
import com.github.wohaopa.zplui.dialog.AddInstanceDialog;
import com.github.wohaopa.zplui.dialog.DoneDialog;
import com.github.wohaopa.zplui.dialog.MapManagerDialog;
import com.github.wohaopa.zplui.dialog.ModsManagerDialog;
import com.github.wohaopa.zplui.util.DesktopUtils;
import com.github.wohaopa.zplui.util.FXUtils;
import com.github.wohaopa.zplui.util.Lazy;
import com.jfoenix.controls.*;

public class InstanceView extends BaseMyScene {

    private static Map<String, TreeItem<String>> cache = new HashMap<>();

    public InstanceView() {
        super(() -> {
            var rootPane = new HBox();

            var treeView = new JFXTreeView<String>();
            {
                var rootTreeItem = new TreeItem<>("实例继承图");
                refreshCall(rootTreeItem);

                treeView.setRoot(rootTreeItem);
                treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        Instances.change(Instance.get(newValue.getValue()));
                    }
                });
                if (Instances.getSelect() != null)
                    treeView.getSelectionModel().select(cache.get(Instances.getSelect().information.name));
                Instances
                    .addListener(newValue -> treeView.getSelectionModel().select(cache.get(newValue.information.name)));

                var dialog = new Lazy<>(AddInstanceDialog::new);

                var menu = new ContextMenu();
                {
                    var newInstance = new MenuItem("新建实例");
                    newInstance.setOnAction(event -> {
                        dialog.getValue().setTransitionType(JFXDialog.DialogTransition.CENTER);
                        dialog.getValue().show(ZplApplication.getRootPane());
                    });
                    var refresh = new MenuItem("刷新");
                    refresh.setOnAction(event -> refreshCall(rootTreeItem));
                    menu.getItems().addAll(newInstance, refresh);
                }
                treeView.setContextMenu(menu);
            }

            var gridPane = new GridPane();
            {
                gridPane.getStyleClass().add("instance-info-grid");

                ColumnConstraints column1 = new ColumnConstraints(70, 70, 100);
                ColumnConstraints column2 = new ColumnConstraints(50, 150, 300);
                column1.setHgrow(Priority.ALWAYS);
                gridPane.getColumnConstraints().addAll(column1, column2);

                gridPane.add(new Label("实例名："), 0, 0);
                gridPane.add(new Label("版本："), 0, 1);
                gridPane.add(new Label("继承："), 0, 2);
                gridPane.add(new Label("启动器："), 0, 3);
                gridPane.add(new Label("分享器："), 0, 4);
                gridPane.add(new Label("自更新："), 0, 5);

                gridPane.add(textFileWrapper(Instances.nameProperty()), 1, 0);
                gridPane.add(textFileWrapper(Instances.versionProperty()), 1, 1);
                gridPane.add(textFileWrapper(Instances.depInstanceProperty()), 1, 2);
                gridPane.add(textFileWrapper(Instances.launcherProperty()), 1, 3);
                gridPane.add(textFileWrapper(Instances.sharerProperty()), 1, 4);
                var toggle = new JFXToggleButton();
                toggle.selectedProperty().bindBidirectional(Instances.updateProperty());

                gridPane.add(toggle, 1, 5);
            }
            var controlView = new VBox();
            {
                controlView.getStyleClass().add("control-view");
                var openInstanceDir = new JFXButton("打开实例目录");
                openInstanceDir.setOnAction(event -> DesktopUtils.openFileLocation(Instances.getSelect().insDir));

                var openRuntimeDir = new JFXButton("打开运行目录");
                openRuntimeDir.setOnAction(event -> DesktopUtils.openFileLocation(Instances.getSelect().runDir));

                var openModsManager = new JFXButton("Mods管理");
                {
                    openModsManager.setDisable(true);
                    var dialog = new ModsManagerDialog();

                    openModsManager.setOnAction(event -> {
                        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
                        dialog.show(ZplApplication.getRootPane());
                    });
                }

                var openMapManager = new JFXButton("映射管理");
                {
                    openMapManager.setDisable(true);
                    var dialog = new MapManagerDialog();

                    openMapManager.setOnAction(event -> {
                        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
                        dialog.show(ZplApplication.getRootPane());
                    });
                }

                var refreshInstance = new JFXButton("刷新实例");
                {
                    refreshInstance.setOnAction(event -> {
                        var msg = new SimpleStringProperty("正在等待...");
                        var dialog = new DoneDialog("刷新实例：" + Instances.getSelect().information.name, msg);

                        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
                        dialog.show(ZplApplication.getRootPane());
                        Scheduler.submitTasks(
                            new RefreshInstanceTask(Instances.getSelect(), s -> FXUtils.runFX(() -> msg.setValue(s))));
                    });
                }

                var newSubInstance = new JFXButton("新建子实例");
                {
                    newSubInstance.setOnAction(event -> {
                        var msg = new SimpleStringProperty("正在等待...");
                        var dialog = new DoneDialog("新建子实例：" + Instances.getSelect().information.name, msg);

                        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
                        dialog.show(ZplApplication.getRootPane());
                        var name = Instances.getSelect().information.name + "-sub";
                        var instanceDir = new File(ZplDirectory.getInstancesDirectory(), name);

                        Scheduler.submitTasks(
                            new NewSubInstanceTask(
                                instanceDir,
                                name,
                                Instances.getSelect(),
                                s -> FXUtils.runFX(() -> msg.setValue(s))));
                    });
                }

                var updateVersion = new JFXButton("版本更新");
                updateVersion.setDisable(true);

                var extractInstance = new JFXButton("导出实例");
                {
                    extractInstance.setOnAction(event -> {
                        var msg = new SimpleStringProperty("正在等待...");
                        var dialog = new DoneDialog("导出实例：" + Instances.getSelect().information.name, msg);

                        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
                        dialog.show(ZplApplication.getRootPane());
                        var instance = Instances.getSelect();
                        var zip = new File(instance.insDir, instance.information.name + ".zip");
                        if (zip.exists()) zip.delete();
                        Scheduler.submitTasks(
                            new ZplExtractTask(zip, instance.insDir, s -> FXUtils.runFX(() -> msg.setValue(s))));
                        DesktopUtils.openFileLocation(instance.insDir);
                    });
                }
                var transfer = new JFXButton("迁移旧实例");
                {
                    transfer.setDisable(true);
                }

                var clean = new JFXButton("清理运行目录");
                {
                    {
                        clean.setOnAction(event -> {
                            var msg = new SimpleStringProperty("正在等待...");
                            var dialog = new DoneDialog("清理运行目录：" + Instances.getSelect().information.name, msg);

                            dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
                            dialog.show(ZplApplication.getRootPane());
                            var instance = Instances.getSelect();

                            Scheduler.submitTasks(new Task<>(s -> FXUtils.runFX(() -> msg.setValue(s))) {

                                @Override
                                public Object call() {
                                    instance.clean();
                                    accept("完成！");
                                    return null;
                                }
                            });
                        });
                    }
                }

                controlView.getChildren()
                    .addAll(
                        openInstanceDir,
                        openRuntimeDir,
                        openModsManager,
                        openMapManager,
                        refreshInstance,
                        clean,
                        transfer,
                        newSubInstance,
                        updateVersion,
                        extractInstance);
            }

            rootPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) return;
                var size = newValue.doubleValue() * 0.333;
                treeView.setPrefWidth(size);
                gridPane.setPrefWidth(size);
                controlView.setPrefWidth(size);
            });
            rootPane.getChildren().addAll(treeView, gridPane, controlView);

            return rootPane;
        });
    }

    private static Node textFileWrapper(Property<String> property) {
        var textField = new JFXTextField();
        textField.textProperty().bindBidirectional(property);

        return textField;
    }

    @Override
    public Parent getIcon() {
        var img = new Label("实例");
        img.setTextFill(Color.WHITE);
        return img;
    }

    public static void refreshCall(TreeItem<String> rootTreeItem) {
        cache.clear();
        rootTreeItem.getChildren().clear();
        Instances.refresh();
        Instances.instancesProperty().get().forEach(o -> {
            if (o instanceof Instance instance) {
                cache.put(instance.information.name, new TreeItem<>(instance.information.name));
            }
        });
        cache.forEach((s, o) -> {
            var dep = Instance.get(s).information.depVersion;
            if ("null".equals(dep)) {
                rootTreeItem.getChildren().add(o);
            } else if (cache.get(dep) != null) {
                cache.get(dep).getChildren().add(o);
            }
        });
    }
}
