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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import com.github.wohaopa.zeropointlanuch.core.launch.Launch;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.font.FontUsages;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.wrapper.FusionW;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;

public class SettingScene extends BaseVScene {

    public SettingScene() {
        super(VSceneRole.MAIN);
        enableAutoContentWidthHeight();

        var comboBox = new ComboBox<Launch>();
        {
            comboBox.getItems().addAll(Launch.getLaunches());
            comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) return;
                _launch.change(newValue);
            });
        }
        var launchConfigGrid = new GridPane();
        {
            launchConfigGrid.add(new ThemeLabel("Java路径："), 0, 0);
            launchConfigGrid.add(new ThemeLabel("Jvm额外参数："), 0, 1);
            launchConfigGrid.add(new ThemeLabel("游戏额外参数："), 0, 2);
            launchConfigGrid.add(new ThemeLabel("最大运行内存："), 0, 3);
            launchConfigGrid.add(new ThemeLabel("最小运行内存："), 0, 4);

            launchConfigGrid.add(getTextObj(_launch.javaPath), 1, 0);
            launchConfigGrid.add(getTextObj(_launch.extraJvmArgs), 1, 1);
            launchConfigGrid.add(getTextObj(_launch.extraGameArgs), 1, 2);
            launchConfigGrid.add(getTextObj(_launch.maxMemory), 1, 3);
            launchConfigGrid.add(getTextObj(_launch.minMemory), 1, 4);
        }

        comboBox.getSelectionModel().selectFirst();
        getContentPane().getChildren().addAll(comboBox, launchConfigGrid);
        FXUtils.observeWidthHeightCenter(getContentPane(), launchConfigGrid);
        FXUtils.observeWidthCenter(getContentPane(), comboBox);
    }

    @Override
    public String title() {
        return "设置";
    }

    private static class _launch {

        static StringProperty javaPath = new SimpleStringProperty();
        static StringProperty extraJvmArgs = new SimpleStringProperty();
        static StringProperty extraGameArgs = new SimpleStringProperty();
        static StringProperty maxMemory = new SimpleStringProperty();
        static StringProperty minMemory = new SimpleStringProperty();
        static Launch cur;

        static void change(Launch launch) {
            cur = launch;
            javaPath.setValue(launch.getJavaPath());
            extraJvmArgs.setValue(launch.getExtraJvmArgs());
            extraGameArgs.setValue(launch.getExtraGameArgs());
            maxMemory.setValue(launch.getMaxMemory());
            minMemory.setValue(launch.getMinMemory());
        }

        static void hasChange() {
            cur.setJavaPath(javaPath.getValue());
            cur.setExtraJvmArgs(extraJvmArgs.getValue());
            cur.setExtraGameArgs(extraGameArgs.getValue());
            cur.setMaxMemory(maxMemory.getValue());
            cur.setMinMemory(minMemory.getValue());
        }
    }

    private static Node getTextObj(StringProperty value) {
        var textField = new TextField();
        var text = new FusionW(textField) {

            {
                FontManager.get().setFont(FontUsages.tableCellText, getLabel());
            }
        };

        textField.textProperty().bindBidirectional(value);
        textField.setPadding(new Insets(0, 10, 0, 10));
        textField.focusedProperty().addListener((ob, old, now) -> {
            if (old == null || now == null) return;
            if (old && !now) {
                _launch.hasChange();
            }
        });

        return text;
    }
}
