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

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import com.jfoenix.controls.JFXDialogLayout;

public class WikiLinkDialog extends BaseDialog {

    public WikiLinkDialog() {
        var layout = new JFXDialogLayout();
        var heading = new Label("GTNH中文维基");
        heading.getStyleClass().add("dialog-title");

        layout.setHeading(heading);
        var img = new Image("/assets/img/Gtnh_wiki_logo.png", 100, 80, false, true);
        var text = new Label("https://gtnh.huijiwiki.com/");
        text.getStyleClass().add("dialog-title");
        var sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        layout.setBody(new HBox(text, sp, new ImageView(img)));

        Hyperlink link = new Hyperlink("使用浏览器打开");
        link.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://gtnh.huijiwiki.com/"));
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

        layout.setActions(link);

        layout.getStyleClass().add("dialog-layout");
        this.setContent(layout);
    }
}
