package com.github.wohaopa.zeropoint.wrapper;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static String zpLaunch;
    private static final File rootDir;

    private static final List<String> lib = new ArrayList<>();

    private static final Properties config = new Properties();

    static {
        // 懒得找了，写死得了
        lib.add("lib/hutool-all-5.8.17.jar");
        lib.add("lib/log4j-core-2.20.0.jar");
        lib.add("lib/log4j-api-2.20.0.jar");
        lib.add("lib/commons-compress-1.21.jar");
        lib.add("lib/xz-1.9.jar");
        lib.add("lib/junit-platform-engine-1.9.2.jar");
        lib.add("lib/junit-platform-commons-1.9.2.jar");
        lib.add("lib/junit-vintage-engine-5.9.2.jar");
        lib.add("lib/junit-4.13.2.jar");
        lib.add("lib/opentest4j-1.2.0.jar");
        lib.add("lib/hamcrest-core-1.3.jar");
        lib.add("lib/ZeroPointLaunch-Core.jar");

        String userDir = System.getProperty("user.dir");
        rootDir = new File(userDir);
        File configFile = new File(rootDir, "/config.properties");
        if (!configFile.exists()) {
            config.setProperty("download-url", "http://127.0.0.1/ZeroPointLaunch/");
            config.setProperty("check-libraries", "true");
            config.setProperty("check-update", "true");
            try {
                config.store(Files.newOutputStream(configFile.toPath()), null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                config.load(Files.newInputStream(configFile.toPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {

        zpLaunch = config.getProperty("download-url", "http://127.0.0.1/ZeroPointLaunch/");

        if (config.getProperty("check-update", "false").equals("true")) {
            File newCore = new File(rootDir, "lib/ZeroPointLaunch-Core-new.jar");
            if (newCore.exists()) {
                File curCore = new File(rootDir, "lib/ZeroPointLaunch-Core.jar");
                File oldCore = new File(rootDir, "lib/ZeroPointLaunch-Core-old.jar");

                if (oldCore.exists()) oldCore.delete();
                if (curCore.exists()) curCore.renameTo(oldCore);
                newCore.renameTo(curCore);
            }
        }

        if (config.getProperty("check-libraries", "false").equals("true")) {
            lib.forEach(s -> {
                File libFile = new File(rootDir, s);
                if (!libFile.exists()) {
                    System.out.println("missing...: " + s);
                    try {
                        downloadFile(s, libFile.getName());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }


        try {
            Class<?> clazz = Class.forName("com.github.wohaopa.zeropointlanuch.main.Main");
            Method method = clazz.getMethod("main", String[].class);
            method.setAccessible(true);
            method.invoke(null, (Object) args);
        } catch (ClassNotFoundException e) {
            System.out.println("无法加载核心类：com.github.wohaopa.zeropointlanuch.main.Main，请重新启动！或者可能缺少ZeroPointLaunch-Core.jar");
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            System.out.println("无法加载main方法，请重新下载！");
            throw new RuntimeException(e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            System.out.println("main方法执行错误");
            throw new RuntimeException(e);
        }
    }


    private static void downloadFile(String urlPath, String fileName) throws IOException {
        if (fileName.equals("ZeroPointLaunch-Core.jar")) {
            File versionJson = new File(rootDir, "lib\\version.json");
            downloadFile("version.json", "version.json");
            try {
                FileReader fr = new FileReader(versionJson);
                char[] buff = new char[(int) versionJson.length()];
                fr.read(buff);
                String context = new String(buff);
                Pattern ptn = Pattern.compile("\"download-url\": \"(.+)\",");
                Matcher matcher = ptn.matcher(context);
                if (matcher.find()) {
                    urlPath = matcher.group(1);
                }
                fr.close();

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            versionJson.delete();
        }


        URL url = new URL(zpLaunch + urlPath);
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

        File file = new File(rootDir, "lib\\" + fileName);
        file.getParentFile().mkdirs();
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
