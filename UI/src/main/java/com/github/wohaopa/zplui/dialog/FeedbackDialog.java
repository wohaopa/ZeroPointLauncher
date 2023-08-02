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

import java.util.Map;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;

public class FeedbackDialog extends BaseDialog {

    public FeedbackDialog() {
        var layout = new JFXDialogLayout();
        var heading = new Label("ZeroPointLauncher 反馈群");
        heading.getStyleClass().add("dialog-title");
        layout.setHeading(heading);

        var img = new Image("/assets/img/bg1.jpg", 296 * 0.3, 256 * 0.3, false, true);
        var text = new Label("QQ群：222625575 \n您的催更将会是我更新的动力，快来催更吧！");
        text.getStyleClass().add("dialog-title");
        var sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        layout.setBody(new HBox(text, sp, new ImageView(img)));
        var acceptBtn = new JFXButton("复制");
        acceptBtn.setTextFill(Color.WHITE);
        acceptBtn.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(Map.of(DataFormat.PLAIN_TEXT, "222625575"));
            this.close();
        });
        layout.setActions(acceptBtn);
        layout.getStyleClass().add("dialog-layout");
        this.setContent(layout);
    }
}
