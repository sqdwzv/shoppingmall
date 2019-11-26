package com.changgou.oauth;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

public class ParseJwtTest {
    @Test
    public void parseJwt(){
        //基于公钥解析JWT
        String jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZGRyZXNzIjoieGlhbiIsImNvbXBhbnkiOiJoZWltYSJ9.R7ubL9FIPOP-5ddKF-r7eF_7quX5to-Ts7vXze8PlJ4W_5MLzwR6_kAjyDU0dFIXpJLQaiAlS8CFsfbUbw0-OE4WuanAkGHov6cg7P1KOY7w1EIwJrXQr54x-yQ5yO7XHHB7cNdilGD_Z-xNOJYEigAHFfTuHnzQuW2kiumKyLp8TJRBAlAKrZgQ8dCAZ6K50DfdEHV-Cyu1mAZsbrZLTkHMz51bJpOnOtUatA4suc6-jImb0_jdCPYaWeebK7Y66D9PUGYV9MgyeX9Hmit68Y2i9mnQ9Ed7DLdJRNFDhmLN-n6RJIDTdgw4Afz3BjZONb5YVUk__8Dw3FXJAVfl9Q";
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvFsEiaLvij9C1Mz+oyAmt47whAaRkRu/8kePM+X8760UGU0RMwGti6Z9y3LQ0RvK6I0brXmbGB/RsN38PVnhcP8ZfxGUH26kX0RK+tlrxcrG+HkPYOH4XPAL8Q1lu1n9x3tLcIPxq8ZZtuIyKYEmoLKyMsvTviG5flTpDprT25unWgE4md1kthRWXOnfWHATVY7Y/r4obiOL1mS5bEa/iNKotQNnvIAKtjBM4RlIDWMa6dmz+lHtLtqDD2LF1qwoiSIHI75LQZ/CNYaHCfZSxtOydpNKq8eb1/PGiLNolD4La2zf0/1dlcr5mkesV570NxRmU1tFm8Zd3MZlZmyv9QIDAQAB-----END PUBLIC KEY-----";
        Jwt token = JwtHelper.decodeAndVerify(jwt, new RsaVerifier(publicKey));
        String claims = token.getClaims();
        System.out.println(claims);

    }
}
