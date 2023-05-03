package com.github.wohaopa.zeropoint.wrapper;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class Main {
    public static String versionUrl = "http://127.0.0.1/version";
    public static String zpLaunch = "http://127.0.0.1/ZeroPointLaunch/ZeroPointLaunch-Core.jar";

    public static void main(String[] args) {


    }

    private static boolean camp(String version1, String version2) {
        String[] a = version1.split("\\.");
        String[] b = version2.split("\\.");
        for (int i = 0; i < 3; i++)
            if (Integer.getInteger(a[i]) < Integer.getInteger(b[i]))
                return true;
        return false;
    }
}