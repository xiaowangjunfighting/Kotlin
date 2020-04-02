package com.example.kotlin2020

import java.io.BufferedReader
import java.io.StringReader
import java.util.*

/**
 * 2.1 基本要素：函数和变量
 */
fun max(a: Int, b: Int): Int {
    return if (a > b) a else b
}

//简化，省略花括号和return
fun max1(a: Int, b: Int): Int = if (a > b) a else b

//进一步简化，省略返回类型
fun max2(a: Int, b: Int) = if (a > b) a else b

/*
    if是有结果值的表达式，类似于Java的三元运算符；

    表达式：可以有值，并且可以作为另一个表达式的一部分使用；

    若函数体是一个表达式，则可以去掉{}和return。因此函数分为：代码块体和表达式体，可以用快捷键option+enter切换。

    表达式体函数：kotlin的类型推导下，可以省略返回类型。但，代码体函数则不可省略返回类型和return。
* */

val a: Int = 1

//可省略变量类型
val a1 = 1

fun test() {
    val a2: Int
    a2 = 1

    val message: String

    message = if (a2 > 0) "more than 0" else "less than 0"


    //val变量不可变，但其指向的对象是可变的
    val list = arrayListOf("java")
    list.add("kotlin")


    //字符串中引入变量，用$
    val str = "java"
    print("print: $str") //print: java

    //转义则可打印$
    print("print: \$str") // print: $str


    //字符串中引入表达式(双引号可以嵌套双引号，只要双引号是表达式的一部分)
    print("print: ${if (a2 > 0) "more than 0" else "less than 0"}") //print: more than 0
}


/*
    与Java不同的是，kotlin声明变量以关键字开始，然后是变量名称，最后是变量类型(可加可不加)；

    变量的关键字分为val(不可变)和var(可变)，分别对应Java中的final变量和非final变量；

    尽量使用val声明变量，除非一些特殊情况需要var，这样更接近函数式变成风格；

    编译器只会在初始化变量时推断类型，不会考虑后续的赋值操作；

    $向字符串引入变量，编译后的代码创建了StringBuilder，并把常量和变量添加进去；

 */


/**
 * 2.2 类和属性
 */
class Person(val name: String, var isMale: Boolean){

}

class Rect(val width: Float, val height: Float) {
    //自定义访问器getter
    val isSquare: Boolean
        get() {
            return width == height
        }

    //简化后
    val isSquare1: Boolean
        get() = width == height
}

fun test1() {
    val person = Person("Tom", true)
    print("name: ${person.name}") //getter 相当于java中peron.getName()
    print("sex: ${person.isMale}") //getter 相当于java中person.isMale()
    person.isMale = false //setter, 相当于java中person.setMale(false)

    val rect = Rect(100f, 100f)
    //相当于java中调用rect.isSquare()
    print("result: ${rect.isSquare}") //result: true
}

/*
    定义一个Person类，这种类通常叫值对象；

    kotlin中类的默认可见性是public，可以省略它；

    java类中仅提供getter，相当于kotlin中val变量；提供getter,setter方法，相当于kotlin中var变量；

    kotlin中包结构和目录结构可以不对应，但是保持Java的规范却是一个很好的策略；

    kotlin中import可以导入类，以及顶层函数；

 */

/**
 * 2.3 表示和处理选择：枚举和"when"
 */
//定义一个枚举类，声明属性
enum class Color(val r: Int) {
    RED(255), GREEN(0), BLUE(0);

    //定义函数
    fun rgb() = r
}

fun getColorValue(color: Color) =
    when (color) {
        Color.RED -> 255
        Color.GREEN, Color.BLUE -> 0
    }

//when的实参可以为任意对象
fun getColorValue(c1: Color, c2: Color) =
    when (setOf(c1, c2)) {
        setOf(Color.RED, Color.GREEN), setOf(Color.RED, Color.BLUE) -> 255
        else -> 0
    }

//避免多次创建Set造成的吸能开销，分支是布尔类型
fun getColorValue1(c1: Color, c2: Color) =
    when {
        (c1 == Color.RED || c2 == Color.RED) -> 255
        else -> 0
    }

/*
    enum是一个软关键字，只有在class前才是关键字，其他时候可当做名称使用；

    枚举类中的常量列表和方法必须要用;隔开，这也是kotlin中唯一使用分号的地方；

    when相当于java中的switch，但不需要break语句；多个值合并到同一个分支，用;隔开。

    when是一个有返回值的表达式，上述就是一个多行的表达式函数；

    枚举常量可以导入，可使用快捷键option+enter提示；

    when表达式的实参可以是任何对象，它被检查是否与分支的类型相同。而java中switch只允许一些数值变量，枚举，String;

    when分支可以使用任意的表达式，这样可以简化代码；

    when的分支都不匹配时，会执行else分支；

    setOf函数：创建一个Set集合，并存储函数中的参数；
 */

//定义一个标记接口
interface Expr

