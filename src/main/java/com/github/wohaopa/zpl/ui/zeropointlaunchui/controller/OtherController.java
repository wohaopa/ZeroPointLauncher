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
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import com.github.wohaopa.zeropointlanuch.core.Config;
import com.github.wohaopa.zeropointlanuch.core.JavaVersion;

public class OtherController extends RootController {

    public ListView<JavaVersion> java8ListView;
    public ListView<JavaVersion> java17ListView;

    private FileChooser javaFileChooser;

    @FXML
    void initialize() {

        javaFileChooser = new FileChooser();
        javaFileChooser.setTitle("选择Java8或Java17以上版本");
        javaFileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        javaFileChooser.getExtensionFilters()
            .addAll(new FileChooser.ExtensionFilter("所有文件", "*.*"), new FileChooser.ExtensionFilter("EXE", "*.exe"));

        JavaVersion.getJavas()
            .forEach(javaVersion -> {
                if (javaVersion.version == JavaVersion.Java.JAVA8) java8ListView.getItems()
                    .add(javaVersion);
                else if (javaVersion.version == JavaVersion.Java.JAVA17) java17ListView.getItems()
                    .add(javaVersion);
            });

        java8ListView.getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Config.getConfig()
                        .setJava8Path(newValue.javaExe.toString());
                }
            });
        java17ListView.getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Config.getConfig()
                        .setJava17Path(newValue.javaExe.toString());
                }
            });

        java8ListView.getSelectionModel()
            .select(
                JavaVersion.getJava(
                    new File(
                        Config.getConfig()
                            .getJava8Path())));

        java17ListView.getSelectionModel()
            .select(
                JavaVersion.getJava(
                    new File(
                        Config.getConfig()
                            .getJava17Path())));
    }

    public void onChooseJava(MouseEvent mouseEvent) {
        File file = javaFileChooser.showOpenDialog(MainController.getWindow());
        if (file != null) {
            JavaVersion java = new JavaVersion(file);

            if (java.version == JavaVersion.Java.JAVA8) {
                java8ListView.getItems()
                    .add(java);
                java8ListView.getSelectionModel()
                    .select(java);
            } else if (java.version == JavaVersion.Java.JAVA17) {
                java17ListView.getItems()
                    .add(java);
                java17ListView.getSelectionModel()
                    .select(java);
            } else MainController.setTip("请选择Java8或Java17以上版本");
        }
    }
}
