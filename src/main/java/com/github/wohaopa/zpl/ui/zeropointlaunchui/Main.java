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

package com.github.wohaopa.zpl.ui.zeropointlaunchui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.github.wohaopa.zeropointlanuch.core.DirTools;
import com.github.wohaopa.zpl.ui.zeropointlaunchui.controller.MainController;

public class Main extends Application {

    public static boolean admin = false;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("ZeroPointLaunch");
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons()
            .add(
                new Image(
                    Main.class.getResource("img/logo.png")
                        .toExternalForm()));
        MainController.setWindow(stage);
        stage.show();
    }

    public static void main(String[] args) {
        Main.initDir();
        Main.checkPermission();
        Application.launch(Main.class, args);
    }

    private static void checkPermission() {
        try {
            File file1 = new File(DirTools.tmpDir, "Permission");
            File file2 = new File(DirTools.tmpDir, "Permission_Link");
            if (file1.createNewFile()) {
                Files.createSymbolicLink(file2.toPath(), file1.toPath());
                file1.delete();
                file2.delete();
                admin = true;
            }

        } catch (IOException ignored) {}
    }

    private static void initDir() {
        String rootDirStr = System.getProperty("zpl.rootDir");

        if (rootDirStr == null) {
            rootDirStr = System.getProperty("user.dir") + "/.GTNH";
        }
        DirTools.init(new File(rootDirStr)); // 目录工具初始化
    }
}
