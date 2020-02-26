@file:JvmName("DiyFileName")
package com.example.kotlin2020

import android.util.Log
import java.lang.IllegalArgumentException

class Chapter03 {

    fun start() {
//        test3_1()
//        test3_2()
//        test3_3()
//        test3_4()
        test3_5()
    }

    /**
     * 3.1 在Kotlin中创建集合
     */

    fun test3_1() {
        val set = hashSetOf("b", "a")
        val list = arrayListOf("b", "a")
        val map = hashMapOf("b" to 2, "a" to 1)

        print(set.javaClass)
        print(list.last()) //最后一个元素
        print(list.max()) //最大值
    }
    /*
        to不是一个特殊结构，只是一个函数;
        Kotlin中的.javaClass相当于java中的getClass();
     */



    /**
     * 3.2 让函数更好调用
     */

    private fun test3_2() {
        Log.e("wcc", arrayListOf("a", "b", "c").toString()) // [a, b, c]
        Log.e("wcc", joinToString(arrayListOf(1, 2, 3), ";", "<", ">")) // <1;2;3>

        joinToString(arrayListOf(1, 2, 3), separator = ";", first = "<", last = ">")

        //不指定参数名，可以省略末尾参数
        Log.e("wcc", joinToString(arrayListOf(1, 2, 3), ";")) //1;2;3
        //指定参数名，可以无序参数参数
        Log.e("wcc", joinToString(arrayListOf(1, 2, 3), last = ">", first = "<")) //<1-2-3>

        topFunction()
    }

    //使用Java方式打印指定规则的集合
    @JvmOverloads
    fun <T> joinToString(collection: Collection<T>
                         , separator: String = "-"
                         , first: String = ""
                         , last: String = ""): String {
        val sb = StringBuilder(first)
        for ((index, element) in collection.withIndex()) {
            if (index > 0) sb.append(separator)
            sb.append(element)
        }
        sb.append(last)
        return sb.toString()
    }

    /*
        kotlin函数的调用：可以指定参数名，且某一个参数指定了参数名，其后所有的参数都要指定参数名；

        kotlin函数的声明：参数可以有默认参数值；

        函数的调用：可以不传入有默认值的参数，而使用默认参数值。
                  若不指定参数名，那么只可以省略末尾的参数；若指定了参数名，则可以省略中间的参数，且可以任意顺序传入参数。

        @JvmOverloads：Java中调用@JvmOverloads注解的joinToString函数时，会有4个重载方法；
     */
}

//顶层函数
fun topFunction(): String {
    return ""
}

var k = 1
val v = 1
const val w = 1

/*
    顶层函数：函数的在一个文件里，而非在一个类里。用于替换Java中的静态工具类；

    Java中调用顶层函数：Chapter03Kt.topFunction();

    自定义顶层函数生成的类名：在package前面添加注解@file:JvmName("DiyFileName")

    var声明可变的顶层属性：Java中可通过getter访问，setter修改值；
    val声明不可变的顶层属性：Java中可通过getter访问；
    const val相当于pubic static final：Java中可通过(文件生成类名.变量名)访问；
 */

/**
 * 3.3 给别人的类添加方法：扩展函数和属性
 */

private fun test3_3() {
    Log.e("wcc", "Kotlin".lastChar().toString())

    val result = arrayListOf(1, 2, 3).joinToString("-")
    Log.e("wcc", result) //<1-2-3>

    arrayListOf("1", "2", "3").join()

    Log.e("wcc", "Kotlin".lastChar.toString()) //n

    val sb = StringBuilder()
    sb.append("a")
    sb.append("b")
    sb.lastChar = 'c'
    Log.e("wcc", sb.toString()) //ac
}

fun String.lastChar() = this.get(this.length - 1)

/*
    扩展函数的定义：将接受者类型放在函数名前面，并用.隔开；

    扩展函数可访问接受者类型的方法和属性，但不能访问私有和受保护的成员；

    项目其他包中访问扩展函数：需要先导入扩展函数 import com.example.kotlin2020.lastChar;

    若多个包中函数名重复，在某一个类中需要同时使用这些函数，那么如何区分？
    1，给函数重命名；(kotlin不推荐)
    2，使用as给导入的函数重新命名：import com.example.kotlin2020.lastChar as last;

    Java中调用扩展函数：跟顶层函数一样，但函数增加的第一个参数，必须是接受者对象"Kotlin";
    eg: DiyFileName.lastChar("Kotlin");

    扩展函数是静态函数，Java中把调用对象作为第一个参数；

    由于扩展函数是静态函数，因此子类不能重写父类的扩展函数，虽然父类和子类可以有同名的扩展函数；

    若类的扩展函数和成员函数有相同的签名，则优先调用成员函数。
    因此，在类中定义成员函数时，要避免和已有的扩展函数签名相同，否则可能会引起未知的错误；
 */

//joinToString方法终极版
fun <T> Collection<T>.joinToString(separator: String = ";", first: String = "<", last: String = ">"): String {
    val sb = StringBuilder(first)
    for ((index, element) in this.withIndex()) {
        if (index > 0) sb.append(separator)
        sb.append(element)
    }
    sb.append(last)
    return sb.toString()
}

