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

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import com.github.wohaopa.zpl.ui.InstanceMaster;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.font.FontUsages;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.toggle.ToggleSwitch;
import io.vproxy.vfx.ui.wrapper.FusionW;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;

public class InstanceScene extends BaseVScene {

    public InstanceScene() {
        super(VSceneRole.MAIN);
        enableAutoContentWidthHeight();

        var infoPane = new GridPane();
        {
            infoPane.setAlignment(Pos.CENTER);
            infoPane.setGridLinesVisible(true);
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
            var toggleSwitch = new ToggleSwitch(5,20);
            toggleSwitch.selectedProperty()
                .bindBidirectional(InstanceMaster.updateProperty());
            toggleSwitch.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (oldValue == null || newValue == null) return;;
                    if (oldValue == !newValue) InstanceMaster.hasChange();
                });


            infoPane.add(toggleSwitch.getNode(), 1, 5);
        }
        {
            var treePane = new TreeView<>();
        }

        getContentPane().getChildren()
            .add(infoPane);
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
