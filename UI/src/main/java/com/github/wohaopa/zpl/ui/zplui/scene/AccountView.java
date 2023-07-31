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

package com.github.wohaopa.zpl.ui.zplui.scene;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import com.github.wohaopa.zpl.ui.zplui.Accounts;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;

public class AccountView extends BaseMyScene {

    public AccountView() {
        super(() -> {
            var rootPane = new StackPane();

            var mainPane = new VBox();

            var accounts = new VBox();
            {
                accounts.setAlignment(Pos.TOP_CENTER);
            }
            var control = new HBox();
            {
                var sp1 = new Region();
                var sp2 = new Region();
                var text = new Label("所有账户");
                text.setTextFill(Color.WHITE);
                var refresh = new JFXButton("刷新");
                {
                    refresh.setTextFill(Color.WHITE);
                    refresh.setOnAction(event -> {
                        var list = accounts.getChildren();
                        list.clear();
                        Accounts.accountsProperty().get().forEach(o -> {
                            var hBox = new HBox();
                            var name = new Label(o.toString());
                            name.setTextFill(Color.WHITE);
                            hBox.setAlignment(Pos.CENTER);
                            hBox.getChildren().addAll(name);

                            list.add(hBox);
                        });
                    });
                    refresh.getOnAction().handle(null);
                }
                var add = new JFXButton("添加");
                {
                    add.setTextFill(Color.WHITE);

                    var dialog = new JFXDialog();
                    {
                        var layout = new JFXDialogLayout();
                        var heading = new Label("添加账户");
                        heading.setTextFill(Color.WHITE);
                        layout.setHeading(heading);
                        var img = new Image("/assets/img/bg1.jpg", 100, 100, false, true);
                        var tabPane = new TabPane();
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
                                textField.setPadding(new Insets(40, 0, 0, 0));

                                var addBtn = new JFXButton("添加");
                                addBtn.setTextFill(Color.WHITE);
                                addBtn.setPadding(new Insets(10, 20, 10, 20));

                                content.setAlignment(Pos.CENTER);
                                content.getChildren().addAll(textField, addBtn);

                                offlineTab.setContent(content);
                            }
                            var msTab = new Tab();
                            {
                                msTab.setText("微软登录");
                                msTab.setClosable(false);
                                var content = new VBox();

                                var msg = new Label("微软登录正在等待审核");

                                var code = new JFXTextField();
                                code.setEditable(false);
                                code.setLabelFloat(true);
                                code.setPromptText("验证码");
                                code.getStyleClass().add("zpl-text-field");
                                code.setPadding(new Insets(40, 0, 0, 0));
                                code.setText("假装这是验证码");

                                Hyperlink link = new Hyperlink("验证链接");
                                link.setOnAction(event -> {
                                    try {
                                        Desktop.getDesktop().browse(new URI("https://baidu.com/"));
                                    } catch (IOException | URISyntaxException e) {
                                        throw new RuntimeException(e);
                                    }
                                });

                                content.getChildren().addAll(msg, code, link);
                                content.setAlignment(Pos.CENTER);
                                msTab.setContent(content);
                            }
                            var yagtab = new Tab();
                            {
                                yagtab.setText("外置登录");
                                yagtab.setClosable(false);
                                var msg = new Label("外置登录正在开发中");

                                yagtab.setContent(msg);
                            }

                            tabPane.getTabs().addAll(offlineTab, msTab, yagtab);
                            tabPane.getSelectionModel().selectFirst();
                        }

                        var sp = new Region();
                        HBox.setHgrow(sp, Priority.ALWAYS);

                        layout.setBody(new HBox(tabPane, sp, new ImageView(img)));
                        // layout.setBody(tabPane,);
                        var acceptBtn = new JFXButton("完成");
                        acceptBtn.setTextFill(Color.WHITE);
                        acceptBtn.setOnAction(event -> { dialog.close(); });
                        layout.setActions(acceptBtn);
                        layout.setBackground(new Background(new BackgroundFill(Color.gray(0.2), null, null)));
                        dialog.setContent(layout);
                        dialog.setBackground(Background.EMPTY);
                    }
                    add.setOnAction(event -> dialog.show(rootPane));
                }

                HBox.setHgrow(sp1, Priority.ALWAYS);
                HBox.setHgrow(sp2, Priority.ALWAYS);
                control.setAlignment(Pos.CENTER);
                control.setMaxWidth(300);
                control.getChildren().addAll(refresh, sp1, text, sp2, add);
            }

            mainPane.setAlignment(Pos.TOP_CENTER);
            mainPane.getChildren().addAll(control, accounts);

            rootPane.getChildren().add(mainPane);
            return rootPane;
        });
    }

    @Override
    public Parent getIcon() {
        var img = new Label("账户");
        img.setTextFill(Color.WHITE);
        return img;
    }
}
