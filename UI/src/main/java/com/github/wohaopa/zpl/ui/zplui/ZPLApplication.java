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

package com.github.wohaopa.zpl.ui.zplui;

import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.github.wohaopa.zpl.ui.zplui.scene.RootScene;

public class ZPLApplication extends Application {

    public static void main(String[] args) {
        launch(ZPLApplication.class);
    }

    @Override
    public void start(Stage stage) throws IOException {

        stage.setScene(new RootScene().getScene());
        stage.setTitle("ZeroPointLauncher - A GTNH Launcher by wohaopa!");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }
}
