package com.fancy_software.crawling.utils;


import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

    public static String getHash(String str) {
//        System.out.println(str);
        return DigestUtils.md5Hex(str);

    }

}