//定义两个子类
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr

fun eval(e: Expr): Int {
    if (e is Num) {
        val n = e as Num
        return n.value
    }

    //e必须是val变量，且不能有自定义的访问器
    if (e is Sum) {
        return eval(e.left) + eval(e.right)
    }

    throw IllegalArgumentException("unknown expression")
}

//简化代码，去掉花括号和return
fun eval1(e: Expr): Int =
    if (e is Num) e.value
        else if (e is Sum) eval1(e.left) + eval1(e.right)
        else throw IllegalArgumentException("unknown expression")

//使用when替换if
fun eval2(e: Expr): Int =
    when(e) {
        is Num -> e.value
        //代码块最后一个表达式为结果
        is Sum -> {
            print("")
            eval2(e.left) + eval2(e.right)
        }
        else -> throw IllegalArgumentException("unknown expression")
    }

/*
    is相当于Java中的instanceOf，通过is检查的变量可以直接当做该类型使用，这个叫智能转换；

    val n = e as Num：将变量e向下转型为Num，并赋值给n；

    if分支只有一个表达式，则可去掉花括号；若有多个表达式，则以最后一个表达式作为结果返回；

    if和when的分支，不仅可以是表达式，还可以是代码块。代码块最后一个表达式，就是返回的值；

    规则：所有使用代码块并需要得到一个结果的地方，代码块中最后一个表达式就是结果。
    该规则对if, when, try, catch, lambda等有效，但对常规函数无效；

 */

/**
 * 2.4 迭代事物："while"循环和"for"循环
 */

//kotlin中while循环和Java一样


fun fizzBuzz(i: Int) =
    when {
        i % 15 == 0 -> "FizzBuzz"
        i % 3 == 0 -> "fizz"
        i % 5 == 0 -> "Buzz"
        else -> "$i"
    }

fun iteratorFizzBuzz() {
    //1到100，自增1
    for (i in 1..100) {
        print(fizzBuzz(i))
    }

    //100到1，自减2
    for (i in 100 downTo 1 step 2) {
        print(fizzBuzz(i))
    }

    //1到99，自增1
    for (i in 0 until 100) {
        print(fizzBuzz(i))
    }

}

/*
    kotlin没有Java中的一般for循环，但可以用区间来实现，区间是闭合的；

    起始值..结束值：表示一个区间，..是一个运算符。从起始值开始递增，步长是1；

    downTo：表示递减；step：可设定步长；

    until：区间不包含结束值；
 */

fun forMap() {
    val map = TreeMap<Char, String>()

    //一般的for循环
    for (c in 'A'..'F') {
        map[c] = Integer.toBinaryString(c.toInt())
    }

    //迭代Map集合
    for ((key, value) in map) {
        println("$key = $value")
    }

    //迭代List集合
    val list = arrayListOf("a", "b")
    list.add("c")
    for ((index, element) in list.withIndex()) {
        println("$index : $element")
    }
}

/*
    ..不仅可以创建数字区间，还可以创建字符区间；

    for循环允许展开集合中的元素，Map则是将键值对存储到变量key， value中;

    小技巧：map[key]读取值，map[key]设置值，不像Java中需要get, put方法；
 */


//相当于(c >= 'a' && c <= 'z') || (c >= 'A' && x <= 'Z')
fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'

fun isNotDigit(c: Char) = c !in '0'..'9'

fun str() {
    val result = "kotlin" in "java".."android"
    val result1 = "ac" in setOf("ab", "bc")
}

fun recognize(c: Char) =
    when(c) {
        in 'a'..'z' -> "yes"
        in 'A'..'H', in 'I'..'Z' -> "no"
        else -> "no"
    }

/*
    任意实现了Comparable接口的类，都可以这种类型的对象区间。这样的对象区间不能列出所有的对象，但可以比较大小。

    in可以检查集合中是否包含某一元素；

    in可以作为when的分支(检查变量类型的is也可以)

 */

/**
 * 2.5 Kotlin中的异常
 */
fun testException(number: Int) {
    val percentage =
        if (number in 1..100)
            number
        else throw IllegalArgumentException("number must more than 0 and less than 100")


    val br = BufferedReader(StringReader("123"))
    val line = br.readLine()
    try {
        val str = Integer.parseInt(line)
    } catch (e: NumberFormatException) {
        //...
    } finally {
        br.close()
    }


    val br1 = BufferedReader(StringReader("not a number"))
    val result =
        try {
            Integer.parseInt(br1.readLine())
        } catch (e: NumberFormatException) {
            null
        }
    print("$result")
}

/*
    Kotlin中抛出异常和Java一样，只是不需要关键字new；

    throw e是一个表达式；

    kotlin中编译时和运行时异常，可以都不出处理。kotlin认为Java中的异常规则毫无意义，
    例如：close方法出错后，通常没有有意义的代码进行处理；

    try关键字引入了一个表达式，不管是单行，多行，都需要用花括号括起来；

 */


































































