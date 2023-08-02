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

import java.util.HashMap;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zplui.Instances;
import com.github.wohaopa.zplui.ZplApplication;
import com.github.wohaopa.zplui.dialog.AddInstanceDialog;
import com.jfoenix.controls.*;

public class InstanceView extends BaseMyScene {

    static JFXTreeView<String> instances;
    static Map<String, TreeItem> cache;

    public InstanceView() {
        super(() -> {
            var rootPane = new HBox();

            var treeView = new JFXTreeView<String>();
            {
                instances = treeView;
                var rootTreeItem = new TreeItem<>("实例继承图");
                {
                    cache = new HashMap<>();
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

                treeView.setRoot(rootTreeItem);
                treeView.getSelectionModel().select(cache.get(ZplApplication.getSelectInstance().information.name));

                var dialog = new AddInstanceDialog();

                var menu = new ContextMenu();
                {
                    var newInstance = new MenuItem("新建实例");
                    newInstance.setOnAction(event -> {
                        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
                        dialog.show(ZplApplication.getRootPane());
                    });
                    menu.getItems().addAll(newInstance);
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

                gridPane.add(textFileWrapper("测试用例"), 1, 0);
                gridPane.add(textFileWrapper("测试用例版本"), 1, 1);
                gridPane.add(textFileWrapper("null"), 1, 2);
                gridPane.add(textFileWrapper("默认"), 1, 3);
                gridPane.add(textFileWrapper("默认"), 1, 4);
                gridPane.add(textFileWrapper("False"), 1, 5);
            }
            var controlView = new VBox();
            {
                controlView.getStyleClass().add("control-view");
                var openInstanceDir = new JFXButton("打开实例目录");
                var openRuntimeDir = new JFXButton("打开运行目录");
                var openModsManager = new JFXButton("Mods管理");
                {
                    var modsManager = new JFXDialog();
                    {
                        var layout = new JFXDialogLayout();
                        layout.getStyleClass().add("dialog-layout");

                        var heading = new Label("Mods管理");
                        heading.getStyleClass().add("dialog-title");
                        layout.setHeading(heading);

                        var body = new JFXTreeView<>();
                        {}

                        modsManager.setContent(layout);
                    }

                    openModsManager.setOnAction(event -> {
                        modsManager.setTransitionType(JFXDialog.DialogTransition.CENTER);
                        modsManager.show(ZplApplication.getRootPane());
                    });
                }

                var openMapManager = new JFXButton("映射管理");
                var refreshInstance = new JFXButton("刷新实例");
                var newSubInstance = new JFXButton("新建子实例");
                var updateVersion = new JFXButton("版本更新");
                var extractInstance = new JFXButton("导出实例");

                controlView.getChildren()
                    .addAll(
                        openInstanceDir,
                        openRuntimeDir,
                        openModsManager,
                        openMapManager,
                        refreshInstance,
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

    private static Node textFileWrapper(String s) {
        var textField = new JFXTextField();
        textField.setText(s);
        // textField.textProperty().bindBidirectional();

        return textField;
    }

    @Override
    public Parent getIcon() {
        var img = new Label("实例");
        img.setTextFill(Color.WHITE);
        return img;
    }
}
