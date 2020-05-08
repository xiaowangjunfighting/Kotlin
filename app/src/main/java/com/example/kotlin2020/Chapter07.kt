package com.example.kotlin2020

import android.util.Log
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.lang.IllegalArgumentException
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * 7.1 重载算数运算符
 */

data class Point(var x: Int, var y: Int) {

//    operator fun plus(other: Point) : Point {
//        return Point(x + other.x, y + other.y)
//    }
}

operator fun Point.plus(other: Point) : Point {
    return Point(this.x + other.x, this.y + other.y)
}

operator fun Char.times(count: Int): String {
    return toString().repeat(count)
}

fun testOperator() {
    val p = Point(1, 1) + Point(2, 2)
    Log.e("wcc", "${p.x} ${p.y}") // 3 3

    Log.e("wcc","${'a' * 3}")

    Log.e("wcc", "${1 and 2}") //0


    //可变变量
    var p2 = Point(1, 2)
    var cloneP2 = p2
    p2 += Point(1, 2)
    Log.e("wcc", "${cloneP2 == p2}") //false

    //可变集合用val声明
    val arrayList = arrayListOf(1, 2)
    val cloneArrayList = arrayList
    arrayList += 3 //修改的是同一个集合
    Log.e("wcc", "${cloneArrayList == arrayList}") //true

    //可读集合需用var声明
    var list = listOf(1, 2)

    var cloneList = list
    list += 3  //返回一个修改后的副本集合
    Log.e("wcc", "${cloneList == list}") //false

    //使用运算符添加集合，会返回一个新的集合
    val newList = list + listOf(3, 4, 5)
    Log.e("wcc", "$newList") //[1, 2, 3, 3, 4, 5]
}

/*
    operator关键字：重载运算符的函数都需要operator标记；

    operator声明plus函数后，可以直接用+来调用函数；

    可重载的二元运算符：
        +(plus)
        -(minus)
        *(times)
        /(div)
        %(mod)

    除了用成员函数重载运算符，还可以用扩展函数；

    重载运算符时，不要求运算符两边是相同类型，也不要求返回值是相同类型；

    可以重载operator函数，只要参数类型不同；

    kotlin没有位运算符，但有对应的函数(可使用中缀调用):
        shl: 带符号左移
        shr: 带符号右移
        ushr: 无符号右移
        and: 按位与
        or: 按位或
        xor: 按位异或
        inv: 按位取反

    若定义了一个返回值为Unit, 函数名为plusAssign的函数，可以使用+=调用该函数；
        类似的函数有：minusAssign, timesAssign等；
        MutableCollection中为集合定义了一些operator方法；

    复合赋值运算符：+=, -=等；

    调用函数时使用+=，plus和plusAssign都可能被调用。建议在一个类中只定义其中一个；

    +=会改变一个变量的引用。若是可变集合，则仍是操作一个集合；
 */


operator fun Point.unaryMinus() : Point{
    return Point(-x, -y)
}

operator fun Point.inc(): Point {
    return Point(x + 1, y + 1)
}

fun testOperator1() {
    Log.e("wcc", "${-Point(1, 2)}") //Point(x=-1, y=-2)

    var p = Point(1, 1)
    Log.e("wcc", "${p++}") //Point(x=1, y=1)
    Log.e("wcc", "${++p}") //Point(x=3, y=3)
}
/*
    重载一元运算符：函数没有参数。重载函数可以是成员函数，或扩展函数；

    类似运算符：+a(unaryPlus), -a(unaryMinus) !a(not)
              (++a, a++) -> inc  (--a, a--) -> dec

    inc和dec函数重载的运算符，支持前缀后缀的自增自减；

    重载一元运算符函数的返回值，必须是扩展函数接受者类型或其子类；
 */


/**
 * 7.2 重载比较运算符
 */

fun testCompare() {
    val a = "abc"
    val b = "abc"
    val c = "ab"

    val r1= a == b //重载运算符的函数equals
    val r2 = a > c //重载运算符的函数compareTo
    Log.e("wcc", "$r1 $r2")

    val result = Saler("a", "b") > Saler("a", "c")
    Log.e("wcc", "$result") //false
}

//先比较firstname，在比较lastname
class Saler(val firstname: String, val lastname: String) : Comparable<Saler> {
    override fun compareTo(other: Saler): Int {
        //可用属性引用替换lambda
        return compareValuesBy(this, other, {saler -> saler.firstname }, {saler -> lastname })
    }
}

