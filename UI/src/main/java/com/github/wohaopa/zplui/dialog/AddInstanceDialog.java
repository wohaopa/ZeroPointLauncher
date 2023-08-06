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
import java.util.LinkedList;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.ZplDirectory;
import com.github.wohaopa.zeropointlanuch.core.download.DownloadProvider;
import com.github.wohaopa.zeropointlanuch.core.tasks.DownloadTask;
import com.github.wohaopa.zeropointlanuch.core.tasks.Scheduler;
import com.github.wohaopa.zeropointlanuch.core.tasks.Task;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.OnlineInstallTask;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.StandardInstallTask;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.ZplInstallTask;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.StringUtil;
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

                    if (StringUtil.isNotEmpty(name)) {

                        var zip = new File(path);
                        var instanceDir = new File(ZplDirectory.getInstancesDirectory(), name);

                        if (zip.exists() && !instanceDir.exists()) {
                            Scheduler.submitTasks(
                                new ZplInstallTask(zip, instanceDir, name, s -> Platform.runLater(() -> msg.set(s))));
                            close();
                        } else {
                            msg.setValue("压缩包不存在！或实例名重复！");
                        }

                    } else {
                        msg.setValue("实例名不能为空");
                    }
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
                textField1.setEditable(false);

                var jfxComboBox = new JFXComboBox<OnlineInstanceItem>();
                jfxComboBox.setPromptText("远程版本");
                jfxComboBox.getStyleClass().add("zpl-combo-box");

                jfxComboBox.getValidators().add(validator);

                Scheduler.submitTasks(new Task<>(null) {

                    @Override
                    public Object call() throws Exception {
                        var file = new File(ZplDirectory.getInstancesDirectory(), "instances.json");
                        new DownloadTask(DownloadProvider.getUrlForFile(file), file, null).call();

                        JSONObject jsonObject = (JSONObject) JsonUtil.fromJson(file);
                        var insts = new LinkedList<OnlineInstanceItem>();
                        jsonObject.getJSONArray("instances").forEach(o -> {
                            if (o instanceof JSONObject object) {
                                insts.add(
                                    new OnlineInstanceItem(
                                        object.getStr("file"),
                                        object.getStr("desc"),
                                        object.getStr("name")));
                            }
                        });
                        Platform.runLater(() -> jfxComboBox.getItems().addAll(insts));

                        return null;
                    }
                });
                jfxComboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
                    if (!newVal) {
                        jfxComboBox.validate();
                    }
                });

                var textField2 = new JFXTextField();
                textField2.setPromptText("实例名");
                textField2.getStyleClass().add("zpl-text-field");
                textField2.setEditable(false);
                textField2.setText("请选择在线实例");
                jfxComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        textField1.setText(newValue.fileName);
                        textField2.setText(newValue.desc);
                    } else {
                        textField2.setText("请选择在线实例");
                    }
                });

                var btn = new JFXButton();
                btn.setText("安装");
                btn.setOnAction(event -> {
                    // var name = textField1.getText();
                    var online = jfxComboBox.getSelectionModel().getSelectedItem();

                    var msg = new SimpleStringProperty();
                    if (online != null) {
                        var name = online.fileName;
                        var instanceDir = new File(ZplDirectory.getInstancesDirectory(), name);
                        if (!instanceDir.exists()) {
                            Scheduler.submitTasks(
                                new OnlineInstallTask(instanceDir, name, s -> Platform.runLater(() -> msg.set(s))));
                            close();
                        } else {
                            msg.setValue("压缩包不存在！或实例名重复！");
                        }
                    } else {
                        msg.setValue("请选择在线实例！");
                    }
                    new DoneDialog("实例安装", msg).show(ZplApplication.getRootPane());
                });

                textField1.widthProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        jfxComboBox.setPrefWidth(newValue.doubleValue());
                        btn.setPrefWidth(newValue.doubleValue());
                    }
                });

                content.setAlignment(Pos.CENTER);
                content.setSpacing(25);
                content.getChildren().addAll(textField1, jfxComboBox, textField2, btn);

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
                    if (StringUtil.isNotEmpty(name)) {
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
                            close();
                        } else {
                            msg.setValue("压缩包不存在！或实例名重复！");
                        }
                    } else {
                        msg.setValue("实例名不能为空");
                    }
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

    private static class OnlineInstanceItem {

        public final String fileName;
        public final String desc;
        public final String displayName;

        private OnlineInstanceItem(String fileName, String desc, String displayName) {
            this.fileName = fileName;
            this.desc = desc;
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
