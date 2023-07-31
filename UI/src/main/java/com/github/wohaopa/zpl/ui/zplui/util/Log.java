package com.github.wohaopa.zpl.ui.zplui.util;

import java.io.IOException;

public class Log {
    public static void error(Class<?> fxmLsClass, String msg, IOException e) {
        System.out.println("错误：" + fxmLsClass.getName() + ": " + msg + " " + e);
    }
}
