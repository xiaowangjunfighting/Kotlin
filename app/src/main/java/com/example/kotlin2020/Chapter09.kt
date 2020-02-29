package com.example.kotlin2020

import android.app.Service
import android.util.Log
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * 9.1 泛型类型参数
 */

//扩展属性定义类型参数
val <T> List<T>.lastChar: T
    get() = this.get(this.size - 1)

abstract class StringList: List<String> {
    override fun get(index: Int): String {
        return "a"
    }
}

abstract class MyList<T> : List<T> { //1
    abstract val t: T

    override fun get(index: Int): T {
        return t
    }
}

fun <T : Comparable<T>> max(first: T, second: T) : T{
    return if (first > second) first else second
}

fun <T> ensure(t: T) : Boolean where T : CharSequence, T : Appendable{
    if (!t.endsWith(".")) { //CharSequence的方法
        t.append(".") //Appendable的方法
    }
    return false
}

//尽管可以编译通过，但建议放到后面
class Data<T : Comparable<T>> where T : CharSequence, T : Appendable{

}

/*
    与Java不同的是，kotlin类型实参必须定义。但编译器可以推断出来的实参类型可省略；

    类，接口，普通方法，扩展函数，顶层函数，扩展属性：均可以定义类型参数；
        非扩展属性不可声明类型参数；

    若一个类继承了泛型类或泛型接口，需要给基类传入类型实参或另一个类型形参；
        1处MyList<T>的T和List<T>的T不一样，前面的T是后面T的类型实参；

    一个类可以把自己作为类型实参使用；
        例如String，源码：public class String : Comparable<String>, CharSequence

    类型参数的约束类型只有一个时，<T : 类型>；
    若约束类型为多个，放到后面where T : 类型1， T : 类型2

    若类型参数没有任何约束类型，那么默认约束类型是Any? ，说明类型参数默认可空；
    若需确保类型参数是非空类型，可以置顶约束类型为Any(或其他非空类型)。
 */


/**
 * 9.2 运行时的泛型：擦除和实化类型参数
 */
fun testTypeParameter() {
    val list1 = listOf(1, 2 ,3) //1
    val list2 = listOf("a", "b",  "c") //2

    val value: Int = 0

    Log.e("wcc", "${list1 is ArrayList<Int>}") //false
    Log.e("wcc", "${list1 is List<Int>}") //true
//    Log.e("wcc", "${list1 is List<String>}") //compile error


//    printSum(listOf(1, 2, 3))
//    printSum(setOf(1, 2, 3)) //抛出异常java.lang.IllegalArgumentException
//    printSum(listOf("a", "b","c")) //抛出异常java.lang.ClassCastException
}

fun printSum(c: Collection<*>) {
    val intList = c as? List<Int>
        ?: throw IllegalArgumentException("error type")
    val r = intList.sum()
}


//编译失败
//fun <T> isA(value: Any) = value is T

//解决方案
inline fun <reified T> isA(value: Any) = value is T

//使用场景
fun testReified() {
    val items = listOf("a", 1, "b")
    val strList = items.filterIsInstance<String>()
    Log.e("wcc", strList.toString()) //[a, b]

    //Class实例的传统方式
    ServiceLoader.load(Service::class.java)

    //使用实化类型参数的函数后
    loadService<Service>()
}

inline fun <reified T> loadService() = ServiceLoader.load(T::class.java)


/*
    编译器知道1，2是两种列表，但运行时看起来都是List(类型擦除了)；

    is检查泛型变量时，需要保证泛型类的类型实参正确，否则编译失败；

    as和as?转换可以使用在泛型变量中；

    kotlin中类型参数实化(reified)的内联函数(inline)，Java无法调用；

    声明了reified的类型参数，在运行时不会被擦除；

    Service::class.java等价于Java中Service.class

 */

/**
 * 9.3 变型：泛型和子类型化
 */

fun printContent(list: List<Any>) {
    print(list.toString())
}

fun printContent1(list: MutableList<Any>) {
    list.add(10)
}


fun testPrintContent() {
    printContent(listOf("a", "b"))
    printContent(listOf(1, 2))
    printContent(arrayListOf(1, 2))

    printContent1(mutableListOf(1, 2))
    printContent1(arrayListOf(1, 2))

    //编译失败
//    printContent1(mutableListOf<String>("a", "b"))
}

class Producer<out T>() {
    fun get() : T? {
        return null
    }
}

class Producer1<in T> {
    fun set(t: T){

    }
}

class Producer2<T> {
    fun set(t: T) : T? {
        return null
    }
}


//类型参数作为构造方法的参数
class Producer3<out T>(vararg t: T) {

}

//k不能声明为协变类型参数
class Producer4<out T, K>(val t: T, var k: K) {

}

//私有方法
class Producer5<out T>(private var t: T)


