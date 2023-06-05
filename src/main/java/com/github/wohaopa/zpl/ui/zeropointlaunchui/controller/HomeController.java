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

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zeropointlanuch.core.Launch;
import com.github.wohaopa.zeropointlanuch.core.auth.Auth;
import com.github.wohaopa.zeropointlanuch.core.auth.OfflineAuth;
import com.github.wohaopa.zpl.ui.zeropointlaunchui.Main;

public class HomeController extends RootController {

    public FlowPane wringPane;
    public ComboBox<Instance> comboBox;

    @FXML
    void initialize() {
        wringPane.setVisible(!Main.admin);
    }

    public void onLaunchBtnClicked(MouseEvent mouseEvent) {

        MainController.setTip("当前启动器还没有启动功能");
        Button btn = (Button) mouseEvent.getSource();
        btn.setDisable(true);
        btn.setText("assets检查");

        new Thread(() -> {
            Auth auth = new OfflineAuth("wohaopa");
            Launch.getLauncher("ZPL-Java8")
                .setJavaPath("C:\\Program Files\\Java\\jdk1.8.0_361\\bin\\java.exe")
                .launch(auth, MainController.current.instance.runDir);

            Platform.runLater(() -> {
                btn.setText("library检查");
                btn.setDisable(false);
            });
        }).start();
    }
}
