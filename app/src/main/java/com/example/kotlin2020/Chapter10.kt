package com.example.kotlin2020

import android.util.Log
import kotlin.reflect.KFunction

/**
 * 10.1 声明并应用注解
 */

const val des = "Use removeAt(index) instead."

@Deprecated(des, ReplaceWith("removeAt(index)"))
fun remove(index: Int) {

}

fun testAnnotation() {
    //IDEA会提示使用remove<index>替换
    remove(1)
}

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class JsonExclude(val name: String)

//自定义元注解
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class MyAnnotation

@MyAnnotation
@Retention
annotation class TestAnntation

/*
    注解的参数类型可以有：基本数据类型，字符串，枚举，类引用，其他的注解类，以及前面这些类型的数组；
        可以使用泛型类作为注解的参数；

    注解的实参语法与Java类似，但有些不一样：
        类：@annotation(MyClass::class)
        注解：去掉注解前面的@
        数组：用arrayOf函数。若Java中注解参数名为value，也可以当做可变参数使用；

    注解实参需要在编译器就确定，因此属性不能作为实参，除非用const修饰;

    const修饰的属性可以放在一个文件底层或object中，且只能修饰基本数据类型和String;

    注解类的声明：annatation class
        kotlin中应用注解，就是常规构造方法的调用。主构造参数中val是强制性的；
        Kotlin使用Java中的注解，必须对value以外的所有参数，使用命名参数的语法；

    元注解@Target约束使用注解的位置；
        AnnotationTarget.PROPERT：kotlin的字段中可使用该注解；
        AnnotationTarget.FIELD：Java的字段中可使用该注解；
        AnnotationTarget.ANNOTATION_CLASS：定义自己的元注解;

    元注解@Retention：声明的注解是否会存储到class文件，或运行期间反射是否可访问该注解；
        kotlin中注解默认AnnotationRetention.RUNTIME;

    KClass的类型Java中的java.lang.Class类型;
        Any::Class是KClass<Any>类型；
 */

class Player(var name: String, val age: Int) {
    fun play() {

    }
}


fun sumTotal(x: Int, y: Int) = x + y

var abc: Int = 1

fun testPlayer() {
    val p = Player("tom", 21)
    val kClass = p.javaClass.kotlin
    Log.e("wcc", "${kClass.simpleName}") //Player

    val members = kClass.members
    members.forEach {
        //age,name,play,equals,hashCode,toString
        Log.e("wcc","${it.name}")
    }

    //-------

    //顶层函数
    val kFunction1 = ::sumTotal
    //编译器不确定参数个数
    Log.e("wcc", "${kFunction1.call(1, 2)}") // 3

    val kFunction2 = ::sumTotal
    //编译器确定参数个数
    Log.e("wcc", "${kFunction2.invoke(3, 4)}") // 7

    //顶层属性
    val kProperty0 = ::abc
    kProperty0.setter.call(2)
    Log.e("wcc", "${kProperty0.get()}") // 2

    //成员属性
    val p1 = Player("tom", 21)
    val kProperty1 = Player::name
    kProperty1.set(p1, "kobe")
    Log.e("wcc", "${kProperty1.get(p1)}") //kobe


}



/*
    KFunction1, KFunction1是编译器生成类型，在kotlin.reflect没有这样的声明；
        他们都继承了KFunction接口，并额外添加了invoke方法；

    KFunction2<P1, P2, R>声明了operator fun invoke(p: P1, p2: P2) : R

    KCallback是函数和属性的超接口；
        KFunction继承了KCallback，调用call方法编译器不会检查函数参数个数，但运行时可能报错；

    顶层属性：KProperty0接口的实例
    成员属性：KProperty1
    反射不支持访问局部变量；





 */