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

package com.github.wohaopa.zpl.ui.zplui.scene;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.svg.SVGGlyph;

public class HomeView extends BaseMyScene {

    public HomeView() {
        super(() -> {
            var rootPane = new StackPane();

            var title = new Label("欢迎使用 ZeroPointLauncher!");
            {
                title.setFont(new Font(24));
                title.setTextFill(Color.WHITE);
            }
            var anchorPane = new AnchorPane();
            {
                var nodesList = new JFXNodesList();
                {
                    var more = new JFXButton();
                    {
                        more.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        var img = new SVGGlyph(
                            "M448 448V192h128v256h256v128H576v256H448V576H192V448h256z m64 576A512 512 0 1 1 512 0a512 512 0 0 1 0 1024z m3.008-92.992a416 416 0 1 0 0-832 416 416 0 0 0 0 832z",
                            Color.WHITE);
                        img.setSize(18, 18);
                        more.setGraphic(img);
                        more.setBackground(new Background(new BackgroundFill(null, new CornerRadii(20), null)));
                        more.setRipplerFill(Color.gray(0.9));
                    }

                    // 反馈群
                    var qq = new JFXButton("反馈群");
                    {
                        qq.setContentDisplay(ContentDisplay.TEXT_ONLY);
                        qq.setBackground(new Background(new BackgroundFill(null, new CornerRadii(20), null)));
                        qq.setTextFill(Color.WHITE);

                        var dialog = new JFXDialog();
                        {
                            var layout = new JFXDialogLayout();
                            var heading = new Label("ZeroPointLauncher 反馈群");
                            heading.setTextFill(Color.WHITE);
                            layout.setHeading(heading);
                            var img = new Image("/assets/img/bg1.jpg", 100, 100, false, true);
                            var text = new Label("QQ群：222625575 \n您的催更将会是我更新的动力，快来催更吧！");
                            text.setTextFill(Color.WHITE);
                            var sp = new Region();
                            HBox.setHgrow(sp, Priority.ALWAYS);

                            layout.setBody(new HBox(text, sp, new ImageView(img)));
                            var acceptBtn = new JFXButton("复制");
                            acceptBtn.setTextFill(Color.WHITE);
                            acceptBtn.setOnAction(event -> {
                                Clipboard clipboard = Clipboard.getSystemClipboard();
                                clipboard.setContent(Map.of(DataFormat.PLAIN_TEXT, "222625575"));
                                dialog.close();
                            });
                            layout.setActions(acceptBtn);
                            layout.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
                            dialog.setContent(layout);
                        }

                        qq.setOnAction(event -> {
                            dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
                            dialog.show(rootPane);
                        });
                    }

                    // 灰机百科
                    var web_wiki = new JFXButton("GTNH百科");
                    {
                        web_wiki.setContentDisplay(ContentDisplay.TEXT_ONLY);
                        web_wiki.setBackground(new Background(new BackgroundFill(null, new CornerRadii(20), null)));
                        web_wiki.setTextFill(Color.WHITE);

                        var dialog = new JFXDialog();
                        {
                            var layout = new JFXDialogLayout();
                            var heading = new Label("GTNH中文维基");
                            heading.setTextFill(Color.WHITE);
                            layout.setHeading(heading);
                            var img = new Image("/assets/img/Gtnh_wiki_logo.png", 100, 80, false, true);
                            var text = new Label("https://gtnh.huijiwiki.com/");
                            text.setTextFill(Color.WHITE);
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
                            layout.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
                            dialog.setContent(layout);
                        }

                        web_wiki.setOnAction(event -> {
                            dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
                            dialog.show(rootPane);
                        });
                    }

                    nodesList.setAlignment(Pos.CENTER);
                    nodesList.setRotate(180);

                    nodesList.getChildren().addAll(more, qq, web_wiki);

                    AnchorPane.setBottomAnchor(nodesList, 20.0);
                    AnchorPane.setRightAnchor(nodesList, 20.0);
                }
                var info = new Label("Core:0.4.0 UI v3:3.0.0");
                {
                    info.setTextFill(Color.WHITE);
                    info.setFont(Font.font(12));

                    AnchorPane.setBottomAnchor(info, 20.0);

                    AnchorPane.setLeftAnchor(info, 20.0);
                }

                anchorPane.getChildren().addAll(nodesList, info);
            }

            rootPane.getChildren().addAll(title, anchorPane);
            return rootPane;
        });
    }

    @Override
    public Parent getIcon() {
        var img = new Label("主页");
        img.setTextFill(Color.WHITE);
        return img;
    }
}
