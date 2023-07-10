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

import java.io.File;
import java.util.*;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zeropointlanuch.core.ZplDirectory;
import com.github.wohaopa.zeropointlanuch.core.tasks.Scheduler;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.ZplExtractTask;
import com.github.wohaopa.zpl.ui.InstanceMaster;
import com.github.wohaopa.zpl.ui.Main;
import com.github.wohaopa.zpl.ui.ModItem;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.font.FontUsages;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;
import io.vproxy.vfx.ui.wrapper.FusionW;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;

public class InstanceScene extends BaseVScene {

    public InstanceScene() {
        super(VSceneRole.MAIN);
        enableAutoContentWidthHeight();

        var infoPane = new GridPane();
        {
            infoPane.setAlignment(Pos.CENTER);
            infoPane.setGridLinesVisible(true);
            ColumnConstraints column1 = new ColumnConstraints(100);
            ColumnConstraints column2 = new ColumnConstraints(50, 150, 300);
            column2.setHgrow(Priority.ALWAYS);

            infoPane.getColumnConstraints()
                .addAll(column1, column2);

            infoPane.add(new ThemeLabel("名："), 0, 0);
            infoPane.add(new ThemeLabel("版本："), 0, 1);
            infoPane.add(new ThemeLabel("继承："), 0, 2);
            infoPane.add(new ThemeLabel("启动器："), 0, 3);
            infoPane.add(new ThemeLabel("分享器："), 0, 4);
            infoPane.add(new ThemeLabel("自动更新："), 0, 5);

            infoPane.add(getTextObj(InstanceMaster.nameProperty()), 1, 0);
            infoPane.add(getTextObj(InstanceMaster.versionProperty()), 1, 1);
            infoPane.add(getTextObj(InstanceMaster.depInstanceProperty()), 1, 2);
            infoPane.add(getTextObj(InstanceMaster.launcherProperty()), 1, 3);
            infoPane.add(getTextObj(InstanceMaster.sharerProperty()), 1, 4);
            var toggleSwitch = new ToggleSwitch(5, 20);
            toggleSwitch.selectedProperty()
                .bindBidirectional(InstanceMaster.updateProperty());
            toggleSwitch.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (oldValue == null || newValue == null) return;;
                    if (oldValue == !newValue) InstanceMaster.hasChange();
                });

