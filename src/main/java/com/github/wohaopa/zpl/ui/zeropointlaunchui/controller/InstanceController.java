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
import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zeropointlanuch.core.Sharer;
import com.github.wohaopa.zeropointlanuch.core.ZplDirectory;
import com.github.wohaopa.zpl.ui.zeropointlaunchui.controller.dialog.ModDialog;
import com.leewyatt.rxcontrols.controls.RXTextField;
import com.leewyatt.rxcontrols.event.RXActionEvent;

public class InstanceController extends RootController {

    @FXML
    public RXTextField workDirTextField;
    public ListView<Instance> instanceListView;
    public Label nameLabel;
    public Label versionLabel;
    public Label depInstanceLabel;
    public ListView<String> includeModListView;
    public ListView<String> excludeModListView;
    public ComboBox<String> sharerComboBox;
    public ListView loadedModListView;

    private ComboBox<Instance> comboBox;

    @FXML
    void initialize() {
        workDirTextField.setText(
            ZplDirectory.getWorkDirectory()
                .toString());
        instanceListView.getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (comboBox != null) comboBox.getSelectionModel()
                        .select(newValue);
                    MainController.current.changeInstance(newValue);
                    sharerComboBox.getSelectionModel()
                        .select(MainController.current.sharer.get());
                }
            });

        // 绑定
        nameLabel.textProperty()
            .bind(MainController.current.name);
        versionLabel.textProperty()
            .bind(MainController.current.version);
        sharerComboBox.getItems()
            .setAll(Sharer.getNames());
        sharerComboBox.getSelectionModel()
            .select("Common");

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
                .selectFirst();

        comboBox = ((HomeController) getController("HomeController")).comboBox;

        comboBox.itemsProperty()
            .bind(instanceListView.itemsProperty());
        comboBox.getSelectionModel()
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
        chooser.setInitialDirectory(ZplDirectory.getWorkDirectory());
        File dir = chooser.showDialog(MainController.getWindow());
        if (dir != null) ZplDirectory.init(dir);
        workDirTextField.setText(
            ZplDirectory.getWorkDirectory()
                .toString());
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
        MainController.current.instance.genRuntimeDir();
        MainController.setTip("刷新运行目录完成！");
    }

    public void onClearSymlinkClicked(MouseEvent mouseEvent) {
        MainController.current.instance.delSymlink();
        MainController.setTip("清空完成！");
    }

    public void onRefreshInstanceClicked(MouseEvent mouseEvent) {

        MainController.current.instance.refreshMapper();
        MainController.setTip("映射刷新完成！");
    }
}
