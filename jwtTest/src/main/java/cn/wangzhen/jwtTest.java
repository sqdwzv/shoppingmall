package cn.wangzhen;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class jwtTest {
    public static void main(String[] args) {
        //获得系统当前时间
        long currentTimeMillis = System.currentTimeMillis();
        currentTimeMillis+=100000000000L;
        Date date = new Date(currentTimeMillis);
        JwtBuilder builder = Jwts.builder()
                .setId("888")//唯一编号
                .setSubject("弯针")//设置主题
                .setIssuedAt(new Date())//设置签发日期
                .setExpiration(date)//设置令牌过期时间
                .claim("roles","管理员")
                .signWith(SignatureAlgorithm.HS256, "itcast");//设置签名使用hs256算法,并设置SecretKey(字符串)
        //构建并返回一个字符串
        String jwtToken = builder.compact();
        System.out.println(jwtToken);

        //解析jwt,得到内部数据
        Claims claims = Jwts.parser().setSigningKey("itcast").parseClaimsJws(jwtToken).getBody();
        System.out.println(claims);
    }
}
