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
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

import com.github.wohaopa.zeropointlanuch.api.Core;
import com.github.wohaopa.zeropointlanuch.core.DirTools;
import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zeropointlanuch.core.Sharer;
import com.github.wohaopa.zpl.ui.zeropointlaunchui.controller.dialog.ModDialog;
import com.leewyatt.rxcontrols.controls.RXTextField;
import com.leewyatt.rxcontrols.event.RXActionEvent;

public class InstanceController {

    @FXML
    public RXTextField workDirTextField;
    public ListView<Instance> instanceListView;
    public Label nameLabel;
    public Label versionLabel;
    public Label sharerLabel;
    public Label depInstanceLabel;
    public ListView<String> includeModListView;
    public ListView<String> excludeModListView;

    @FXML
    void initialize() {
        workDirTextField.setText(DirTools.workDir.toString());
        instanceListView.getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    MainController.current.changeInstance(newValue);
                }
            });
        // includeModListView.setCellFactory(param -> new _ListCell());

        // 绑定
        nameLabel.textProperty()
            .bind(MainController.current.name);
        versionLabel.textProperty()
            .bind(MainController.current.version);
        sharerLabel.textProperty()
            .bind(MainController.current.sharer);
        depInstanceLabel.textProperty()
            .bind(MainController.current.depInstance);
        includeModListView.itemsProperty()
            .bind(MainController.current.includeMod);
        excludeModListView.itemsProperty()
            .bind(MainController.current.excludeMod);

        onRefreshClicked(null);
        if (instanceListView.getItems()
            .size() != 0)
            instanceListView.getSelectionModel()
                .select(0);
    }

    @FXML
    public void onRefreshClicked(MouseEvent mouseEvent) {
        Core.refresh();
        instanceListView.getItems()
            .setAll(Core.listInst());
    }

    public void onChooseWorkDir(RXActionEvent rxActionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(DirTools.workDir);
        File dir = chooser.showDialog(MainController.getWindow());
        if (dir != null) DirTools.init(dir);
        workDirTextField.setText(DirTools.workDir.toString());
    }

    public void onInstanceDirClicked(MouseEvent mouseEvent) {
        // Clicked
        Util.openFileLocation(MainController.current.instance.insDir);
    }

    public void onRunDirClicked(MouseEvent mouseEvent) {
        Util.openFileLocation(MainController.current.instance.runDir);
    }

    public void onImgDirClicked(MouseEvent mouseEvent) {
        Util.openFileLocation(MainController.current.instance.imageDir);
    }

    public void onSharerDirClicked(MouseEvent mouseEvent) {
        Util.openFileLocation(Sharer.get(MainController.current.instance.information.sharer).rootDir);
    }

    public void onIncludeModListViewClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() > 1) {
            List<String> list = includeModListView.getSelectionModel()
                .getSelectedItems();
            ModDialog alert = new ModDialog(list.get(0));
            alert.showAndWait();
        }
    }

    public void onRefreshRunDirClicked(MouseEvent mouseEvent) {
        MainController.current.instance.genRuntimeDir(null);
    }

    public void onClearSymlinkClicked(MouseEvent mouseEvent) {
        MainController.current.instance.delSymlink();
    }
}
