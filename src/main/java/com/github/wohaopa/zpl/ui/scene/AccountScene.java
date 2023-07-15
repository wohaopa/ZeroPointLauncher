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

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import com.github.wohaopa.zeropointlanuch.core.Config;
import com.github.wohaopa.zeropointlanuch.core.auth.OfflineAuth;
import com.github.wohaopa.zpl.ui.AccountMaster;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.font.FontUsages;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.ui.stage.VStageInitParams;
import io.vproxy.vfx.ui.wrapper.FusionW;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;

public class AccountScene extends BaseVScene {

    VStage addStage;

    public AccountScene() {
        super(VSceneRole.MAIN);
        var pane = new VBox();
        {
            pane.setAlignment(Pos.CENTER);
            pane.setSpacing(2);
        }

        addStage = new VStage(new VStageInitParams().setMaximizeAndResetButton(false));
        {
            addStage.setTitle("添加账户");
            addStage.getInitialScene().enableAutoContentWidthHeight();

            var comboBox = new ComboBox<String>();
            {
                comboBox.setPrefWidth(200);
                comboBox.setPrefHeight(40);
                comboBox.getItems().add("离线登录");
                comboBox.getItems().add("微软登录");
                comboBox.getItems().add("外置登录");
                comboBox.getSelectionModel().selectFirst();
            }

            var waring = new ThemeLabel("微软登录正在等待Mojang AB\n的审核，外置登录正在开发中。");
            {
                waring.visibleProperty().bind(comboBox.getSelectionModel().selectedIndexProperty().greaterThan(0));
            }

            var offlineGrid = new GridPane();

            var offlineTextField = new TextField();
            var offlineText = new FusionW(offlineTextField);
            FontManager.get().setFont(FontUsages.tableCellText, offlineText.getLabel());
            {
                offlineGrid.add(new ThemeLabel("用户名："), 0, 0);
                offlineGrid.add(offlineText, 1, 0);
                offlineGrid.visibleProperty().bind(comboBox.getSelectionModel().selectedIndexProperty().isEqualTo(0));
            }

            var btn = new FusionButton("添加");
            {
                btn.setPrefWidth(200);
                btn.setPrefHeight(40);
                btn.setOnAction(event -> {
                    if (comboBox.getSelectionModel().getSelectedItem().equals("离线登录")) {
                        var name = offlineTextField.getText();
                        if (name == null || "".equals(name)) return;
                        AccountMaster.addAccount(new OfflineAuth(name));
                        updateVBox(pane);
                    }
                    addStage.close();
                });
            }

            addStage.getRoot().getContentPane().widthProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == null || newValue == null) return;
                comboBox.setLayoutX((newValue.doubleValue() - comboBox.getWidth()) * 0.5);
                waring.setLayoutX((newValue.doubleValue() - waring.getWidth()) * 0.5);
                offlineGrid.setLayoutX((newValue.doubleValue() - offlineGrid.getWidth()) * 0.5);
                btn.setLayoutX((newValue.doubleValue() - btn.getWidth()) * 0.5);
            });
            addStage.getRoot().getContentPane().heightProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == null || newValue == null) return;
                waring.setLayoutY((newValue.doubleValue() - waring.getHeight()) * 0.5);
                offlineGrid.setLayoutY((newValue.doubleValue() - offlineGrid.getHeight()) * 0.5);
                btn.setLayoutY((newValue.doubleValue() - 100));
            });
            addStage.getRoot().getContentPane().setMinHeight(360);
            addStage.getRoot().getContentPane().setMinWidth(400);
            addStage.getInitialScene().getContentPane().getChildren().addAll(comboBox, waring, offlineGrid, btn);
        }

        enableAutoContentWidthHeight();

        updateVBox(pane);
        var addBtn = new FusionButton("添加账户");
        {
            addBtn.setPrefHeight(40);
            addBtn.setPrefWidth(200);
            addBtn.setOnAction(event -> addStage.show());
        }

        getContentPane().widthProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null || newValue == null) return;
            pane.setLayoutX((newValue.doubleValue() - pane.getWidth()) * 0.5);
            addBtn.setLayoutX((newValue.doubleValue() - addBtn.getWidth()) * 0.5);
        });
        getContentPane().heightProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null || newValue == null) return;
            addBtn.setLayoutY((newValue.doubleValue() - addBtn.getHeight()));
        });

        getContentPane().getChildren().addAll(pane, addBtn);
    }

    private void updateVBox(VBox pane) {
        var children = pane.getChildren();
        children.clear();
        for (var auth : Config.getConfig().getAuths()) {
            var hBox = new HBox();
            hBox.getChildren().add(new ThemeLabel(auth.getName()));
            hBox.getChildren().add(new HPadding(15));
            hBox.getChildren().add(new ThemeLabel(auth.getUserType()));
            children.add(hBox);
        }
    }

    @Override
    public String title() {
        return "账户";
    }
}
