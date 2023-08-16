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

import java.util.LinkedList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zeropointlanuch.core.auth.Auth;
import com.github.wohaopa.zeropointlanuch.core.tasks.Scheduler;
import com.github.wohaopa.zeropointlanuch.core.tasks.Task;
import com.github.wohaopa.zplui.Accounts;
import com.github.wohaopa.zplui.Instances;
import com.github.wohaopa.zplui.ZplApplication;
import com.github.wohaopa.zplui.dialog.DoneDialog;
import com.github.wohaopa.zplui.util.FXUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.svg.SVGGlyph;

public class RootScene {

    private final StackPane rootPane;
    private final BorderPane mainPane;
    private final HBox navigationBar;
    private final HBox launcherBar;
    private final VBox menuPane;
    private final List<BaseMyScene> menuItems;
    LinkedList<Parent> stack = new LinkedList<>() {

        public void push(Parent e) {
            remove(e);
            super.push(e);
        }
    };
    private Scene scene;

    private double xOffset, yOffset;

    public RootScene() {
        rootPane = new StackPane(); // 根Pane，用于显示其他的各种Pane
        {
            rootPane.getStylesheets().add("/assets/css/style.css");
            rootPane.setMinWidth(ZplApplication.MIN_WIDTH);
            rootPane.setMinHeight(ZplApplication.MIN_HEIGHT);
        }
        mainPane = new BorderPane(); // 主界面
        navigationBar = new HBox(); // 导航栏
        {
            var title = new Label("ZeroPointLauncher - A GTNH Launcher by wohaopa!");
            var spacer1 = new Region();
            var spacer2 = new Region();

            var back = new JFXButton();
            var backImg = new SVGGlyph(
                0,
                "angle-left",
                "M358.286 640q0-7.429-5.714-13.143l-224.571-224.571 224.571-224.571q5.714-5.714 5.714-13.143t-5.714-13.143l-28.571-28.571q-5.714-5.714-13.143-5.714t-13.143 5.714l-266.286 266.286q-5.714 5.714-5.714 13.143t5.714 13.143l266.286 266.286q5.714 5.714 13.143 5.714t13.143-5.714l28.571-28.571q5.714-5.714 5.714-13.143z",
                Color.WHITE);
            backImg.setSize(14, 14);
            back.setGraphic(backImg);
            back.setOnAction(event -> onBack());
            back.setRipplerFill(Color.BLACK);

            title.setMinHeight(20);
            title.setFont(Font.font(14));

            var close = new JFXButton();
            var closeImg = new SVGGlyph(
                0,
                "CLOSE",
                "M810 274l-238 238 238 238-60 60-238-238-238 238-60-60 238-238-238-238 60-60 238 238 238-238z",
                Color.WHITE);
            closeImg.setSize(14, 14);
            close.setGraphic(closeImg);
            close.setCursor(Cursor.HAND);
            close.setRipplerFill(Color.BLACK);
            close.setOnAction(event -> {
                Platform.exit();
                System.exit(0);
            });

            HBox.setHgrow(spacer1, Priority.ALWAYS);
            HBox.setHgrow(spacer2, Priority.ALWAYS);

            navigationBar.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            navigationBar.setOnMouseDragged(event -> {
                scene.getWindow().setX(event.getScreenX() - xOffset);
                scene.getWindow().setY(event.getScreenY() - yOffset);
            });

            navigationBar.getStyleClass().add("navigation-bar");
            navigationBar.getChildren().addAll(back, spacer1, title, spacer2, close);
        }

        menuPane = new VBox();
        {
            menuItems = new LinkedList<>();

            menuItems.add(new HomeView());
            menuItems.add(new InstanceView());
            menuItems.add(new AccountView());
            menuItems.add(new LauncherView());
            menuItems.add(new ServerView());
            menuItems.add(new ConsoleView());
            menuItems.add(new SettingView());

            menuItems.forEach(baseMyScene -> {
                var btn = new JFXButton();
                btn.setGraphic(baseMyScene.getIcon());
                btn.setOnAction(event -> onAdd(baseMyScene.getPane()));
                if (baseMyScene instanceof LauncherView || baseMyScene instanceof ServerView
                    || baseMyScene instanceof ConsoleView) btn.setDisable(true);
                menuPane.getChildren().add(btn);
            });

            menuPane.widthProperty()
                .addListener((observable, oldValue, newValue) -> menuPane.getChildren().forEach(node -> {
                    if (node instanceof JFXButton btn) {
                        btn.setPrefWidth(newValue.doubleValue());
                    }
                }));
            if (menuItems.size() != 0) onAdd(menuItems.get(0).getPane());

            menuPane.getStyleClass().add("menu-pane");
        }
        launcherBar = new HBox();
        {
            var spacer1 = new Region();
            var spacer2 = new Region();
            var launch = new JFXButton();

            HBox.setHgrow(spacer1, Priority.ALWAYS);
            HBox.setHgrow(spacer2, Priority.ALWAYS);

            var accountCh = new JFXComboBox<>();
            accountCh.getStyleClass().add("zpl-combo-box");
            accountCh.setPrefWidth(150);
            accountCh.setFocusColor(Color.WHITE);
            accountCh.setLabelFloat(false);
            accountCh.setEditable(false);
            accountCh.setUnFocusColor(Color.WHITE);
            accountCh.itemsProperty().bind(Accounts.accountsProperty());
            accountCh.setPromptText("请添加账户");

            accountCh.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue instanceof Auth account) {
                    Accounts.change(account);
                }
            });
            Accounts.addListener(newValue -> accountCh.getSelectionModel().select(newValue));
            accountCh.getSelectionModel().selectFirst();

            var instanceCh = new JFXComboBox<>();
            instanceCh.getStyleClass().add("zpl-combo-box");
            instanceCh.setPrefWidth(150);
            instanceCh.setFocusColor(Color.WHITE);
            instanceCh.setLabelFloat(false);
            instanceCh.setEditable(false);
            instanceCh.itemsProperty().bind(Instances.instancesProperty());
            instanceCh.setPromptText("请添加实例");
            instanceCh.setUnFocusColor(Color.WHITE);

            instanceCh.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue instanceof Instance instance) {
                    Instances.change(instance);
                }
            });
            Instances.addListener(newValue -> instanceCh.getSelectionModel().select(newValue));
            instanceCh.getSelectionModel().selectFirst();

            launch.setButtonType(JFXButton.ButtonType.RAISED);
            launch.setText("启动");

            launch.setTextFill(Color.WHITE);
            launch.setBackground(new Background(new BackgroundFill(null, new CornerRadii(5), null)));
            launch.setBorder(
                new Border(
                    new BorderStroke(
                        Color.WHITE,
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(5),
                        BorderWidths.DEFAULT,
                        new Insets(1))));
            launch.setRipplerFill(Color.BLACK);
            launch.setPrefSize(150, 40);

            launch.setOnAction(event -> {
                var msg = new SimpleStringProperty("正在等待...");
                var dialog = new DoneDialog("启动：" + Instances.getSelect().information.name, msg);
                dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
                dialog.show(ZplApplication.getRootPane());
                var account = Accounts.getSelect();
                var instance = Instances.getSelect();

                Scheduler.submitTasks(new Task<>(s -> FXUtils.runFX(() -> msg.setValue(s))) {

                    @Override
                    public Object call() {
                        instance.launchInstance(account, callback);
                        return null;
                    }
                });
            });
            launcherBar.getChildren().addAll(accountCh, spacer1, instanceCh, spacer2, launch);
            launcherBar.getStyleClass().add("launcher-bar");
        }

        mainPane.setTop(navigationBar);
        mainPane.setBottom(launcherBar);
        mainPane.setLeft(menuPane);
        mainPane.getStyleClass().add("main-pane");

        rootPane.setBackground(
            new Background(
                new BackgroundImage(
                    new Image("/assets/img/bg.png", true),
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true))));

        rootPane.getChildren().addAll(mainPane);
        scene = new Scene(rootPane);
    }

    public void onAdd(Parent parent) {
        mainPane.setCenter(parent);
        stack.push(parent);
    }

    private void onBack() {
        if (stack.size() == 1) return;
        stack.pop();
        mainPane.setCenter(stack.peek());
    }

    public Scene getScene() {
        return scene;
    }

    public StackPane getRootPane() {
        return rootPane;
    }
}
