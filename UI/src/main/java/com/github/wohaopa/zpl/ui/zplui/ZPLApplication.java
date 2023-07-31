package com.github.wohaopa.zpl.ui.zplui;

import com.github.wohaopa.zpl.ui.zplui.scene.RootScene;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

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