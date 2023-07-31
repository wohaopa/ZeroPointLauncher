package com.github.wohaopa.zpl.ui.zplui.scene;

import com.github.wohaopa.zpl.ui.zplui.util.Lazy;
import javafx.scene.Parent;

import java.util.concurrent.Callable;

public abstract class BaseMyScene {
    protected Lazy<? extends Parent> pane;

    public BaseMyScene(Callable<Parent> callable) {
        pane = new Lazy<>(callable);
    }

    public abstract Parent getIcon();

    public Parent getPane() {
        return pane.getValue();
    }
}
