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

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import io.vproxy.vfx.control.scroll.VScrollPane;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.ui.pane.ClickableFusionPane;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.ui.stage.VStageInitParams;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;

public class MessageStage extends VStage {

    private VScrollPane pane;
    private VBox vbox;

    @SuppressWarnings("FieldCanBeLocal")
    private boolean alwaysScrollToEnd = true;

    public MessageStage() {
        super(new VStageInitParams().setMaximizeAndResetButton(false).setIconifyButton(false));
        setTitle("消息");
        getInitialScene().enableAutoContentWidthHeight();
        getRoot().getContentPane().setMinSize(480, 360);
        getStage().setAlwaysOnTop(true);

        this.pane = new VScrollPane();
        this.vbox = new VBox();
        vbox.setSpacing(3);
        FXUtils.observeWidth(pane.getNode(), vbox);
        pane.setContent(vbox);

        vbox.heightProperty().addListener((ob, old, now) -> {
            if (alwaysScrollToEnd) {
                Platform.runLater(() -> pane.setVvalue(1));
            }
        });

        FXUtils.observeWidth(getRoot().getContentPane(), pane.getNode());
        FXUtils.observeHeight(getRoot().getContentPane(), pane.getNode(), -40);

        getInitialScene().getContentPane().getChildren().add(pane.getNode());
    }

    public void accept(String s) {
        if (!getStage().isShowing()) show();
        add(s);
    }

    private void add(String log) {
        FXUtils.runOnFX(() -> add0(log));
    }

    private void add0(String s) {
        var label = new ThemeLabel(s) {

            {
                setFont(new Font(FontManager.FONT_NAME_JetBrainsMono, 12));
            }
        };

        var logPane = new ClickableFusionPane(false);
        logPane.getContentPane().getChildren().add(label);
        vbox.getChildren().add(logPane.getNode());
    }
}
