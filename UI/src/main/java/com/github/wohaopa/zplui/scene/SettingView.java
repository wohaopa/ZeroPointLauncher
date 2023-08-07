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

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import com.github.wohaopa.zeropointlanuch.core.Config;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;

public class SettingView extends BaseMyScene {

    public SettingView() {
        super(() -> {
            var root = new VBox();
            root.getStyleClass().add("vbox");

            var mirror = new JFXTextField();
            mirror.getStyleClass().add("zpl-text-field");
            mirror.setPromptText("镜像地址");
            mirror.setLabelFloat(true);
            var regex = new RegexValidator("请输入正确的URL");
            regex.setRegexPattern(
                "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?");
            mirror.setValidators(regex);
            mirror.setText(Config.getConfig().getLibraries_url());
            mirror.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Config.getConfig().setLibraries_url(newValue);
                }
            });

            root.getChildren().addAll(mirror);
            return root;
        });
    }

    @Override
    public Parent getIcon() {
        var img = new Label("设置");
        img.setTextFill(Color.WHITE);
        return img;
    }
}
