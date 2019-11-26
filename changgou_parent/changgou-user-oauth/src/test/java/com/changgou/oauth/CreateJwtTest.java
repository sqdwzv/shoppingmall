package com.changgou.oauth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

public class CreateJwtTest {
    @Test
   public void createJWT(){
       //基于私钥生成jwt
        //1.创建一个秘钥工厂
        //1:私钥位置
        ClassPathResource classPathResource = new ClassPathResource("changgou.jks");
        //2:秘钥库密码
        String keyPass = "changgou";
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource,keyPass.toCharArray());
        //2.基于工厂获取私钥
        String alias = "changgou";//秘钥别名
        String password = "changgou";//秘钥密码
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, password.toCharArray());//私钥
        //将当前的私钥转换为rsa私钥
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

        //3.生成jwt
        Map<String,String> map = new HashMap<>();
        map.put("company","heima");
        map.put("address","xian");
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(map), new RsaSigner(rsaPrivateKey));//数据和签名
        String JwtEncoded = jwt.getEncoded();//jwt令牌
        System.out.println(JwtEncoded);
    }
}
