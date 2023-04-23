package com.github.wohaopa.zeropointwrapper.utils;

import java.io.File;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.extra.compress.CompressUtil;
import cn.hutool.extra.compress.extractor.Extractor;

/** 解压zip文件 */
public final class ZipUtil {

    public static void unCompress(File zip, File savePath) {

        Extractor extractor = CompressUtil.createExtractor(CharsetUtil.CHARSET_UTF_8, zip);
        extractor.extract(savePath);
    }
}
