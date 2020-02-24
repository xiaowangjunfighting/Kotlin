package com.example.kotlin2020

import android.util.Log
import java.io.BufferedReader
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException


//编译正常
fun strLenSafe(s: String) = s.length

//编译错误
//fun strLenSafe1(s: String?) = s.length
//修改后
fun strLenSafe1(s: String?) = if (s != null) s.length else 0

fun testNull() {
    logAllCaps(null)
    Worker(null).testSafeInvoke()

//    testNotNullAssert(null)
}

fun logAllCaps(str: String?) {
    //str可空，返回值可空的类型为String?
    val a: String? = str?.toUpperCase()
    val b: Int? = str?.length
    Log.e("wcc", "$a") //null
    Log.e("wcc", "$b") //null
}

class Address(val country: String)
class Company(val address: Address?)
class Worker(val company: Company?)

fun Worker.testSafeInvoke() {
    val country: String? = company?.address?.country
    val result = if (country == null) "unknown" else country

    //Elvis运算符简化后：
    val result1 = country ?: "unknown"

    //Elvis运算符其他应用：
    val result2 = country ?: throw IllegalArgumentException("unknown")

    Log.e("wcc", result) //unknown
}

//安全转换运算符、Elvis运算符结合使用例子：
class Designer(val firstName: String, val lastName: String) {
    override fun equals(other: Any?): Boolean {
        val otherDesigner = other as? Designer ?: return false

        //若代码可以执行到这里，otherDesigner必定为Designer类型
        return this.firstName == otherDesigner.firstName &&
                this.lastName == otherDesigner.lastName
    }

    override fun hashCode(): Int {
        return firstName.hashCode() * 37 + lastName.hashCode()
    }
}

/*
    kotlin中变量和函数参数默认不为null，可以在类型后面加上?允许为null；

    一个可空类型的值，不可调用它的方法，不能赋值给非空类型变量，不能传给非空类型参数的函数；

    安全调用运算符?.  ：把null检查和调用方法(或属性)合并为一个操作。非空则正常调用函数，否则不会调用且表达式返回null；

    Elvis运算符?:   : 运算符左右两边接受两个表达式。若左边表达式不为空，结果为左边表达式，否则结果为右边表达式；

    安全转换运算符as?  : 尝试把变量转化为指定类型，若类型不合适返回null。

    安全转换运算符有时候会返回null，经常与Elvis运算符一起使用；
 */


fun testNotNull(country: String?) {
    val r = country!!
    Log.e("wcc", "$r")

    val email: String? = null
    val other: String
    email?.let { other = it }
}

/*
    非空断言运算符!!    :可以将任何变量值转化为非空，对于null值会抛出KotlinNullPointerException异常；

    避免在一行代码中显示多个!!，否则无法根据栈的跟踪信息定位问题；

    let函数：将一个可空变量赋值给非空变量(或函数非空参数)。
        若let函数的调用者为null，则不会执行lambda中代码；否则会执行lambda中代码；
 */

class Service {
    var list: ArrayList<String>? = null

    lateinit var list1: ArrayList<String>

    fun init() {
        list = arrayListOf()
        list1 = arrayListOf()
    }

    fun test() {
        //1
        val size = list?.size
        val size1 = list1.size
    }
}

/*
    1处用了安全调用运算符，若不想这样，可以在声明变量list时用lateinit修饰;

    若lateinit变量没有初始化就使用，则会报异常；
 */


//定义一个可空的String类的扩展函数
fun String?.isNull() {
    this == null
}

fun testExtension() {
    val str: String? = null
    //1
    str.isNull()
}

/*
    为一个可空类型定义扩展函数时，可以用可空变量直接调用这个函数。
        例如：在str可空的情况下，1处没有使用安全调用运算符；

    使用场景：需要在可空类型上调用扩展函数；
 */

//t可为null
fun <T> printHashCode(t: T) {
    print(t?.hashCode())
}

//t不可为null
fun <T : Any> printHashCode1(t: T) {
    print(t.hashCode())
}

