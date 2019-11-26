package cn.wangzhen;

import java.lang.reflect.Constructor;

public class test {
    public static void main(String[] args) throws NoSuchMethodException {
        Demo1 instance = Demo1.getInstance();
        System.out.println(instance);
        Constructor<Demo1> constructor = Demo1.class.getConstructor();
    }
}
