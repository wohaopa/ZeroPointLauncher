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

package com.github.wohaopa.zpl.ui.zplui.util;

import java.io.IOException;
import java.util.Locale;
import javafx.fxml.FXMLLoader;

import com.github.wohaopa.zpl.ui.zplui.ZPLApplication;

public class FXMLs {

    public static <T> T loadFXML(String path) {
        try {
            final FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceUtils.get(Locale.CHINESE));
            loader.setLocation(ZPLApplication.class.getResource("fxml/" + path));
            loader.load();

            return loader.getController();
        } catch (IOException e) {
            Log.error(FXMLs.class, "init AddServerView failed", e);
            throw new IllegalStateException(e);
        }
    }
}
