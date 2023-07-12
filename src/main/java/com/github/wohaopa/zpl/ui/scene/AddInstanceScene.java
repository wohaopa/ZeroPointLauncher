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

package com.github.wohaopa.zpl.ui.scene;

import java.io.File;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import com.github.wohaopa.zeropointlanuch.core.ZplDirectory;
import com.github.wohaopa.zeropointlanuch.core.tasks.Scheduler;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.StandardInstallTask;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.ZplInstallTask;

import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;

public class AddInstanceScene extends BaseVScene {

    public AddInstanceScene() {
        super(VSceneRole.MAIN);
        enableAutoContentWidthHeight();

        var comboBox = new ComboBox<String>();
        {
            comboBox.setPrefWidth(200);
            comboBox.setPrefHeight(40);
            comboBox.getItems().add("ZPL导出包安装");
            comboBox.getItems().add("ZPL在线安装");
            comboBox.getItems().add("GTNH-Client包安装");
            comboBox.getSelectionModel().selectFirst();
        }

        var waring = new ThemeLabel("在线安装仍在开发中");
        {
            waring.visibleProperty().bind(comboBox.getSelectionModel().selectedIndexProperty().isEqualTo(1));
        }

        var grid = new GridPane();
        var nameTextField = new TextField();
        var zipTextField = new TextField();
        var versionTextField = new TextField();

        {
            grid.add(new ThemeLabel("实例名："), 0, 0);
            grid.add(new ThemeLabel("安装包路径："), 0, 1);
            grid.add(new ThemeLabel("版本："), 0, 2);
            grid.add(nameTextField, 1, 0);
            grid.add(zipTextField, 1, 1);
            grid.add(versionTextField, 1, 2);
        }
        grid.visibleProperty().bind(comboBox.getSelectionModel().selectedIndexProperty().isNotEqualTo(1));
        versionTextField.visibleProperty().bind(comboBox.getSelectionModel().selectedIndexProperty().isEqualTo(0));

        var btn = new FusionButton("添加");
        {
            btn.setPrefWidth(200);
            btn.setPrefHeight(40);
            btn.setOnAction(event -> {
                if (comboBox.getSelectionModel().getSelectedItem().equals("ZPL导出包安装")) {
                    var name = nameTextField.getText();
                    if (name == null || "".equals(name)) return;
                    File file = null;
                    try {
                        file = new File(zipTextField.getText());
                    } catch (Exception ignored) {}
                    if (file == null || !file.isFile()) return;
                    var instanceDir = new File(ZplDirectory.getInstancesDirectory(), name);
                    if (instanceDir.exists()) return;

                    Scheduler.submitTasks(new ZplInstallTask(file, instanceDir, name, null));
                } else if (comboBox.getSelectionModel().getSelectedItem().equals("ZPL在线安装")) {

                } else if (comboBox.getSelectionModel().getSelectedItem().equals("GTNH-Client包安装")) {
                    var name = nameTextField.getText();
                    if (name == null || "".equals(name)) return;
                    File file = null;
                    try {
                        file = new File(zipTextField.getText());
                    } catch (Exception ignored) {}
                    if (file == null || !file.isFile()) return;
                    var instanceDir = new File(ZplDirectory.getInstancesDirectory(), name);
                    if (instanceDir.exists()) return;
                    var version = versionTextField.getText();
                    if (version == null || version.isEmpty()) return;

                    Scheduler.submitTasks(new StandardInstallTask(file, instanceDir, name, version, null));
                }
            });
        }

        getContentPane().widthProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null || newValue == null) return;
            comboBox.setLayoutX((newValue.doubleValue() - comboBox.getWidth()) * 0.5);
            waring.setLayoutX((newValue.doubleValue() - waring.getWidth()) * 0.5);
            grid.setLayoutX((newValue.doubleValue() - grid.getWidth()) * 0.5);
            btn.setLayoutX((newValue.doubleValue() - btn.getWidth()) * 0.5);
        });
        getContentPane().heightProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null || newValue == null) return;
            waring.setLayoutY((newValue.doubleValue() - waring.getHeight()) * 0.5);
            grid.setLayoutY((newValue.doubleValue() - grid.getHeight()) * 0.5);
            btn.setLayoutY((newValue.doubleValue() - 100));
        });

        getContentPane().getChildren().addAll(comboBox, waring, grid, btn);
    }

    @Override
    public String title() {
        return "新建实例";
    }
}
