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

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import com.github.wohaopa.zeropointlanuch.core.auth.Auth;
import com.github.wohaopa.zplui.Accounts;
import com.github.wohaopa.zplui.ZplApplication;
import com.github.wohaopa.zplui.dialog.AddAccountDialog;
import com.jfoenix.controls.*;

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
                            if (o instanceof Auth auth) {
                                var hBox = new HBox();
                                var name = new Label(auth.getName());
                                var type = new Label(auth.getUserType().name());
                                name.setTextFill(Color.WHITE);
                                hBox.setAlignment(Pos.CENTER);
                                hBox.getChildren().addAll(name, type);

                                list.add(hBox);
                            }
                        });
                    });
                    refresh.getOnAction().handle(null);
                }
                var add = new JFXButton("添加");
                {
                    add.setTextFill(Color.WHITE);

                    var dialog = new AddAccountDialog(() -> {
                        refresh.getOnAction().handle(null);
                        return null;
                    });

                    add.setOnAction(event -> dialog.show(ZplApplication.getRootPane()));
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
