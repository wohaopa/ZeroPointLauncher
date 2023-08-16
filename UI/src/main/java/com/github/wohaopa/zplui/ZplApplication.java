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

package com.github.wohaopa.zplui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.github.wohaopa.zplui.scene.RootScene;

public class ZplApplication extends Application {

    static RootScene root;

    public static void main(String[] args) {
        launch(ZplApplication.class);
    }

    @Override
    public void start(Stage stage) {
        root = new RootScene();
        var scene = root.getScene();

        stage.setScene(scene);
        stage.setTitle("ZeroPointLauncher - A GTNH Launcher by wohaopa!");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(new Image("assets/img/logo.png"));
        stage.show();
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        bindResize(scene, stage);
    }

    public static final double MIN_WIDTH = 580; // 窗口最小宽度
    public static final double MIN_HEIGHT = 360; // 窗口最小高度
    private static final int RESIZE_WIDTH = 5; // 判定是否为调整窗口状态的范围与边界距离
    private static Pos pos = Pos.None;

    private enum Pos {

        Right,
        Left,
        Top,
        Bottom,
        RightTop,
        RightBottom,
        LeftTop,
        LeftBottom,
        None;

        public boolean bottom() {
            return this == Pos.Bottom || this == Pos.LeftBottom || this == Pos.RightBottom;
        }

        public boolean right() {
            return this == Pos.Right || this == Pos.RightBottom || this == Pos.RightTop;
        }

        public boolean left() {
            return this == Pos.Left || this == Pos.LeftBottom || this == Pos.LeftTop;
        }

        public boolean top() {
            return this == Pos.Top || this == Pos.LeftTop || this == Pos.RightTop;
        }
    }

    public static StackPane getRootPane() {
        return root.getRootPane();
    }

    private static void bindResize(Scene scene, Stage stage) {

        scene.setOnMouseMoved(event -> {
            event.consume();
            double x = event.getSceneX();
            double y = event.getSceneY();
            double width = stage.getWidth();
            double height = stage.getHeight();
            Cursor cursorType;

            if (y >= height - RESIZE_WIDTH) {
                if (x >= width - RESIZE_WIDTH) {
                    pos = Pos.RightBottom;
                    cursorType = Cursor.SE_RESIZE;
                } else {
                    pos = Pos.Bottom;
                    cursorType = Cursor.S_RESIZE;
                }
            } else {
                if (x >= width - RESIZE_WIDTH) {
                    pos = Pos.Right;
                    cursorType = Cursor.E_RESIZE;
                } else {
                    pos = Pos.None;
                    cursorType = Cursor.DEFAULT;
                }
            }
            scene.setCursor(cursorType);
        });
        scene.setOnMouseDragged(event -> {
            // event.consume();
            double x = event.getSceneX();
            double y = event.getSceneY();

            double nextWidth = stage.getWidth();
            double nextHeight = stage.getHeight();

            if (pos.right()) {
                nextWidth = Math.max(x, MIN_WIDTH);
            }
            if (pos.bottom()) {
                nextHeight = Math.max(y, MIN_HEIGHT);
            }
            stage.setWidth(nextWidth);
            stage.setHeight(nextHeight);
        });
    }
}