/*
    Java中实现了Comparable接口的类，在Kotlin中可使用运算符：>, >=, <, <=
        例如："a" >= "b"等价于"a".comparaTo(b) >= 0 ;

    Operator在接口中已声明，在重写的子类方法中无需再重复；
 */

/**
 * 7.3 集合和区间的约定
 */

fun testOperator2() {
    //Map中使用下标运算符
    val map = hashMapOf(10 to "a", 20 to "b")
    val a = map[10]
    map[10] = "A"

    val p = Point(10, 20)
    Log.e("wcc", "${p[0]} ${p[1]}") //10 20

    p[0] = 11
    p[1] = 21
    Log.e("wcc", "${p[0]} ${p[1]}") //11 21
}

operator fun Point.get(index: Int): Int {
    return when (index) {
        0 -> this.x
        1 -> this.y
        else -> throw IllegalArgumentException("error")
    }
}

operator fun Point.set(index: Int, value: Int) {
    when (index) {
        0 -> this.x = value
        1 -> this.y = value
        else -> throw IllegalArgumentException("error")
    }
}

/*
    下标运算符：kotlin中的一种规定；

    通过下标运算符获取值，调用了get运算符方法；通过下标运算符修改值，调用了set运算符方法；

    方括号的访问会转化为调用get方法：x[a, b] <==> x.get(a, b)
    方括号的赋值会转化为调用set方法: x[a, b] = c <==> x.set(a, b, c)

 */

operator fun Point.contains(other: Point): Boolean {
    return this.x == other.x && this.y == other.y
}

fun testOperator3() {
    val p = Point(1, 2)
    Log.e("wcc", "${Point(1, 3) in p}") //false

    //遍历区间
    val n = 9
    (0..(n + 1)).forEach {
        Log.e("wcc", "$it")
    }

    //for in遍历
    for(i in 1..10) {

    }

    for (char in "abc") {

    }
}

/*
    in运算符：相当于调用contains函数。a in b等价于b.contains(a)；

    rangeTo运算符：相当于调用rangeTo函数。例如：1..10

    kotlin中Comprable接口定义了扩展函数rangeTo，其子类都不需要再定义rangeTo运算符函数；

    ..运算符优先级低于算术运算符，但还是建议将算数运算括起来；

    调用forEach方法遍历区间，需要将区间用括号括起来；

    for in遍历：iterator方法可被定义为扩展函数；
        区间类型ClosedRange定义了iterable扩展函数，因此可以用for in遍历区间；
        CharSequence类定义了iterable扩展函数，可以用for in遍历String；
 */

/**
 * 解构声明和组件函数
 */

//手动为非数据类的属性，提供解构声明的能力
class WhitePoint(val x: Int, val y: Int) {
    operator fun component1() = x
    operator fun component2() = y
}

data class NameComponents(val name: String, val extension: String)

fun testComponent() {
    val (x, y) = WhitePoint(1, 2)

    val str = "123.45.6"
    val (a, b) = str.split(".", limit = 2)
    Log.e("wcc", "$a $b") //123 45.6

    //解构声明在Map集合上的使用
    val map = mapOf("a" to 1, "b" to 2)

    //常规遍历Map
    for (entry in map) {
        val key = entry.key
        val value = entry.value
        Log.e("wcc", "$key $value")
    }

    //解构声明遍历
    for ((k, v) in map) {
        Log.e("wcc", "$k $v")
    }
}

/*
    解构声明：访问一个对象的多个属性，最多访问前5个。
            初始化多个变量，其实是调用componentN函数。
            val (a, b) = p 等价于 val a = p.component1(), val b = p.component2() ；

    编译器为数据类主构造方法中的属性，都生成了componentN函数；

    解构声明的使用场景：将多个数据封装到一个类里，然后使用解构声明将这个类里的属性展开；

    解构声明可以展开集合和数组中元素；

    kotlin给Map添加了一个iterator扩展函数，因此Map遍历可以用for in;
        Map.Entry的扩展函数component1和component2分别返回键和值，因此可使用解构声明获取键和值；

    扩展函数对kotlin中约定很重要!!
 */

/**
 * 7.5 重用属性访问的逻辑：委托属性
 */

//大概的代码示例
/*class Foo {
    val p: Int by Delegate()
}

class Delegate {
    operator fun getValue(...)= 1

    operator fun setValue(..., value: Int) {

    }
}*/

