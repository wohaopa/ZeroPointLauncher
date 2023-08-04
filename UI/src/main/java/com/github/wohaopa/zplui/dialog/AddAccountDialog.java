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

import java.awt.*;
import java.util.concurrent.Callable;
import javafx.application.Platform;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import com.github.wohaopa.zeropointlanuch.core.tasks.Scheduler;
import com.github.wohaopa.zeropointlanuch.core.tasks.Task;
import com.github.wohaopa.zplui.Accounts;
import com.github.wohaopa.zplui.ZplApplication;
import com.github.wohaopa.zplui.util.DesktopUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;

public class AddAccountDialog extends BaseDialog {

    public AddAccountDialog(Callable<Object> refresh) {
        var layout = new JFXDialogLayout();
        var heading = new Label("添加账户");
        heading.getStyleClass().add("dialog-title");
        layout.setHeading(heading);

        var img = new Image("/assets/img/bg1.jpg", 296 * 0.4, 256 * 0.4, false, true);
        var tabPane = new JFXTabPane();
        {
            var offlineTab = new Tab();
            {
                offlineTab.setText("离线登录");
                offlineTab.setClosable(false);
                var content = new VBox();

                var textField = new JFXTextField();
                textField.setLabelFloat(true);
                textField.setPromptText("用户名");
                textField.getStyleClass().add("zpl-text-field");

                var addBtn = new JFXButton("添加");

                addBtn.setOnAction(event -> {
                    var name = textField.getText();
                    textField.setText("");
                    Accounts.addOfflineAccount(name);
                    this.close();
                    try {
                        refresh.call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                content.getStyleClass().add("vbox");
                content.getChildren().addAll(textField, addBtn);

                textField.widthProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue == null) return;
                    addBtn.setPrefWidth(newValue.doubleValue());
                });

                offlineTab.setContent(content);
            }
            var msTab = new Tab();
            {
                msTab.setText("微软登录");
                msTab.setClosable(false);

                var content = new VBox();

                var getCode = new JFXButton("点击获取验证码");

                var code = new JFXTextField();
                code.setEditable(false);
                code.setLabelFloat(true);
                code.setPromptText("验证码");
                code.getStyleClass().add("zpl-text-field");

                code.setText("正在等待获取...");
                getCode.setOnAction(event -> Scheduler.submitTasks(new Task<>(null) {

                    @Override
                    public Object call() {
                        Accounts.addMSAccounts(entries -> Platform.runLater(() -> {
                            var str = entries.getStr("user_code");
                            code.setText(str);
                        }));
                        Platform.runLater(() -> {
                            close();
                            new DoneDialog("微软登录", "完成").show(ZplApplication.getRootPane());
                            try {
                                refresh.call();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });

                        return null;
                    }
                }));

                Hyperlink link = new Hyperlink("验证链接");
                link.setOnAction(event -> DesktopUtils.openLink("https://www.microsoft.com/link"));

                content.getStyleClass().add("vbox");
                content.getChildren().addAll(getCode, code, link);

                code.widthProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue == null) return;
                    getCode.setPrefWidth(newValue.doubleValue());
                    link.setPrefWidth(newValue.doubleValue());
                });

                msTab.setContent(content);
            }
            var yagtab = new Tab();
            {
                yagtab.setText("外置登录");
                yagtab.setClosable(false);
                var msg = new Label("外置登录正在开发中");

                yagtab.setContent(msg);
            }

            tabPane.getStyleClass().add("dialog-content");
            tabPane.getTabs().addAll(offlineTab, msTab, yagtab);
            tabPane.getSelectionModel().selectFirst();
        }

        var sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        layout.setBody(new HBox(tabPane, sp, new ImageView(img)));
        var acceptBtn = new JFXButton("完成");
        acceptBtn.setTextFill(Color.WHITE);
        acceptBtn.setOnAction(event -> { close(); });
        layout.setActions(acceptBtn);
        layout.getStyleClass().add("dialog-layout");
        this.setContent(layout);
    }
}
