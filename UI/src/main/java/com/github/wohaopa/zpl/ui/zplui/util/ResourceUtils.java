package com.github.wohaopa.zpl.ui.zplui.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceUtils {
    public static ResourceBundle get(Locale locale) {
        return ResourceBundle.getBundle("assets.i18n.lang", locale);
    }

}
