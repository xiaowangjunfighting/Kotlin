package com.example.kotlin2020;

/**
 * java类，用于和Kotlin进行互调
 */
public class Origin {
    public void start() {
        Chapter03 chapter03 = new Chapter03();
        chapter03.joinToString(null);
        chapter03.joinToString(null, "");
        chapter03.joinToString(null, "" , "");
        chapter03.joinToString(null, "", "", "");

        //Java调用顶层函数，替换静态工具类
        //Chapter03Kt.topFunction();

        //自定义顶层函数生成的类名
        DiyFileName.topFunction();

        DiyFileName.getK();
        DiyFileName.setK(2);

        DiyFileName.getV();

        int w = DiyFileName.w;

        //在Java中调用扩展函数
        DiyFileName.lastChar("Kotlin");
    }
}
