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

package com.github.wohaopa.zpl.ui.zeropointlaunchui.controller;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

import com.github.wohaopa.zpl.ui.zeropointlaunchui.Main;
import com.leewyatt.rxcontrols.animation.carousel.AnimNone;
import com.leewyatt.rxcontrols.controls.RXCarousel;
import com.leewyatt.rxcontrols.pane.RXCarouselPane;

public class MainController {

    @FXML
    public AnchorPane topBar;
    @FXML
    public ToggleGroup navGroup;
    @FXML
    public RXCarousel mainCarousel;

    private double offsetX, offsetY;
    private Window window;

    @FXML
    void initialize() throws IOException {
        RXCarouselPane homePane = new RXCarouselPane(
            new FXMLLoader(Main.class.getResource("fxml/home-view.fxml")).load());
        RXCarouselPane instancePane = new RXCarouselPane(
            new FXMLLoader(Main.class.getResource("fxml/instance-view.fxml")).load());
        RXCarouselPane accountPane = new RXCarouselPane(
            new FXMLLoader(Main.class.getResource("fxml/account-view.fxml")).load());
        RXCarouselPane otherPane = new RXCarouselPane(
            new FXMLLoader(Main.class.getResource("fxml/other-view.fxml")).load());
        mainCarousel.setPaneList(homePane, instancePane, accountPane, otherPane);
        mainCarousel.setCarouselAnimation(new AnimNone());
        navGroup.selectedToggleProperty()
            .addListener((ob, ov, nv) -> {
                int index = navGroup.getToggles()
                    .indexOf(nv);
                mainCarousel.setSelectedIndex(index);
            });
    }

    @FXML
    public void topBarDraggedAction(MouseEvent mouseEvent) {
        if (window == null) window = topBar.getScene()
            .getWindow();

        window.setX(mouseEvent.getScreenX() - offsetX);
        window.setY(mouseEvent.getScreenY() - offsetY);
    }

    @FXML
    public void topBarPressedAction(MouseEvent mouseEvent) {
        offsetX = mouseEvent.getSceneX();
        offsetY = mouseEvent.getSceneY();
    }

    @FXML
    public void exitAction(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }
}
