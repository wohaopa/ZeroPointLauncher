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

import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;

public class DoneDialog extends BaseDialog {

    public DoneDialog(String title, StringProperty context) {
        var label = new Label();
        label.textProperty().bind(context);
        label.getStyleClass().add("dialog-title");

        init(title, label);
    }

    public DoneDialog(String title, String context) {
        var label = new Label(context);
        label.getStyleClass().add("dialog-title");

        init(title, label);
    }

    public DoneDialog(String title, Node context) {
        init(title, context);
    }

    protected void init(String title, Node context) {
        var layout = new JFXDialogLayout();
        var heading = new Label(title);
        heading.getStyleClass().add("dialog-title");
        layout.setHeading(heading);

        layout.setBody(context);
        var doneBtn = new JFXButton("完成");
        doneBtn.setTextFill(Color.WHITE);
        doneBtn.setOnAction(event -> this.close());

        layout.setActions(doneBtn);
        layout.getStyleClass().add("dialog-layout");
        this.setContent(layout);
    }
}
