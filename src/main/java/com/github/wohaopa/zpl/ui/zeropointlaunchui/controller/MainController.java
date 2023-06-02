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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zpl.ui.zeropointlaunchui.Main;
import com.leewyatt.rxcontrols.animation.carousel.AnimNone;
import com.leewyatt.rxcontrols.controls.RXCarousel;
import com.leewyatt.rxcontrols.pane.RXCarouselPane;

public class MainController extends RootController {

    @FXML
    public AnchorPane topBar;
    @FXML
    public ToggleGroup navGroup;
    @FXML
    public RXCarousel mainCarousel;

    private double offsetX, offsetY;
    private static Window window;
    public static _Current_ current = new _Current_();

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

        getWindow().setX(mouseEvent.getScreenX() - offsetX);
        getWindow().setY(mouseEvent.getScreenY() - offsetY);
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

    protected static Window getWindow() {
        return window;
    }

    public static void setWindow(Window window) {
        MainController.window = window;
    }
}

class _Current_ {

    ObjectProperty<ObservableList<String>> includeMod = new SimpleObjectProperty<>();
    ObjectProperty<ObservableList<String>> excludeMod = new SimpleObjectProperty<>();
    ObjectProperty<ObservableList<String>> loadedMod = new SimpleObjectProperty<>();
    ObjectProperty<ObservableList<String>> includeFile = new SimpleObjectProperty<>();
    ObjectProperty<ObservableList<String>> excludeFile = new SimpleObjectProperty<>();
    Instance instance = null;
    StringProperty depInstance = new SimpleStringProperty();
    StringProperty name = new SimpleStringProperty();
    StringProperty version = new SimpleStringProperty();
    StringProperty sharer = new SimpleStringProperty();

    void changeInstance(Instance instance) {
        this.instance = instance;
        name.setValue(instance.information.name);
        version.setValue(instance.information.version);
        sharer.setValue(instance.information.sharer);
        depInstance.setValue(instance.information.depVersion);
        includeMod.setValue(FXCollections.observableList(instance.information.includeMods));
        excludeMod.setValue(FXCollections.observableList(instance.information.excludeMods));
        // includeMod.setValue(FXCollections.observableList(instance.information.includeMods));
    }
}
