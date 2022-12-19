package com.xxxx.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

/**
 * Author: asus
 * Date: 2022/10/22 14:27
 */
@Component
public class MD5Util {

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static String salt = "1a2b3c4d";

    public static String inpuPassToFromPass(String inputPass) {
        String s = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(s);
    }

    public static String fromPassToDbPass(String fromPass, String salt) {

        String s = "" + salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(5) + salt.charAt(4);
        return md5(s);
    }

    public static String inputPassToDBPass(String inputPass, String salt) {
        String fromPass = inpuPassToFromPass(inputPass);
        String dbPass = fromPassToDbPass(fromPass, salt);
        return dbPass;
    }

    public static void main(String[] args) {
        //d3b1294a61a07da9b49b6e22b2cbd7f9
        System.out.println(inpuPassToFromPass("123456"));
        System.out.println(fromPassToDbPass("d3b1294a61a07da9b49b6e22b2cbd7f9", "1a2b3c4d"));
        System.out.println(inputPassToDBPass("123456", "1a2b3c4d"));

    }

}