//使用更具体的类型，作为扩展函数的接受者类型
fun Collection<String>.join(separator: String = ";", first: String = "<", last: String = ">")
        = joinToString(separator, first, last)


//扩展不可变属性
val String.lastChar: Char
    get() = this.get(this.length - 1)

//扩展可变属性
var StringBuilder.lastChar: Char
    get() = this.get(this.length - 1)
    set(value: Char) {
        setCharAt(this.length - 1, value)
    }

/*
    扩展属性：不可以初始化，因为没有地方存储初始值；
            必须定义getter函数，因为没有支持字段就没有默认的getter实现；
 */


/**
 * 3.4 处理集合：可变参数，中缀调用和库的支持
 */


private fun test3_4() {
    val array: Array<Int> = arrayOf(3, 4)
    val list: List<Int> = listOf(1, 2, *array)
    Log.e("wcc", list.toString()) //[1, 2, 3, 4]

    val (a, b) = 1 to 2
    Log.e("wcc", "a = $a, b = $b") //a = 1, b = 2
}

//to函数源码，返回一个库函数Pair<A, B>
//public infix fun <A, B> A.to(that: B): Pair<A, B> = Pair(this, that)


/*
    许多扩展函数在Kotlin标准库中都有声明，学习所有的扩展函数没有必要，建议使用IDE的提示功能学习；

    listOf是一个扩展函数，且函数参数的数量可变。
    Java是将元素打包到数组中，再传递给函数的参数；而Kotlin是显示解包数组，以便所有的元素作为单独的参数使用。
    Kotlin使用解包数组方式：在前面加*。这样函数可以同时传入一些固定值和数组，这是Java不支持的；

    "1" to "a" : 中缀调用，与"1".to("a")等价。to是一个函数，而非特殊的符号；

    无论是普通函数还是扩展函数，只要只有一个参数，就可以使用中缀调用；

    若一个函数允许中缀调用，则必须用infix修饰符标记函数；

    Pair可以初始化两个变量，这个功能叫解构声明；
 */


/**
 * 3.5 字符串和正则表达式的处理
 */

private fun test3_5() {
    //分割字符串，匹配一个点或破折号
    val split = "123.456-6-A".split("\\.|-".toRegex())
    Log.e("wcc", split.toString()) //[123, 456, 6, A]

    //对于一些简单的分割，可以不使用正则表达式
    val split1 = "123.456-6-A".split(".", "5") //[123, 4, 6-6-A]
    Log.e("wcc", split1.toString())

    val path = "com/example/kotlin2020/Chapter03.kt"

    //分割字符串，可以使用一些函数替换正则表达式
    val directory = path.substringBeforeLast("/")
    val fullName = path.substringAfterLast("/")

    val fileName = fullName.substringBefore(".")
    val extension = fullName.substringAfter(".")

    Log.e("wcc", "$directory   $fileName    $extension") //   com/example/kotlin2020   Chapter03    kt

    //使用三重引号字符串定义正则表达式
    val regex = """(.+)/(.+)\.(.+)""".toRegex()
    val result = regex.matchEntire(path)
    if (result != null) {
        val (a, b, c) = result.destructured
        Log.e("wcc", "$a   $b   $c") //    com/example/kotlin2020   Chapter03   kt
    }

}

/*
    Kotlin中，使用toRegex将字符串转化为正则表达式；

    split重载方法中，支持任意数量的纯文本字符串分隔符；

    substringBeforeLast: 最后一个分隔符之前的子字符串;
    substringAfterLast: 最后一个分隔符之后的子字符串;
    substringBefore: 第一个分隔符之前的子字符串；
    substringAfter: 第一个分隔符之后的子字符串；

    三重引号字符不需要对任何字符进行转义；
 */


/**
 * 3.6 让你的代码更整洁：局部函数和扩展
 */

class User(val name: String, val address: String, val id: Int)

//代码重复
fun saveUser(user: User) {
    if (user.name.isEmpty()) {
        throw IllegalArgumentException("${user.id} has empty name")
    }

    if (user.address.isEmpty()) {
        throw IllegalArgumentException("${user.id} has empty address")
    }

    //save...
}

//使用局部函数，封装检查字段的逻辑
fun saveUser1(user: User) {
    fun invalidate(user: User, value: String, fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalArgumentException("${user.id} has empty $fieldName")
        }
    }

    invalidate(user, "", "name")
    invalidate(user, "", "address")

    //save...
}

//局部函数省略User参数
fun saveUser2(user: User) {
    fun invalidate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            //局部函数可访问外部函数的参数和变量
            throw IllegalArgumentException("${user.id} has empty $fieldName")
        }
    }
    invalidate("", "name")
    invalidate("", "address")

    //save...
}

//使用扩展函数，封装检查的逻辑
fun saveUser3(user: User) {
    user.invalidateBeforeSave()

    //save...
}

//扩展函数中嵌套了局部函数
fun User.invalidateBeforeSave() {
    fun invalidate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalArgumentException("${this.id} has empty $fieldName")
        }
    }

    invalidate("", "name")
    invalidate("", "address")
}

/*
    扩展函数也可以声明为局部函数；

    不建议深度嵌套多层局部函数；
 */

























