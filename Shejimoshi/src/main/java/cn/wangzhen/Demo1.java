package cn.wangzhen;

/**
 * 单例设计模式
 */
public class Demo1 {
    private static Demo1 demo1;
    //构造函数私有化
    private Demo1() {
    }
    public static Demo1 getInstance(){
        if (demo1==null){
            //如果实例是空的返回一个对象
            demo1 = new Demo1();
        }
        return demo1;
    }
}
