package com.example.kotlin2020

import android.util.Log
import java.io.BufferedReader
import java.io.FileReader
import java.util.concurrent.locks.Lock
import java.util.function.Predicate
import kotlin.concurrent.withLock

/**
 *8.1 声明高阶函数
 */

fun testFunction() {
    val f: (Int, Int) -> Unit = {x: Int, y: Int -> }

    //函数类型的参数类型，返回值可空
    val f1: (Int, Int?) -> Int? = {x: Int, y: Int? -> null}

    //x, y类型已在函数类型变量声明，lambda可以不指定类型；
    val f2: (Int, Int) -> Int = {x, y -> x + y}

    //显示声明函数类型的参数名称
    val f3: (x: Int, y: Int) -> Int = {x, y -> x + y}

    //函数类型的变量可空
    val f4: ((Int, Int) -> Int)? = null


    two { x, y -> x + y }
    two { x, y -> x * y}
}

fun two(operator: (x: Int, y: Int) -> Int) {
    val result = operator(2, 3)
    Log.e("wcc", "$result") //两次调用结果分别是：5  6
}

/*
    高阶函数：以另一个函数作为参数或返回值的函数；
        帮助开发者减少重复代码，改进代码结构；

    函数类型的声明格式： (参数类型1， 参数类型2) -> 返回类型

    函数类型的返回值为Unit，也需要显示声明，不可省略；

    调用作为参数的函数和调用普通函数一样；
 */

//String扩展filter函数
fun String.filter(predicate: Predicate<Char>) : String {
    val sb = StringBuilder()
    for (index in 0 until this.length) {
        val char = this.get(index)
        if (predicate.test(char)) sb.append(char)
    }
    return sb.toString()
}

//将函数参数改为函数类型，修改后
fun String.filter1(predicate : (Char) -> Boolean) : String {
    val sb = StringBuilder()
    for (index in 0 until this.length) {
        val char = this.get(index)

        //仅仅该行代码不一样
        if (predicate(char)) sb.append(char)
    }
    return sb.toString()
}

fun testStrFilter() {
    Log.e("wcc", "12abAB".filter{ it in 'a'..'z' }) //ab
    Log.e("wcc", "12abcAB".filter1{ it in 'a'..'z' }) //abc
}


//-------------------

fun testJava(arg: (Int, Int) -> Unit) {
    arg(1, 2)
}

//给函数类型的参数置顶默认值
fun <T> Collection<T>.mergeElement(separator: String
                                   , firstChar: String
                                   , lastChar: String
                                   , transformer: (T) -> String = {it.toString()}) : String{
    val sb = StringBuilder()
    sb.append(firstChar)

    for ((index, element) in this.withIndex()) {
        sb.append(transformer(element))
        if (index < this.size - 1) {
            sb.append(separator)
        }
    }
    sb.append(lastChar)
    return sb.toString()
}

fun <T> Collection<T>.mergeElement1(separator: String
                                   , firstChar: String
                                   , lastChar: String
                                   , transformer: ((T) -> String)?) : String{
    val sb = StringBuilder()
    sb.append(firstChar)

    for ((index, element) in this.withIndex()) {
        val str = transformer?.invoke(element) //安全调用
            ?: element.toString() //Evils运算符处理为null的情况

        sb.append(str)
        if (index < this.size - 1) {
            sb.append(separator)
        }
    }
    sb.append(lastChar)
    return sb.toString()
}


fun testMergeElement() {
    val list = listOf("a", "BC")
    //不传入参数，使用默认值
    Log.e("wcc", list.mergeElement(",", "(", ")")) //(a,BC)
    //显示指定参数名
    Log.e("wcc", list.mergeElement(",", "(", ")", transformer = {it.toUpperCase()})) //(A,BC)
    //将lambda放到括号外面
    Log.e("wcc", list.mergeElement(",", "(", ")"){it.toLowerCase()}) //(a,bc)
}

fun testFoo(callback: (() -> Unit)?) {
    callback?.invoke()
}

/*
    Java8可以很方便调用，kotlin中参数是函数类型的函数。
        传统的Java调用，需传入一个匿名内部类；

    若Kotlin函数类型的返回值是Unit，Java中需要返回类型Unit.INSTANCE；

    函数类型作为函数参数，可设置默认值和可空性；

    函数类型是一个包含invoke方法的接口的实例；

    函数类型可以作为函数的返回值类型；

    将函数参数抽取到函数的参数中，可以抽取重复的行为；
 */

/**
 * 8.2 内联函数：消除lambda带来的运行时开销
 */

inline fun <T> synchronized(lock: Lock, action: () -> T) : T {
    lock.lock()
    try {
        return action()
    } finally {
        lock.unlock()
    }
}

