package com.example.kotlin2020;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;

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

        Pay pay = Pay.INSTANCE;

        Cat cat = Cat.Companion.fromJson("");

        Chapter08Kt.testJava((x, y) -> Unit.INSTANCE);

    }

    //java类实现kotlin中有默认方法的接口，需要重写自己的默认方法showOff
    class Inner implements Clickable {
        @Override
        public void click() {

        }

        @Override
        public void showOff() {

        }
    }

    public class Button implements View {

        public class ButtonState implements State {

        }

        @NotNull
        @Override
        public State getCurrentState() {
            return new ButtonState();
        }

        @Override
        public void restoreState(@NotNull State state) {

        }



    }

}