//传统方式完成属性的惰性初始化
class Man1(name: String) {
    private var _email: List<String>? = null

    val email: List<String>
        get() {
            if (_email == null) {
                _email = loadEmail()
            }
            return _email!!
        }

    //使用委托属性
    val email2 by lazy { loadEmail() }
}

fun loadEmail(): List<String> = listOf("123456@qq.com", "123123@qq.com")


//演示委托属性的实现方式

//1，定义一个实体类的基类
open class PropertyChangeAware {
    protected val changeSupport =  PropertyChangeSupport(this)

    fun addPropertyChangedListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
    }

    fun removePropertyChangedListener(listener: PropertyChangeListener) {
        changeSupport.removePropertyChangeListener(listener)
    }
}

class Woman(val name: String, age: Int) : PropertyChangeAware() {

    var age = age
        set(value) {
            val oldValue = age
            field = value
            changeSupport.firePropertyChange("age", oldValue, field)
        }
}

fun testProperty() {
    val w = Woman("jane", 21)
    w.addPropertyChangedListener(PropertyChangeListener {
        //age  21 18
        evt -> Log.e("wcc", "${evt.propertyName}  ${evt.oldValue} ${evt.newValue}")
    })
    w.age = 18
}

//2，将访问属性的操作封装到一个类中
class ObservableProperty(val propName: String, var propValue: Int, val changeSupport: PropertyChangeSupport) {
    fun getValue() : Int = propValue

    fun setValue(newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        changeSupport.firePropertyChange(propName, oldValue, newValue)
    }
}

class Woman1(val name: String, age: Int) : PropertyChangeAware() {
    val instance = ObservableProperty("age", age, changeSupport)
    var age: Int
        get() = instance.getValue()
        set(value) {
            instance.setValue(value)
        }
}

fun testProperty1() {
    val w = Woman1("jane", 24)
    w.addPropertyChangedListener(PropertyChangeListener {
        //age  24 16
        evt -> Log.e("wcc", "${evt.propertyName}  ${evt.oldValue} ${evt.newValue}")
    })
    w.age = 16
}

//3,委托属性需要使用Kotlin规定的样本代码，修改封装属性访问器的类：
class ObservableProperty1(var propValue: Int, val changeSupport: PropertyChangeSupport) {
    operator fun getValue(p: Woman2, prop: KProperty<*>) = propValue

    operator fun setValue(p: Woman2,  prop: KProperty<*>, newValue: Int) {
        val oldValue = propValue
        changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }
}

//使用委托属性，不需要定义访问器的封装类
class Woman2(val name: String, age: Int) : PropertyChangeAware() {
    var age: Int by ObservableProperty1(age, changeSupport)
}

//4,终极版：使用Kotlin标准库，实现可观察的属性
class Woman3(val name: String, age: Int) : PropertyChangeAware() {
    var age: Int by Delegates.observable(age){
        property, oldValue, newValue ->  changeSupport.firePropertyChange(property.name, oldValue, newValue)
    }
}


/*
    委托属性：就是将属性的访问器交给了一个对象。
        该对象必须提供getValue，setValue方法(var变量)；

    lazy函数：有一个getValue方法，用它和by关键字创建委托属性；

    by右边的表达式，不一定是新建的实例，也可能是函数调用，属性或其他表达式。
        但，需保证右边是一个可调用getValue，setValue的对象；

    委托属性适用于任何类型的属性；
 */

class Example {
    private val container = hashMapOf<String, String>()

    fun setAttribute(key: String, value: String) {
        container[key] = value
    }

    val name: String
        get() = container["name"]!!
}

//使用委托属性修改后
class Example1 {
    private val container = hashMapOf<String, String>()

    fun setAttribute(key: String, value: String) {
        container[key] = value
    }

    //变量名称name必须是Map中的key
    val name: String by container
}

fun testExample() {
    val e = Example()
    for ((key, value) in hashMapOf("name" to "tom", "age" to "20")) {
        e.setAttribute(key, value)
    }
    Log.e("wcc", e.name) //tom

    val e1 = Example1()
    for ((key, value) in hashMapOf("name" to "tom1", "age" to "21")) {
        e1.setAttribute(key, value)
    }
    Log.e("wcc", e1.name) //tom1
}


/*
    委托属性使用场景：
        自定义属性的存储位置(eg：Map)
        访问属性时做点什么(eg：属性改变后发出通知)

    Map和MutableMap定义了getValue和setValue的扩展函数。
 */


