/*
    类，类型，子类型

    非泛型类型的名称，可以当做"类型"；

    泛型类型复杂点，例如：List是一个类，不是一个"类型"；
        每个泛型类都可以生成无数的"类型"，List<String>, List<List<String>>

    子类型：需要A类型的值，能够使用类型B的值当做A的值，则称B是A的子类型；
        Int是Number子类，Any是Any?子类型；

    若类上声明类型参数是协变的，则Producer<Int>是Producer<Number的子类型；
        List<out E>是协变类，List<Int>是List<Number>子类。MutableList<E>则不是协变类；

    若类型参数作为函数的输入参数，用in修饰；若作为函数的返回值，用out修饰；
    若同时作为函数的输入参数和返回值，则该类型参数不能协变；

    不遵守协变规则的情况：
        1，没有val和var修饰的构造方法参数；
        2，类中的私有方法

    构造函数的参数用val和var声明，生成的get,set方法需遵守协变规则。
        getter(out位置)，setter方法(out位置，in位置)；

    协变的特点：限制了类型参数的使用位置；
 */

fun testComparator() {
    val strs = listOf("a", "b")
    val anyComparator = Comparator<Any>{
        a1, a2 -> a1.hashCode() - a.hashCode()
    }

    //Comparator<Any>是Comparator<String>的子类型
    strs.sortedWith(anyComparator)
}

//-------

fun <T> copyData(source: MutableList<T>, destination: MutableList<T>) {
    for (t in source) {
        destination.add(t)
    }
}

//T是R的子类
fun <T : R, R> copyData1(source: MutableList<T>, destination: MutableList<R>) {
    for (t in source) {
        destination.add(t)
    }
}

//使用点变型修改后
fun <T> copyData2(source: MutableList<T>, destination: MutableList<in T>) {
    for (t in source) {
        destination.add(t)
    }
}

//使用点变型修改后
fun <T> copyData3(source: MutableList<out T>, destination: MutableList<T>) {
    for (t in source) {
        destination.add(t)
    }
}

fun testCopyData() {
    copyData(mutableListOf(1, 2, 3), mutableListOf<Int>())

    copyData1(mutableListOf(1, 2, 3), mutableListOf<Number>())
}


/*
    类型参数的使用，可以扩展为它的子类或超类；


    kotlin中点变型对应Java中限定通配符：
        MutableList<in T>等价于Java中MutableList<? super T>;
        MutableList<out T>等价于Java中的MutableList<? extends T>;

    在类型参数的声明和使用不会用到点变型，只有类型实参才会用到点变型；
 */

fun testStar() {
    val anyList = mutableListOf(1, "a", "b")
    val charList = mutableListOf('a', 'b', 'c')
    val toMutableList = charList.toMutableList()
    val mutableList: MutableList<out Any> = if (Random.nextBoolean()) anyList else charList
    val mutableList1: MutableList<*> = if (Random.nextBoolean()) anyList else charList

    //compile ok
    val e: Any = mutableList.get(1)

    //compile error
//    mutableList1.add(1)

}

fun printFirst(list: MutableList<*>) {
    if (list.isNotEmpty()) { //compile ok
        //compile ok
        val first = list.first()

        //compile error
//        list.add(1)
    }
}

//替换点变型的方法
fun <T> printFirst1(list: MutableList<T>) {
    if (list.isNotEmpty()) {
        val first: T = list.first()
    }
}


/*
    当类型实参不重要时，可以*作为类型实参。
        不能使用方法签名中有类型参数；
        只读取数据而不关心它的类型，类型都是Any?
 */


//*另一个使用示例
interface FieldValidator<in T> {
    fun validate(input: T) : Boolean
}

object DefaultStringValidator : FieldValidator<String> {
    override fun validate(input: String): Boolean {
        return input.isNotEmpty()
    }
}

object DefaultIntValidator : FieldValidator<Int> {
    override fun validate(input: Int): Boolean {
        return input >= 0
    }
}

fun testValidate() {
    //*表示不限定类型
    val map = mutableMapOf<KClass<*>, FieldValidator<*>>()
    map[String::class] = DefaultStringValidator
    map[Int::class] = DefaultIntValidator

    val strVal = map[String::class]

    //compile error
//    strVal.validate("a")

    //改进后编译成功，有警告
    val strVal1 = map[String::class] as FieldValidator<String>
    strVal1.validate("a")

    //编译也成功，但运行时肯定报错
    val strVal2 = map[Int::class] as FieldValidator<String>
    strVal2.validate("a")

    //终极版测试
    Validators.registerValidate(String::class, DefaultStringValidator)
    Validators.registerValidate(Int::class, DefaultIntValidator)

    Validators.get(String::class).validate("a")
    Validators[String::class].validate("a") //使用运算符
}

//终极版修改
object Validators {
    private val validators = mutableMapOf<KClass<*>, FieldValidator<*>>()

    //封装set逻辑
    fun <T : Any> registerValidate(clazz: KClass<T>, fv: FieldValidator<T>) {
        validators[clazz] = fv
    }

    //封装get逻辑
    operator fun <T : Any> get(clazz: KClass<T>) : FieldValidator<T> {
        val fv = validators[clazz]

        return fv as? FieldValidator<T> ?: throw java.lang.IllegalArgumentException("error")
    }
}






















