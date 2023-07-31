package com.github.wohaopa.zpl.ui.zplui.util;

import com.github.wohaopa.zpl.ui.zplui.ZPLApplication;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Locale;

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
