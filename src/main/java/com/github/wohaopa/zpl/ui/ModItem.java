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

package com.github.wohaopa.zpl.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ModItem {

    private StringProperty fullName = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();
    private BooleanProperty disable = new SimpleBooleanProperty(false);
    private StringProperty instance = new SimpleStringProperty();

    public ModItem(String name) {
        this.name.setValue(name);
    }

    public ModItem(String name, String fullName, String instance) {
        this.fullName.set(fullName);
        this.name.setValue(name);
        this.instance.setValue(instance);
    }

    public void setDisable(boolean disable) {
        this.disable.setValue(disable);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public BooleanProperty disableProperty() {
        return disable;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty instanceProperty() {
        return instance;
    }

    @Override
    public String toString() {
        return name.getValue();
    }

    public int compareTo(ModItem value) {
        return this.name.getValue().compareTo(value.name.getValue());
    }

    public boolean getDisable() {
        return disable.get();
    }
}
