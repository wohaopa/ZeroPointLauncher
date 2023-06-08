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

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import com.github.wohaopa.zeropointlanuch.core.Account;
import com.github.wohaopa.zeropointlanuch.core.auth.Auth;
import com.github.wohaopa.zeropointlanuch.core.auth.OfflineAuth;

public class AccountController extends RootController {

    public ListView<Auth> accountListView;
    public TextField nameTextField;

    @FXML
    void initialize() {
        accountListView.getItems()
            .setAll(Account.getAuths());
        accountListView.getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Account.select(newValue);
                    MainController.current.changeAccount(newValue);
                }
            });

        accountListView.getSelectionModel()
            .selectFirst();
    }

    public void onAddOutline(MouseEvent mouseEvent) {
        String name = nameTextField.getText();
        if (name != null && !name.isEmpty()) {
            OfflineAuth auth = new OfflineAuth(name);
            accountListView.getItems()
                .add(auth);
            Account.add(auth);
        }
    }
}