fun foo(l: Lock) {
    Log.e("wcc", "before sync")
    synchronized(l) {
        Log.e("wcc", "action")
    }
    Log.e("wcc", "after sync")
}

//foo调用内联函数，等价于：
fun foo1(l: Lock) {
    Log.e("wcc", "before sync")

    l.lock()
    try {
        Log.e("wcc", "action")
    } finally {
        l.unlock()
    }

    Log.e("wcc", "after sync")
}

//查看Sequence.map源码，它不是内联函数，传递进来的lambda会编译成函数接口的匿名类
//public fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R> {
//    return TransformingSequence(this, transform) //1
//}


//lambda中代码是加锁的，详情查看withLock源码
fun testLock(lock: Lock) {
    lock.withLock {

    }
}

fun testUse() {
    val br = BufferedReader(FileReader("/sdcard/1.txt"))
    br.use {
        val readLine = br.readLine()
    }
}

/*
    内联函数：函数体会被直接替换到被调用的地方，与正常函数被调用不同；

    Java中可以调用绝大部分的内联函数，但会被当做普通函数调用；

    若一个函数有多个lambda参数，对于不想内联的lambda参数，可以使用关键字noinline;

    处理序列函数的lambda没有内联，而处理集合的函数会产生中间集合(在集合元素很多时，性能损耗较大)。
        因此，若操作的是大集合，可以将集合转化为序列；若操作的小集合，使用集合更好。

    内联函数好处：
        解决了lambda性能较低的问题，减少了创建匿名类和匿名类实例的开销；
        减少了函数调用的开销，转而拷贝内联函数体的代码；

    定义内联函数的注意点：函数体内代码不应过多。否则每次调用内联函数，都会拷贝大量代码；
        解决办法：将于lambda参数无关的代码，放到非内联函数中；

    Lock.withLock扩展函数：给代码加锁；

    Java7支持try-with-resource语法糖关闭资源，kotlin没有类似的语法。
        但,可以使用Closeable或其子类的扩展函数use;
 */


/**
 * 8.3 高阶函数中的控制流
 */

data class Male(val name: String, val age: Int)

fun lookForTom() {
    val list = listOf(Male("tom", 18), Male("jane", 16))
    for (male in list) {
        if (male.name == "tom") {
            Log.e("wcc", "find tom")
            return
        }
    }
    Log.e("wcc", "not find tom")
}

fun lookForTom1() {
    val list = listOf(Male("tom", 18), Male("jane", 16))
    list.forEach {
        if (it.name == "tom") {
            Log.e("wcc", "find tom ...")
            return
        }
    }

    //这里可能不执行
    Log.e("wcc", "not find tom ...")
}

fun lookForTom2() {
    val list = listOf(Male("tom", 18), Male("jane", 16))

    //局部返回加标签，方式1：
    list.forEach {
        if (it.name == "tom") {
            Log.e("wcc", "find tom --")
            return@forEach
        }
    }

    //局部返回加标签，方式2：
    list.forEach label@{
        if (it.name == "tom") {
            Log.e("wcc", "find tom --")
            return@label
        }
    }

    //这里肯定执行
    Log.e("wcc", "not find tom --")
}



fun testReturn() {
//    lookForTom()
//    lookForTom1()
    lookForTom2()
}

//带接受者的lambda，使用标签指定this
fun testTag() {
    StringBuilder().apply outerApply@{
        "abc".apply {
            //this@outerApply是StringBuilder类型，this是String类型
            this@outerApply.append(this)

        }
    }
}

fun testHideFunction() {
    listOf(1, 2, 3).apply {
        this.filter(
            //匿名函数替换lambda
            fun (num): Boolean {
                Log.e("wcc", "num: " + num)
                //返回的是匿名函数
                return num > 2
            }
        )
        Log.e("wcc", "1--")

        this.asSequence().map {
            Log.e("wcc", "it: $it")
            it + 1
        }.toList()
    }

    //最后执行
    Log.e("wcc", "3--")
}

/*
    forEach是一个内联函数，其函数体作为被调用者的一部分，return表达式会将整个函数结束；

    内联函数支持return表达式，非内联函数不支持return。
        非内联函数没有直接调用函数，而是将lambda保存在一个变量中并在其他地方使用，此时return语句无法结束函数调用了；

    局部返回的自定义标签，优先级比默认默认标签名(内联函数名称)高;

    lambda代码块还有一种表现形式：匿名函数；
        使用场景：编写多个退出点的代码时使用

    return表达式会返回最近一个fun标记的函数；
        lambda没有fun，返回的外层函数。匿名函数有fun，返回的是匿名函数；
 */




















