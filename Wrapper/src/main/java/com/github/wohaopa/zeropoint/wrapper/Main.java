package com.github.wohaopa.zeropoint.wrapper;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static String zpLaunch = "http://127.0.0.1/ZeroPointLaunch/Library/";
    private static File rootDir;

    public static List<String> lib = new ArrayList<>();

    static {
        // 懒得找了，写死得了
        lib.add("hutool-all-5.8.17.jar");
        lib.add("log4j-core-2.20.0.jar");
        lib.add("log4j-api-2.20.0.jar");
        lib.add("commons-compress-1.21.jar");
        lib.add("xz-1.9.jar");
        lib.add("junit-platform-engine-1.9.2.jar");
        lib.add("junit-platform-commons-1.9.2.jar");
        lib.add("junit-vintage-engine-5.9.2.jar");
        lib.add("junit-4.13.2.jar");
        lib.add("opentest4j-1.2.0.jar");
        lib.add("hamcrest-core-1.3.jar");
        lib.add("ZeroPointLaunch-Core.jar");

        String userDir = System.getProperty("user.dit");
        rootDir = new File(userDir, "lib");
    }

    public static void main(String[] args) {

        lib.forEach(s -> {
            File libFile = new File(rootDir, s);
            if (!libFile.exists()) {
                System.out.println("missing...: " + s);
                try {
                    downloadFile(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        try {
            Class<?> clazz = Class.forName("com.github.wohaopa.zeropointlanuch.main.Main");
            Method method = clazz.getMethod("main", String[].class);
            method.setAccessible(true);
            method.invoke(null, (Object) args);
        } catch (ClassNotFoundException e) {
            System.out.println("无法加载核心类：com.github.wohaopa.zeropointlanuch.main.Main，可能缺少ZeroPointLaunch-Core.jar");
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            System.out.println("无法加载main方法，请重新下载！");
            throw new RuntimeException(e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            System.out.println("main方法执行错误");
            throw new RuntimeException(e);
        }
    }


    private static void downloadFile(String s) throws IOException {


        URL url = new URL(zpLaunch + s);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        rootDir.mkdirs();

        File file = new File(rootDir + File.separator + s);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);

        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }

        System.out.println("info:" + url + " download success");

    }


    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


}
