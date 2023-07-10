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

package com.github.wohaopa.zpl.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zpl.ui.scene.*;

import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.*;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.util.FXUtils;

public class Main extends Application {

    private final List<BaseVScene> mainScenes = new ArrayList<>();
    private VSceneGroup sceneGroup;

    @Override
    public void start(Stage primaryStage) throws IOException {
        ImageManager.get().loadBlackAndChangeColor("/images/menu.png", Map.of("white", 0xffffffff));
        // ImageManager.get().loadBlackAndChangeColor("/images/up-arrow.png", Map.of("white",
        // 0xffffffff));

        var stage = new VStage(primaryStage) {

            @Override
            public void close() {
                super.close();
                TaskManager.get().terminate();
                GlobalScreenUtils.unregister();
                Platform.exit();
                System.exit(0);
            }
        };
        stage.getInitialScene().enableAutoContentWidthHeight();

        stage.setTitle("ZeroPointLauncher - A GTNH Launcher");

        mainScenes.add(new HomeScene());
        mainScenes.add(new InstanceScene());
        mainScenes.add(new AddInstanceScene());
        mainScenes.add(new AccountScene());
        mainScenes.add(new SettingScene());

        var initialScene = mainScenes.get(0);
        sceneGroup = new VSceneGroup(initialScene);
        for (var s : mainScenes) {
            if (s == initialScene) continue;
            sceneGroup.addScene(s);
        }

        // 启动栏
        var launchPane = new FusionPane();
        {
            launchPane.getNode().setPrefHeight(60);

            var accountButton = new ChoiceBox<>(AccountMaster.getAccounts());

            {
                accountButton.setPrefWidth(200);
                accountButton.setPrefHeight(launchPane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);

                accountButton.setBackground(
                    new Background(
                        new BackgroundFill(
                            Theme.current().subSceneBackgroundColor(),
                            CornerRadii.EMPTY,
                            Insets.EMPTY)));
                accountButton.getSelectionModel()
                    .selectedItemProperty()
                    .addListener(
                        (observable, oldValue, newValue) -> { if (newValue != null) AccountMaster.change(newValue); });
                accountButton.getSelectionModel().select(AccountMaster.getCur());
            }

            var instanceButton = new ChoiceBox<>();

            {
                instanceButton.setPrefWidth(200);
                instanceButton.setPrefHeight(launchPane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);

                instanceButton.setBackground(
                    new Background(
                        new BackgroundFill(
                            Theme.current().subSceneBackgroundColor(),
                            CornerRadii.EMPTY,
                            Insets.EMPTY)));
                instanceButton.getStylesheets().add("css/choice-box.css");

                instanceButton.itemsProperty().bind(InstanceMaster.instancesProperty());
                instanceButton.getSelectionModel()
                    .selectedItemProperty()
                    .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue != null) InstanceMaster.change((Instance) newValue);
                        });
                instanceButton.getSelectionModel().select(InstanceMaster.getCur());
            }

            var launchButton = new FusionButton("启动");
            {
                launchButton.setPrefWidth(200);
                launchButton.setPrefHeight(launchPane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);
                launchButton.setOnlyAnimateWhenNotClicked(true);
            }

            launchPane.getContentPane().getChildren().add(accountButton);
            launchPane.getContentPane().getChildren().add(instanceButton);
            launchPane.getContentPane().getChildren().add(launchButton);

            launchPane.getContentPane().widthProperty().addListener((ob, old, now) -> {
                if (now == null) return;
                var v = now.doubleValue();
                instanceButton.setLayoutX((v - instanceButton.getPrefWidth()) / 2);
                launchButton.setLayoutX(v - launchButton.getPrefWidth());
            });
        }

        var box = new HBox(
            new HPadding(10),
            new VBox(new VPadding(10), sceneGroup.getNode(), new VPadding(5), launchPane.getNode()),
            new HPadding(10));

        stage.getInitialScene().getContentPane().getChildren().add(box);

        FXUtils.observeHeight(stage.getInitialScene().getContentPane(), sceneGroup.getNode(), -10 - 60 - 5 - 10);
        FXUtils.observeWidth(stage.getInitialScene().getContentPane(), sceneGroup.getNode(), -20);
        FXUtils.observeWidth(stage.getInitialScene().getContentPane(), launchPane.getNode(), -20);

        // 菜单
        var menuScene = new VScene(VSceneRole.DRAWER_VERTICAL);
        {
            menuScene.getNode().setPrefWidth(150);
            // menuScene.enableAutoContentWidth();
            menuScene.enableAutoContentWidth();
            menuScene.getNode()
                .setBackground(
                    new Background(
                        new BackgroundFill(
                            Theme.current().subSceneBackgroundColor(),
                            CornerRadii.EMPTY,
                            Insets.EMPTY)));
            stage.getRootSceneGroup().addScene(menuScene, VSceneHideMethod.TO_LEFT);
            var menuVBox = new VBox() {

                {
                    setPadding(new Insets(10, 0, 10, 24));

                    getChildren().add(new VPadding(5));
                }
            };
            menuScene.getContentPane().getChildren().add(menuVBox);
            for (int i = 0; i < mainScenes.size(); ++i) {
                final var fi = i;
                var s = mainScenes.get(i);
                var title = s.title();
                var button = new FusionButton(title);
                button.setDisableAnimation(true);
                button.setOnAction(e -> {
                    // noinspection SuspiciousMethodCalls
                    var currentIndex = mainScenes.indexOf(sceneGroup.getCurrentMainScene());
                    if (currentIndex != fi) {
                        sceneGroup
                            .show(s, currentIndex < fi ? VSceneShowMethod.FROM_RIGHT : VSceneShowMethod.FROM_LEFT);
                    }
                    stage.getRootSceneGroup().hide(menuScene, VSceneHideMethod.TO_LEFT);
                });
                button.setPrefWidth(100);
                button.setPrefHeight(40);
                if (i != 0) {
                    menuVBox.getChildren().add(new VPadding(5));
                }
                menuVBox.getChildren().add(button);
            }
            menuVBox.getChildren().add(new VPadding(5));
        }

        var menuBtn = new FusionImageButton(ImageManager.get().load("images/menu.png:white")) {

            {
                setPrefWidth(40);
                setPrefHeight(VStage.TITLE_BAR_HEIGHT + 1);
                getImageView().setFitHeight(15);
                setLayoutX(-2);
                setLayoutY(-1);
            }
        };
        menuBtn.setOnAction(e -> stage.getRootSceneGroup().show(menuScene, VSceneShowMethod.FROM_LEFT));
        stage.getRoot().getContentPane().getChildren().add(menuBtn);

        stage.getStage().setWidth(768);
        stage.getStage().setHeight(480);
        stage.getStage().centerOnScreen();
        stage.getStage().show();
    }

    public static void launch(String[] args) {
        Theme.setTheme(new ZplTheme());
        Application.launch(Main.class, args);
    }

    public static void openFileLocation(File path) {
        try {
            if (path.isFile()) Runtime.getRuntime().exec("explorer.exe /e,/select," + path);
            else Runtime.getRuntime().exec("explorer.exe /e," + path + "\\");
        } catch (IOException ex) {
            Log.warn(ex.getMessage());
        }
    }
}
