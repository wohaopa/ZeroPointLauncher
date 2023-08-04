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

package com.github.wohaopa.zplui.dialog;

import java.io.File;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import com.github.wohaopa.zeropointlanuch.core.ZplDirectory;
import com.github.wohaopa.zeropointlanuch.core.tasks.Scheduler;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.StandardInstallTask;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.ZplInstallTask;
import com.github.wohaopa.zplui.ZplApplication;
import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;

public class AddInstanceDialog extends BaseDialog {

    public AddInstanceDialog() {
        var layout = new JFXDialogLayout();
        layout.getStyleClass().add("dialog-layout");

        var heading = new Label("新建实例");
        heading.getStyleClass().add("dialog-title");
        layout.setHeading(heading);

        var tabPane = new JFXTabPane();
        {
            var zplTab = new Tab();
            {
                zplTab.setText("导出包安装");
                zplTab.setClosable(false);
                var content = new VBox();

                var validator = new RequiredFieldValidator();
                validator.setMessage("必填！");

                var textField1 = new JFXTextField();
                textField1.setPromptText("实例名");
                textField1.getStyleClass().add("zpl-text-field");

                textField1.getValidators().add(validator);
                textField1.focusedProperty().addListener((o, oldVal, newVal) -> {
                    if (!newVal) {
                        textField1.validate();
                    }
                });

                var textField2 = new JFXTextField();
                textField2.setPromptText("安装包路径");
                textField2.getStyleClass().add("zpl-text-field");

                textField2.getValidators().add(validator);
                textField2.focusedProperty().addListener((o, oldVal, newVal) -> {
                    if (!newVal) {
                        textField2.validate();
                    }
                });

                var btn = new JFXButton();
                btn.setText("安装");
                btn.setOnAction(event -> {
                    var name = textField1.getText();
                    var path = textField2.getText();

                    var msg = new SimpleStringProperty();
                    var zip = new File(path);
                    var instanceDir = new File(ZplDirectory.getInstancesDirectory(), name);
                    if (zip.exists() && !instanceDir.exists()) {
                        Scheduler.submitTasks(
                            new ZplInstallTask(zip, instanceDir, name, s -> Platform.runLater(() -> msg.set(s))));
                    }
                    close();
                    new DoneDialog("实例安装", msg).show(ZplApplication.getRootPane());
                });

                content.getStyleClass().add("vbox");
                content.getChildren().addAll(textField1, textField2, btn);

                textField1.widthProperty()
                    .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue != null) btn.setPrefWidth(newValue.doubleValue());
                        });

                content.setPadding(new Insets(20, 0, 20, 0));

                zplTab.setContent(content);
            }
            var onlineTab = new Tab();
            {
                onlineTab.setText("在线安装");
                onlineTab.setClosable(false);
                var content = new VBox();

                var validator = new RequiredFieldValidator();
                validator.setMessage("必填！");

                var textField1 = new JFXTextField();
                textField1.setPromptText("实例名");
                textField1.getStyleClass().add("zpl-text-field");

                textField1.getValidators().add(validator);
                textField1.focusedProperty().addListener((o, oldVal, newVal) -> {
                    if (!newVal) {
                        textField1.validate();
                    }
                });

                var jfxComboBox = new JFXComboBox<>();
                jfxComboBox.setPromptText("远程版本");
                jfxComboBox.getStyleClass().add("zpl-combo-box");

                jfxComboBox.getValidators().add(validator);
                jfxComboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
                    if (!newVal) {
                        jfxComboBox.validate();
                    }
                });
                var btn = new JFXButton();
                btn.setText("安装");

                textField1.widthProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        jfxComboBox.setPrefWidth(newValue.doubleValue());
                        btn.setPrefWidth(newValue.doubleValue());
                    }
                });

                content.setAlignment(Pos.CENTER);
                content.setSpacing(25);
                content.getChildren().addAll(textField1, jfxComboBox, btn);

                content.setPadding(new Insets(20, 0, 20, 0));
                onlineTab.setContent(content);
            }
            var gtnhtab = new Tab();
            {
                gtnhtab.setText("GTNH压缩包");
                gtnhtab.setClosable(false);
                var content = new VBox();

                var validator = new RequiredFieldValidator();
                validator.setMessage("必填！");

                var textField1 = new JFXTextField();
                textField1.setPromptText("实例名");
                textField1.getStyleClass().add("zpl-text-field");
                textField1.getValidators().add(validator);
                textField1.focusedProperty().addListener((o, oldVal, newVal) -> {
                    if (!newVal) {
                        textField1.validate();
                    }
                });

                var textField2 = new JFXTextField();
                textField2.setPromptText("版本");
                textField2.getStyleClass().add("zpl-text-field");
                textField2.getValidators().add(validator);
                textField2.focusedProperty().addListener((o, oldVal, newVal) -> {
                    if (!newVal) {
                        textField2.validate();
                    }
                });

                var textField3 = new JFXTextField();
                textField3.setPromptText("安装包路径");
                textField3.getStyleClass().add("zpl-text-field");

                textField3.getValidators().add(validator);
                textField3.focusedProperty().addListener((o, oldVal, newVal) -> {
                    if (!newVal) {
                        textField3.validate();
                    }
                });

                var btn = new JFXButton();
                btn.setText("安装");
                btn.setOnAction(event -> {
                    var name = textField1.getText();
                    var version = textField2.getText();
                    var path = textField3.getText();

                    var msg = new SimpleStringProperty();
                    var zip = new File(path);
                    var instanceDir = new File(ZplDirectory.getInstancesDirectory(), name);
                    if (zip.exists() && !instanceDir.exists()) {
                        Scheduler.submitTasks(
                            new StandardInstallTask(
                                zip,
                                instanceDir,
                                name,
                                version,
                                s -> Platform.runLater(() -> msg.set(s))));
                    }
                    close();
                    new DoneDialog("实例安装", msg).show(ZplApplication.getRootPane());
                });

                textField1.widthProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        btn.setPrefWidth(newValue.doubleValue());
                    }
                });

                content.getStyleClass().add("vbox");
                content.getChildren().addAll(textField1, textField2, textField3, btn);

                content.setPadding(new Insets(20, 0, 20, 0));

                gtnhtab.setContent(content);
            }

            tabPane.getStyleClass().add("dialog-content");
            tabPane.getTabs().addAll(zplTab, onlineTab, gtnhtab);
            tabPane.getSelectionModel().selectFirst();
        }

        layout.setBody(tabPane);

        this.setContent(layout);
    }
}
