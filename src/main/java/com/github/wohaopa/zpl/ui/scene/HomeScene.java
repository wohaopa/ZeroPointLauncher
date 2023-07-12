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

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;

public class HomeScene extends BaseVScene {

    public HomeScene() {
        super(VSceneRole.MAIN);
        enableAutoContentWidthHeight();
        var title = new ThemeLabel("ZPL-GTNH启动器");
        {
            FontManager.get().setFont(title, settings -> settings.setSize(40));
        }
        var info = new ThemeLabel("反馈群：222625575（点击复制）");
        {
            FontManager.get().setFont(info, settings -> settings.setSize(18));
            info.setPrefHeight(20);
            info.setOnMouseClicked(event -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.put(DataFormat.PLAIN_TEXT, "222625575");
                clipboard.setContent(clipboardContent);
            });
        }
        var version = new ThemeLabel("ZPL版本：0.3");
        {
            FontManager.get().setFont(info, settings -> settings.setSize(18));
            info.setOnMouseClicked(event -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.put(DataFormat.PLAIN_TEXT, "222625575");
                clipboard.setContent(clipboardContent);
            });
        }

        getContentPane().getChildren().addAll(title, info, version);
        getContentPane().heightProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null || newValue == null) return;
            info.setLayoutY(newValue.doubleValue() * 0.5);
            version.setLayoutY(info.getLayoutY() + info.getPrefHeight());
        });
        FXUtils.observeWidthHeightCenter(getContentPane(), title);

        setBackgroundImage(ImageManager.get().load("images/bg.jpg"));
    }

    @Override
    public String title() {
        return "主页";
    }
}
