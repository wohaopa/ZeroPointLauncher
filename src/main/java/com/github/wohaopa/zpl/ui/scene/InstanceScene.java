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

import java.util.*;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zpl.ui.InstanceMaster;

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

        getContentPane().getChildren()
            .add(infoPane);
        getContentPane().getChildren()
            .add(treePane);
        getContentPane().getChildren()
            .add(fileTreePane);
        getContentPane().widthProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (oldValue == null || newValue == null) return;
                double width = newValue.doubleValue() * 0.3;
                double x1 = width + newValue.doubleValue() * 0.05;
                double x2 = x1 + newValue.doubleValue() * 0.05;

                infoPane.setPrefWidth(width);

                treePane.setPrefWidth(width);

                fileTreePane.setPrefWidth(width);
                fileTreePane.setLayoutX(x1);
            });
        getContentPane().heightProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (oldValue == null || newValue == null) return;

                treePane.setPrefHeight(newValue.doubleValue() * 0.5);
                treePane.setLayoutY(newValue.doubleValue() - treePane.getPrefHeight());
            });
        FXUtils.observeHeight(getContentPane(), fileTreePane);
    }

    @Override
    public String title() {
        return "实例";
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