            infoPane.add(toggleSwitch.getNode(), 1, 5);
        }
        var fileTreePane = new TreeView<String>();
        {
            fileTreePane.rootProperty()
                .bind(InstanceMaster.rootProperty());
            fileTreePane.setPrefWidth(200);
        }
        var treePane = new TreeView<String>();
        {
            var root = new TreeItem<>("继承图");
            Map<String, String> nameToDep = new HashMap<>();
            Map<String, TreeItem<String>> nameToItem = new HashMap<>();

            nameToItem.put("null", root);

            for (Instance instance : Instance.list()) {
                nameToItem.put(instance.information.name, new TreeItem<>(instance.information.name));
                nameToDep.put(instance.information.name, instance.information.depVersion);
            }
            nameToItem.forEach((s, stringTreeItem) -> {
                if (s.equals("null")) return;
                var dep = nameToItem.get(nameToDep.get(s));
                dep.getChildren()
                    .add(stringTreeItem);
            });

            treePane.setRoot(root);
            treePane.setPrefHeight(100);
        }
        var modsPane = new TreeView<ModItem>();
        {
            modsPane.rootProperty()
                .bind(InstanceMaster.modsProperty());

            modsPane.getSelectionModel()
                .setSelectionMode(SelectionMode.SINGLE);
            modsPane.setCellFactory(new Callback<>() {

                @Override
                public TreeCell<ModItem> call(TreeView<ModItem> param) {
                    return new TreeCell<>() {

                        final HBox hbox;
                        final Label label;

                        final Background normal;
                        final Background disabled;

                        {
                            hbox = new HBox();
                            label = new Label();
                            hbox.getChildren()
                                .add(label);
                            normal = Background.EMPTY;
                            disabled = new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY));
                        }

                        @Override
                        protected void updateItem(ModItem item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!empty) {
                                this.setGraphic(hbox);
                                label.textProperty()
                                    .bind(item.nameProperty());
                                if (item.getDisable()) {
                                    hbox.setBackground(disabled);
                                } else hbox.setBackground(normal);
                            } else {
                                this.setGraphic(null);
                            }
                        }
                    };
                }
            });

            var menu = new ContextMenu() {

                final MenuItem menuItem1 = new MenuItem("打开文件位置");
                final MenuItem menuItem2 = new MenuItem("禁用");
                final MenuItem menuItem3 = new MenuItem("版本管理");
                final MenuItem menuItem4 = new MenuItem("删除");

                ModItem modItem;

                {
                    menuItem1.setOnAction(
                        event -> {
                            Main.openFileLocation(
                                new File(
                                    ZplDirectory.getModsDirectory(),
                                    modItem.fullNameProperty()
                                        .get()));
                        });
                    menuItem2.setOnAction(event -> {
                        if (modItem.getDisable()) {
                            modItem.setDisable(false);
                            InstanceMaster.getCur().information.excludeMods.remove(
                                modItem.fullNameProperty()
                                    .get());
                        } else {
                            modItem.setDisable(true);
                            InstanceMaster.getCur().information.excludeMods.add(
                                modItem.fullNameProperty()
                                    .get());
                        }
                        modsPane.refresh();
                    });
                    menuItem3.setDisable(true); // 还没写
                    menuItem4.setOnAction(new EventHandler<>() {

                        @Override
                        public void handle(ActionEvent event) {
                            if (!InstanceMaster.getCur().information.name.equals(
                                modItem.instanceProperty()
                                    .get())) {
                                modItem.setDisable(true);
                                InstanceMaster.getCur().information.excludeMods.add(
                                    modItem.fullNameProperty()
                                        .get());
                            } else {
                                InstanceMaster.getCur().information.includeMods.remove(
                                    modItem.fullNameProperty()
                                        .get());
                            }
                            InstanceMaster.refresh();
                        }
                    });

                    getItems().addAll(menuItem1, menuItem2, menuItem3, menuItem4);
                }

                @Override
                public void show(Node anchor, double screenX, double screenY) {
                    modItem = modsPane.getSelectionModel()
                        .getSelectedItem()
                        .getValue();
                    if (modItem.fullNameProperty()
                        .get() == null) {
                        menuItem1.setDisable(true);
                        menuItem2.setDisable(true);
                        menuItem3.setDisable(true);
                        menuItem4.setDisable(true);
                    } else {
                        menuItem1.setDisable(false);
                        menuItem2.setDisable(false);
                        menuItem3.setDisable(true); // 还没写
                        menuItem4.setDisable(false);
                    }

                    if (modItem.getDisable()) menuItem2.setText("启用");
                    else menuItem2.setText("禁用");

                    super.show(anchor, screenX, screenY);
                }
            };

            modsPane.setContextMenu(menu);
        }
        var menuInstance = new ContextMenu() {

            final MenuItem menuItem1 = new MenuItem("打开实例目录");
            final MenuItem menuItem2 = new MenuItem("打开运行目录");
            final MenuItem menuItem3 = new MenuItem("保存实例配置");
            final MenuItem menuItem4 = new MenuItem("刷新实例配置");
            final MenuItem menuItem5 = new MenuItem("刷新映射信息");
            final MenuItem menuItem6 = new MenuItem("导出实例");
            final MenuItem menuItem7 = new MenuItem("新建子实例");
            final MenuItem menuItem8 = new MenuItem("检查更新");

            Instance instance;

            {
                menuItem2.disableProperty()
                    .bind(menuItem1.disableProperty());
                menuItem2.disableProperty()
                    .bind(menuItem1.disableProperty());
                menuItem3.disableProperty()
                    .bind(menuItem1.disableProperty());
                menuItem4.disableProperty()
                    .bind(menuItem1.disableProperty());
                menuItem5.disableProperty()
                    .bind(menuItem1.disableProperty());
                menuItem6.disableProperty()
                    .bind(menuItem1.disableProperty());
                menuItem7.disableProperty()
                    .bind(menuItem1.disableProperty());
                menuItem8.disableProperty()
                    .bind(menuItem1.disableProperty());

                menuItem1.setOnAction(event -> Main.openFileLocation(instance.insDir));
                menuItem2.setOnAction(event -> Main.openFileLocation(instance.runDir));
                menuItem3.setOnAction(event -> InstanceMaster.hasChange());

                menuItem6.setOnAction(event -> {
                    var fileChooser = new FileChooser();
                    fileChooser.setInitialDirectory(instance.insDir);
                    fileChooser.setTitle("导出实例");
                    fileChooser.setInitialFileName(instance.information.name+"_extract.zip");
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("ZIP","*.zip"),new FileChooser.ExtensionFilter("ALL","*.*"));
                    var file = fileChooser.showSaveDialog(getOwnerWindow());
                    Scheduler.submitTasks(new ZplExtractTask(file,instance.insDir, null));
                });

                getItems()
                    .addAll(menuItem1, menuItem2, menuItem3, menuItem4, menuItem5, menuItem6, menuItem7, menuItem8);
            }

            @Override
            public void show(Node anchor, Side side, double dx, double dy) {
                instance = InstanceMaster.getCur();
                menuItem1.setDisable(instance == null);
                super.show(anchor, side, dx, dy);
            }

            @Override
            public void show(Node anchor, double screenX, double screenY) {
                instance = Instance.get(
                    treePane.getSelectionModel()
                        .getSelectedItem()
                        .getValue());
                menuItem1.setDisable(instance == null);
                super.show(anchor, screenX, screenY);
            }
        };
        var menuBtn = new MenuButton("选项") {

            @Override
            public void show() {
                menuInstance.show(this, Side.BOTTOM, 0, 0);
            }
        };
        {
            menuBtn.setPrefHeight(40);
            menuBtn.setPrefWidth(80);
        }

        treePane.setContextMenu(menuInstance);

        getContentPane().getChildren()
            .add(infoPane);
        getContentPane().getChildren()
            .add(menuBtn);
        getContentPane().getChildren()
            .add(treePane);
        getContentPane().getChildren()
            .add(fileTreePane);
        getContentPane().getChildren()
            .add(modsPane);

        getContentPane().widthProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (oldValue == null || newValue == null) return;

                double allWidth = newValue.doubleValue() - 20;
                double width = allWidth * 0.33333;
                double x1 = width + 10;
                double x2 = x1 + width + 10;

                infoPane.setPrefWidth(width);
                treePane.setPrefWidth(width);

                fileTreePane.setPrefWidth(width);
                fileTreePane.setLayoutX(x1);

                modsPane.setPrefWidth(width);
                modsPane.setLayoutX(x2);
            });
        getContentPane().heightProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (oldValue == null || newValue == null) return;

                treePane.setPrefHeight(newValue.doubleValue() * 0.5);
                treePane.setLayoutY(newValue.doubleValue() - treePane.getPrefHeight());

                menuBtn.setLayoutY(treePane.getLayoutY() - menuBtn.getPrefHeight() - 5);
            });
        FXUtils.observeHeight(getContentPane(), fileTreePane);
        FXUtils.observeHeight(getContentPane(), modsPane);
    }

    @Override
    public String title() {
        return "实例";
    }

    @Override
    protected void onShown() {
        getContentPane().setDisable(InstanceMaster.getCur() == null);
        super.onShown();
    }

    private static Node getTextObj(StringProperty property) {
        var textField = new TextField();
        var text = new FusionW(textField) {

            {
                FontManager.get()
                    .setFont(FontUsages.tableCellText, getLabel());
            }
        };

        textField.textProperty()
            .bindBidirectional(property);
        textField.setPadding(new Insets(0, 10, 0, 0));
        textField.focusedProperty()
            .addListener((ob, old, now) -> {
                if (old == null || now == null) return;
                if (old && !now) {
                    property.setValue(textField.getText());
                    InstanceMaster.hasChange();
                }
            });

        return text;
    }
}
