package com.changgou;

import org.springframework.util.DigestUtils;

public class MD5Test {
    public static void main(String[] args) {
        String s = DigestUtils.md5DigestAsHex("123".getBytes());
        System.out.println(s);
    }
}