/*
    Kotlin中泛型类、泛型函数的类型参数默认可空，尽管没有用?标识。(类型参数是唯一的例外)

    任何类型(包括可空类型)都可以替换类型参数；

    若需使类型参数非空，则需要在给类型参数一个非空上边界类型；
 */

fun testUseJavaVariable() {
    val man = Man()
    man.setName(null)
    man.setAddress("china")
    val name: String? = man.name //可空
    val address: String = man.address //不为空

    man.setEmail(null)
    val uppperStr1 = man.email?.toString() //可空
    val upperStr = man.email.toString() //不为空
}

/*
    @Nullable String name;
    @NotNull String address;

    Java中的@Nullable String，在kotlin中被当做String?  ;
    Java中的@NotNull String，在kotlin中被当做String    ;

    平台类型：kotlin中不知道可空性信息的类型。当做可空类型或非空类型都可以，安全交给了开发者；

    String!表示平台类型，但不属于正常语法的一部分，往往会在报错信息中看到；

    在Kotlin中重写Java类或接口的方法时，要明确方法参数和返回值的可空性；
 */

fun testVariable() {
    val a: Int = 1
    val list = listOf(2L, 3L)

    //编译错误
//    val result = a in list

    val result = a.toLong() in list

    val b1 = "12".toByte()
    val b2 = "12".toShort()
    val b3 = "12".toInt()
    val b4 = "12".toLong()
    val b5 = "12.3".toFloat()
    val b6 = "12.3".toDouble()
    val b7 = "true".toBoolean()
}

/*
    Kotlin中不区分基本数据类型和包装类型。除了泛型类型参数会编译成包装类，大多数时候都会编译为基本数据类型；

    由于Java中基本数据类型不为null，因此在kotlin中引用Java基本数据类型时，会认定为非空类型；

    Kotlin中Int?类型，Java中会当做包装类型使用；

    代码中变量用到了不同的数字类型，必须显示转换类型。例如：Int和Long

    转换类型的方法：toByte,toChar,toShort,toInt,toLong,toFloat,toDouble(没有Boolean的转换)；

    Kotlin提供了一些库函数，将字符串的内容转化为基本数据类型，若失败抛出NumberFormatException;

    数字值不需要使用转换方法；

    一些基本数据类型的写法：
        Long -> 2L
        Double -> 0.12, 1.2e10
        Float -> 12.3f, .123F, 1e3f
        十六进制：0xABC, 0Xabc
        二进制：0B1010, 0b1010
 */

fun testAny() {
    val answer : Any = 1 //Any是引用类型，1会被装箱
}

/*
    Java中Object是所有类的超类，但不包括基本数据类型int等；

    Kotlin中Any是所有类型的超类型，包括基本数据类型Int等；

    Kotlin中使用Any时，它会编译成Java字节码中的Object。但Any不能使用Object中的方法；

    Any是非空类型，Any?是可空类型；
 */

fun testUnit(): Unit {

}

interface Processor<T> {
    fun proccess(): T
}

class NoResultProcessor : Processor<Unit> {
    override fun proccess() : Unit{ //Unit可省略

        return Unit //return可省略
    }
}

fun testNothing() : Nothing{
    throw IllegalArgumentException("nothing")
}

fun testNothing1() : Nothing{
    //无限循环不会结束
    while (true) {

        //无意义
        val a: Nothing
    }
}

/*
    Kotlin函数返回值Unit相当于Java中void，作为函数返回值可省略；

    若函数返回值是Unit，且没有重写泛型函数，底层会编译Unit为void；

    与void相比，Unit是一个完整的类型，可以作为类型参数使用；

    函数返回值为Nothing，表示函数不会正常结束。Nothing声明变量没有意义；
 */


fun readNumber(bf: BufferedReader) : List<Int?> {
    //创建元素可空的集合
    val result = arrayListOf<Int?>()
    val ls = bf.lineSequence()
    for (str in ls) {
        /*try {
            val number = element.toInt()
            result.add(number)
        } catch (e: NumberFormatException) {
            result.add(null)
        }*/

        //可以toIntOrNull方法替换上面逻辑
        result.add(str.toIntOrNull())
    }
    return result
}

