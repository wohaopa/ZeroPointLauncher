package com.github.wohaopa.zpl.ui.zplui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Instances {
    private static ObjectProperty<ObservableList<Object>> instances = new SimpleObjectProperty<>();

    static {
        instances.setValue(FXCollections.observableArrayList());
        instances.getValue().addAll("实例测试占位1", "实例测试占位2");
    }

    public static ObjectProperty<ObservableList<Object>> instancesProperty() {
        return instances;
    }
}
