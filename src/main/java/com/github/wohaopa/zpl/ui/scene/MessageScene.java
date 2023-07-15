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

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import io.vproxy.vfx.control.scroll.VScrollPane;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;

import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;

public class MessageScene extends VScene {

    Callable<?> show;
    Callable<?> hide;

    VBox vBox = new VBox();

    public MessageScene() {
        super(VSceneRole.DRAWER_HORIZONTAL);
        getNode().setPrefHeight(50);
        enableAutoContentHeight();

        getScrollPane().setContent(vBox);

        getNode().setBackground(
            new Background(
                new BackgroundFill(Theme.current().subSceneBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        FXUtils.observeWidthCenter(getNode(), vBox);

    }

    Timer timer = new Timer();

    public void addMessage(String message, int time) {
        var label = new ThemeLabel(message);
        vBox.getChildren().add(label);
        try {
            show.call();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (vBox.getChildren().size() == 1) {
                            try {
                                hide.call();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        vBox.getChildren().remove(label);
                    });
                }
            }, time * 1000L); // 延迟2秒执行

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setShowCallable(Callable<?> show) {
        this.show = show;
    }

    public void setHideCallable(Callable<?> hide) {
        this.hide = hide;
    }
}
