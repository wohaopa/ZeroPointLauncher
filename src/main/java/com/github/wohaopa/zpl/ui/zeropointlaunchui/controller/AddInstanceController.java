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

import java.io.File;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import com.github.wohaopa.zeropointlanuch.core.*;
import com.leewyatt.rxcontrols.controls.RXTextField;
import com.leewyatt.rxcontrols.event.RXActionEvent;

public class AddInstanceController extends RootController {

    public RXTextField zipPath;
    public TextField instanceName;
    public TextField instanceVersion;
    public TextField instanceDepName;
    public ComboBox<String> sharerName;
    public ComboBox<String> launchName;

    private File zip;

    @FXML
    void initialize() {

        launchName.getItems()
            .setAll(Launch.getLaunches());
        sharerName.getItems()
            .setAll(Sharer.getNames());
        launchName.getSelectionModel()
            .select("ZPL-Java8");
        sharerName.getSelectionModel()
            .select("Common");
    }

    public void onOpenZip(RXActionEvent rxActionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(ZplDirectory.getZipDirectory());
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("压缩包", "zip", "rar", "7z"));
        File file = fileChooser.showOpenDialog(
            zipPath.getScene()
                .getWindow());
        if (file == null) return;
        zip = file;
        zipPath.setText(zip.getPath());

        String name = zip.getName();

        instanceName.setText(name.substring(0, name.lastIndexOf(".")));
        instanceVersion.setText(instanceName.getText());
        instanceDepName.setText("null");

        if (name.contains("Java17")) launchName.getSelectionModel()
            .select("ZPL-Java17");
        else launchName.getSelectionModel()
            .select("ZPL-Java8");
    }

    public void onInstanceClicked(MouseEvent mouseEvent) {
        boolean flag = true;
        File instanceDir = new File(ZplDirectory.getInstancesDirectory(), instanceName.getText());
        if (Instance.containsKey(instanceName.getText()) || instanceDir.exists()) {
            instanceName.getStyleClass()
                .add("waring-input");
            flag = false;
        }

        if (flag) {
            Button button = (Button) mouseEvent.getSource();
            button.setDisable(true);
            button.setText("正在安装");

            new Thread(() -> {
                instanceName.getStyleClass()
                    .remove("waring-input");

                Instance.Information information = new Instance.Information();
                information.name = instanceName.getText();
                information.version = instanceVersion.getText();
                information.depVersion = instanceDepName.getText();
                information.sharer = sharerName.getValue();
                information.launcher = launchName.getValue();

                InstanceInstaller.installForZip(zip, instanceDir, information);

                Platform.runLater(() -> {
                    button.setDisable(false);
                    button.setText("安装实例");
                });
            }).start();
        }
    }
}
