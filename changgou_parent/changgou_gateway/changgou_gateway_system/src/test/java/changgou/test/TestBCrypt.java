package changgou.test;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class TestBCrypt {
    public static void main(String[] args) {
        for (int i=0;i<10;i++) {
            //获得盐
            String gensalt = BCrypt.gensalt();
            System.out.println("盐是:"+gensalt);
            //加盐加密
            String hashpw = BCrypt.hashpw("123456", gensalt);
            System.out.println("加密后的密码:"+hashpw);
            //解密
            boolean checkpw = BCrypt.checkpw("123456", hashpw);
            System.out.println("密码校验结果"+checkpw);
        }
    }
}
