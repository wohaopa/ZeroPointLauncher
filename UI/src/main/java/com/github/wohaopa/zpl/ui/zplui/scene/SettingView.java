package com.github.wohaopa.zpl.ui.zplui.scene;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class SettingView extends BaseMyScene {
    public SettingView() {
        super(() -> {
            var img = new Label("设置");
            img.setTextFill(Color.WHITE);
            return img;
        });
    }

    @Override
    public Parent getIcon() {
        var img = new Label("设置");
        img.setTextFill(Color.WHITE);
        return img;
    }
}
