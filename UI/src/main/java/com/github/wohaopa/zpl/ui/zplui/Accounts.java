package com.github.wohaopa.zpl.ui.zplui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Accounts {
    private static ObjectProperty<ObservableList<Object>> accounts = new SimpleObjectProperty<>();

    static {
        accounts.setValue(FXCollections.observableArrayList());
        accounts.getValue().addAll("账户测试占位1", "账户测试占位2");
    }

    public static ObjectProperty<ObservableList<Object>> accountsProperty() {
        return accounts;
    }
}
