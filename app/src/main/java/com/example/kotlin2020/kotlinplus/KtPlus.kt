package com.example.kotlin2020.kotlinplus

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.util.Size
import androidx.core.content.ContextCompat
import kotlin.reflect.jvm.internal.impl.resolve.scopes.receivers.ThisClassReceiver


fun logE(msg: String?) {
    if (msg == null) {
        Log.e("wcc", "null")
    } else {
        Log.e("wcc", msg)
    }
}


class TestCompanionObject {

    class MyRunnable : Runnable {
        override fun run() {
            Log.e("wcc", "myRunnable")
        }

    }

    companion object : Runnable by MyRunnable()
}

fun testRunnable() {
    TestCompanionObject.run()
}

/*
    伴生对象：只能在类里面，类里有且只有一个；
        伴生对象也是一个单例对象；

    对象声明可以在类或文件里，可以有多个；


    若继承一个类或接口，不想重写太多的方法，可以使用by
 */


fun testLet() {
    val numbers = listOf("one", "two", "three")
    numbers.map { it.length }.filter { it > 3 }
        .let {
            print(it)
            print(it.size)
        }

    val str: String? = null
    logE(str?.let { it }) //null

    numbers.also {
        print(it)
    }.map { it.length }.filter { it > 3 }
}


/*
    作用域函数：let,with,apply,also,run

    作用域函数主要关注:
        函数参数是否为带有接受者的lambda(是，则作用域内可用this表示接受者实例，否则用it表示作用域输入参数)
        返回值是函数调用者为类型，还是lambda的结果(若为前者，则可以继续调用者的链式调用)

    使用场景：
        let函数
            在调用链的结果上调用一个或多个函数；
            仅可用于非空值执行代码，对于空值则不执行lambda内容并返回null

        also函数：
            不会改变对象的数据，只是利用对象做一些打印操作；

    建议：作用域函数可以简化代码，但不要过度使用，否则会降低代码可读性；
         避免嵌套作用域函数；

    Java中静态方法，可以放在companion object{}中

    lateinit修饰的变量：不能初始化，属性类型不能是可空类型；
 */


fun testForEachContinue() {
    //continue效果
    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) {
            return@forEach
        }
        logE("$it") //1 2 4 5
    }

    logE("------")


    //break效果
    run {
        listOf(1, 2, 3, 4, 5).forEach {
            if (it == 3) {
                return@run
            }
            logE("$it") //1 2
        }
    }

}


fun testFunctionType() {

    val value: Int = 1 //完整定义，右边推断无意义(函数类型的区别)
    val value1 = 1 //可以根据右边的值推断value1的类型

    //根据变量action的函数类型，可以推断出输入参数类型是String
    val action: (String) -> Int = {
        1+2
    }

    //拆开理解
    val action1: (String) -> Int
    action1 = {
        1+2
    }

    //根据Lambda中输入参数和返回值类型，推断出action2变量的函数类型；
    val action2= {str: String->
        str.toInt()
    }
    logE("${action2.invoke("1")}") // 1
    logE("${action2("3")}") // 3

}

/*
    与普通的类型不同，函数类型可以：
        1，可以调用以变量名作为方法名的方法，方法参数是函数类型的输入参数；
        2，调用invoke方法，方法参数是函数类型的输入参数；
            以上两个方法的返回值类型，均为函数类型的返回值类型；
            调用以上两个方法执行的代码，均为lambda中代码。类似于：fun t(str: String) : Int = str.toInt()


    public inline fun <T> T.apply(block: T.() -> Unit): T
    public inline fun <T> T.also(block: (T) -> Unit): T

    带接受的lambda参数，定义格式：(变量类型).()  或 变量类型.()
        函数类型的输入参数只能有一个，且不可声明输入参数的变量名；
        lambda的输入参数是变量类型，同时，lambda范围内的this也是变量类型；
 */



fun tt() {
    var list = arrayListOf(1, 2)
    list.apply {
        add(3)
    }
    logE(list.toString()) //[1, 2, 3]
}


fun tt1() {
    var list = arrayListOf(3, 4)
    list.also {
        it += 5
    }

    logE(list.toString()) //[3, 4, 5]
}

/*
    带接受者的lambda，可以修改对象this的内容；
    对象作为函数类型的输入参数，it也可以修改对象this的内容；
 */




fun exe(
    i: Int = 0,
    context: Context? = null,
    text: String? = null,
    action: (String) -> Unit = {},
    action1: (Int) -> Unit = {}
) {

}

fun test(context: Context?) {
    exe(1)

    exe(1) {

    }
}

/*
    调用方法时:
        规则1：显示声明某个参数名后，其他的参数都需要显示声明参数名;
        规则2：若5个参数都有默认值，则参数可以有5种情况，从左至右传入参数个数为1，2，3，4，5；
        规则3：若最后一个参数是函数类型，可以省略前面的默认参数。(若不是函数类型，则必须按照规则2)
 */


fun testArray(context: Context) {
    val states = Array(4){ intArrayOf()}
    states[0] = intArrayOf(android.R.attr.state_pressed)
    states[1] = intArrayOf(android.R.attr.state_enabled)
    states[2] = intArrayOf(android.R.attr.state_focused)
    states[3] = intArrayOf()
    val colors = intArrayOf(
        ContextCompat.getColor(context, android.R.color.holo_red_dark),
        ContextCompat.getColor(context, android.R.color.holo_green_dark),
        ContextCompat.getColor(context, android.R.color.holo_orange_dark),
        ContextCompat.getColor(context, android.R.color.black)
    )
    val colorStateList = ColorStateList(states, colors)
}
/*
    Array类：存放对象(包括数组)，可以用于创建二维数组。
        1，构造方法(可以建立index与元素的关系)；
        2，arrayOf方法：仅仅用于列出有限个元素，初始化元素无法拿到角标；

    IntArray类：只能存放Int类型的值。
        1，构造方法(可以建立index与元素的关系)；
        2，intArrayOf方法：仅仅用于列出有限个元素，初始化元素无法拿到角标；

 */

class Person constructor(val age: Int) : Any(){
    constructor() : this(1) {

    }

    constructor(name: String) : this()
}

class Student : Any{
    constructor() : super() {

    }

    constructor(age: Int) : this() {

    }
}

/*
    若类中有主构造函数：
        父类必须指定构造参数；(需要知道调用哪个super方法)
        类中定义的从构造方法，必须通过调用主构造方法this()或其他从构造方法来初始化；(不能调用super)

    若类中没有主构造方法：
        若类中有从构造方法，则父类无需指定构造参数；
        从构造方法可以通过this()调用其他从构造方法，或super来初始化；

    总结：若有主构造方法，则从构造方法不能调用super来初始化；

    若类没有主和从构造方法，必须父类必须制定一个构造方法；
 */



open class Shape(open val rect: Int) {
    open val size: Int = 0
}

class Rectangle(override var rect: Int) : Shape(rect)


open class R {
    open val size: Int = 0
}
class Square : R() {
    override val size: Int
        get() = 1
}

fun testSquare() {
    Log.e("wcc", "${Square().size}")
}

fun textMaxInt() {
    val result = 0xFFFFFFFF - Int.MAX_VALUE
    Log.e("wcc", "$result")
}


fun testTake() {
    val list = listOf(1, 2, 3)
    list.take(2).forEach {
        Log.e("wcc", "$it")
    }
}






