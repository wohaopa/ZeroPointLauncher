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

package com.github.wohaopa.zplui;

import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.Config;
import com.github.wohaopa.zeropointlanuch.core.auth.MicrosoftAuth;
import com.github.wohaopa.zeropointlanuch.core.auth.OfflineAuth;

public class Accounts {

    private static final ObjectProperty<ObservableList<Object>> accounts = new SimpleObjectProperty<>();

    static {
        accounts.setValue(FXCollections.observableArrayList(Config.getConfig().getAuths()));;
    }

    public static ObjectProperty<ObservableList<Object>> accountsProperty() {
        return accounts;
    }

    public static void addOfflineAccount(String name) {
        var auth = new OfflineAuth(name);
        Config.getConfig().addAccount(auth);
        accounts.get().add(auth);
    }

    public static void addMSAccounts(Consumer<JSONObject> callback) {
        var auth = new MicrosoftAuth();
        Config.getConfig().addAccount(auth);
        accounts.get().add(auth);
        auth.loginFist(callback);
    }
}
