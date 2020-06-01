package com.mean.androidprivacy.utils;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: Md5CalcUtil
 * @Description: MD5计算工具
 * @Author: MeanFan
 * @Version: 1.0
 */
public class Md5CalcUtil {
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    /**
    * @Author: MeanFan
    * @Description: 文件类取MD5
    * @Param: [context, filePath]
    * @return: java.lang.String
    **/
    public static String calcMD5(Context context, String filePath){
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(filePath);
            return calcMD5(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
    * @Author: MeanFan
    * @Description: 输入流取MD5
    * @Param: [stream]
    * @return: java.lang.String
    **/
    public static String calcMD5(InputStream stream) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int len;
            while ((len = stream.read(buf)) > 0) {
                digest.update(buf, 0, len);
            }
            return toHexString(digest.digest());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
    * @Author: MeanFan
    * @Description: 字节数组转String
    * @Param: [data]
    * @return: java.lang.String
    **/
    public static String toHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }
}