//计算集合中有效数字的和
fun addValidNumber(numbers: List<Int?>) : Int{
    var invalidCount = 0;
    var validSum = 0;
    for (number in numbers) {
        if (number == null) {
            invalidCount++
        } else {
            validSum += number
        }
    }
    return validSum
}

//使用操作集合的函数处理
fun addValidNumber1(numbers: List<Int?>) : Int{
    val newList = numbers.filterNotNull()
    return newList.sum()
}

fun testIterable() {
    //创建可读集合
    val list: List<Int> = listOf()
    val set: Set<Int> = setOf()
    val map: Map<Int, Int> = mapOf()

    //创建可变集合
    val arrayList: ArrayList<Int> = arrayListOf()
    val hashSet: HashSet<Int> = hashSetOf()
    val hashMap: HashMap<Int, Int> = hashMapOf()
}


class MyProcessor : FileProcessor {
    override fun process(list: MutableList<String?>?) { //1

    }
}

/*
    kotlin支持类型参数可空，例如: List<Int?>

    注意集合List<Int>?可空，与集合类型参数List<Int?>可空的区别；

    Java集合在Kotlin都有两种形式：可读集合，可变集合；

    Collections.kt文件提供两种形式集合(括号内为可变集合)：
    Iterable(MutableIterable)
    Collection(MutableCollection)
    List(MutableList)
    Set(MutableSet)
    Map(MutableMap)

    kotlin中ArrayList实现了接口MutableList，HashSet实现了接口MutableSet；

    Kotlin中调用Java中方法，传入的参数不管是可读or可变的，Java都会当做可变。
        因为Java不区分可读集合和可变集合；

    思考这样一件事：若kotlin中需要重写有参数是集合的Java方法，如何考虑集合可读还是可变？
        需要考虑Java中集合类型：集合是否可空，集合元素是否可空，集合是否可变(根据具体业务进行分析)。
        例如：1处方法参数类型可以是：List<String>?  List<String?>，MutableList<String?>?  等；

 */

fun testArray() {
    //int[] a = new int[2]，等价于：
    val a = IntArray(2)

    //int b = {0, 0}，等价于：
    val b = intArrayOf(0, 0)

    /*
    int[] c = new int[2];
    for (int i = 0; i < c.length; i++) {
        c[i] = i * i
    } 等价于：
     */
    val c = IntArray(5){index -> index * index}

    //创建Integer对象数组
    val d = Array<Int>(2){index -> index * index}

    //使用函数创建Integer对象数组
    val e: Array<Int> = arrayOf(1, 2)

    //创建大小为2的对象数组，且初始值为null
    val f: Array<String?> = arrayOfNulls<String>(2)

    //集合转化为数组
    val list = listOf(1, 2)
    val array: Array<Int> = list.toTypedArray() //集合转化为对象数组
    val array1: IntArray = list.toIntArray() //集合转化为基本数据类型数组
    val array2: IntArray = array.toIntArray() //包装类数组转化为基本数据类型数组

    //for..in遍历数组
    for (element in array) {

    }

    //函数遍历数组(没有角标)
    array.forEach {
        it
    }

    //函数遍历数组(有角标)
    array.forEachIndexed {
        index, element ->
        print("$index, $element")
    }

    val filterList: List<Int> = array.filter { it > 1 }
}


/*
    kotlin中数组是一个带有类型参数的类；

    IntArray：定义int类型数组。还有其他类：
    ByteArray
    ShortArray
    LongArray
    FloatArray
    DoubleArray
    CharArray
    BooleanArray

    IntArray构造方法最后一个参数是lambda。输入参数是数组角标，函数体返回值是对应角标下的元素；

    IntArray主构造方法可定义一个确定大小的数组，从构造方法可以通过角标初始化元素；

    操作集合的filter，map等函数，也可以用来操作数组，但返回值是集合类型；

 */
































