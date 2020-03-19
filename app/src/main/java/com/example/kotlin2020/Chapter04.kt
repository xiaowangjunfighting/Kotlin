package com.example.kotlin2020

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import java.io.File
import java.io.Serializable

/**
 * 4.1 定义类继承结构
 * */

interface Clickable {
    fun click()

    fun showOff() {
        Log.e("wcc", "I'm clickable")
    }
}

interface Focusable {
    fun setFocus(b: Boolean) {
        Log.e("wcc", "I ${if (b) "got" else "lost"}")
    }

    fun showOff() {
        Log.e("wcc", "I'm focusable")
    }
}

class Button : Clickable, Focusable {
    override fun click() {
        Log.e("wcc", "I was clicked")
    }

    override fun showOff() {
        //相当于Java中Clickable.super.showOff()
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }

    //若只需要调用一个继承的实现，可以这样写：
//    override fun showOff() = super<Clickable>.showOff()
}

fun test4_1() {
    val button = Button()
    button.click()
    button.setFocus(true)
    button.showOff()
}

/*
    关键字interface定义一个接口；

    :代替Java中的extends, implements;

    与Java8一样，接口的方法可以有默认的实现，但不需要default修饰;

    一个子类有多个接口，且接口的默认方法相同，则子类必须重写showOff方法;

    由于kotlin1.0是以Java6为目标设计，因此java类实现kotlin中有默认方法的接口，需要重写自己的默认方法showOff；
* */


open class RichButton : Clickable {
    //子类可以重写
    override fun click() {

    }

    //子类不能重写
//    final override fun click() {
//
//    }

    //子类不能重写
    fun disable() {

    }

    //子类可以重写
    open fun ainimate() {

    }
}

abstract class Animated {
    abstract fun animated()

    //子类可以重写
    open fun stopAnimating() {

    }

    //子类不可以重写
    fun animateTwice() {

    }
}

/*
    Kotlin中的类和方法，默认都是final的。若需要一个类可派生，一个方法或属性可重写，需要添加关键字open;

    若重写了一个基类或接口的成员，则成员默认是open，可以添加final关键字修改；

    默认为final的好处：方便使用智能转换；

    与Java一样，抽象类和抽象方法用abstract关键字；

    抽象类中的抽象成员默认是open，非抽象的成员默认是final；

    接口中的成员始终是open，不能被修改为final，否则编译报错；
 */


internal open class TalkativeButton : Focusable {
    private fun yell() {

    }

    protected fun whisper() {

    }
}

/*
fun TalkativeButton.giveSpeech() { //error
    yell() //error

    whisper() //error
}
*/

/*
    kotlin中可见性分为四种：private, protected, internal, public(默认)，没有包私有；

    internal：模块内可见；

    protected：成员只有本类或其子类中可访问。与Java不同的是，kotlin的同一个包下，也不能访问protected成员；

    private: 允许顶层声明中使用，包括类，函数，属性。只能在该文件中访问这些顶层声明；

    Kotlin中的外部类，不能访问内部类的private成员;

    kotlin不允许扩展函数的接受者类型，比扩展函数可见性更低;

    扩展函数无法访问接受者类型的private, protected成员；
 */


interface State : Serializable

interface View {
    fun getCurrentState(): State

    fun restoreState(state: State){}

}

class Button1 : View {

    //相当于java中内部类添加了static
    class ButtonState : State {

    }

    inner class ButtonState1 : State {
        fun getOuterReference(): Button1 = this@Button1
    }

    override fun getCurrentState(): State = ButtonState()

    override fun restoreState(state: State) {

    }
}

/*
    Kotlin中内部类，默认不持有外部类的引用，除非加上关键字inner;

    inner内部类中，用this@OuterClass表示外部类的实例；
 */


sealed class Expr1 {
    class Num(val value: Int) : Expr1()

    class Sum(val left: Expr1, val right: Expr1): Expr1()
}

fun eval(e: Expr1): Int =
    when (e) {
        is Expr1.Num -> e.value
        is Expr1.Sum -> eval(e.left) + eval(e.right)
    }

/*
    密封类：用关键字sealed修饰。kotlin1.0版本，密封类的子类必须在其内部；在1.1+版本只需保证密封类及其子类在一个文件中；

    密封类的好处：若when子句分支中，列出了所有子类的情况，则不需要在最后添加else分支；
 */


/**
 * 4.2 声明一个带非默认构造方法或属性的类
 */

class User1(val nickname: String)


//研究简化的主构造方法的工作过程
class User2 constructor(nickname: String) {
    val nickname: String

    init {
        this.nickname = nickname
    }

    init {

    }
}

//如果主构造方法没有可见性修饰符和注解，那么constructor可以省略
class User3(nickname: String) {
    //没有特别复杂的逻辑，可以去掉init语句块
    val _nickname = nickname
}

//若属性用构造方法的参数来初始化，则可以替换类中的属性定义
class User4(val nickname: String)


class Student(val name: String = "tom", val age: Int = 20)

fun testClass() {
    val stu = Student(name = "tom1", age = 21)
    val stu1 = Student(age = 21)

    //编译器会生成一个不带参数的构造方法，来使用所有的默认值；
    val stu2 = Student()
}


open class Father(val name: String)

//Son的name参数不要加val，已经从父类继承了name,无需再定义name属性
class Son(name: String, val age: Int) : Father(name)


class Teacher private constructor()

/*
    (val nickname: String)：括号里的内容叫主构造方法；

    constructor关键字：用于开始一个主构造方法或从构造方法的声明；

    init关键字：引入一个初始化语句块，用于和主构造方法一起使用。一个类中可以使用多个init语句；

    init语句在类创建时执行，由于主构造方法不能包含初始化代码，因此需要init语句配合；

    构造方法和普通函数的声明一样，可以为参数设置一个默认值；

    若没有给类声明任何的构造方法，则会生成一个默认的空参构造方法，子类继承Father时仍需要加上();

    主构造方法确定了两件事：类中定义了相应的属性，构造方法有对应的参数并初始化了类中属性；

    若需要声明私有的构造方法，则不能省略constructor，且需在前面加上private；
 */


open class View1 {
    constructor(cxt: Context) {

    }

    constructor(cxt: Context, attr: AttributeSet?) {

    }
}


class MyButton : View1 {

    //委托给本类中两个参数构造方法
    constructor(cxt: Context) : this(cxt, null) {

    }

    //调用父类的构造方法
    constructor(cxt: Context, attr: AttributeSet?) : super(cxt, attr) {

    }
}

/*
    从构造方法：在类中用constructor定义从构造方法；

    从构造方法使用场景：Java构造方法互操作性的情况下使用；

    kotlin中构造方法也需要初始化基类
 */



interface Garden {
    //声明一个抽象属性
    val nickname: String

    //该属性可以被子类继承，不是必须重写
    val age: Int
        get() = 1
}

//在主构造方法中声明属性
class PrivateUser(override val nickname: String) : Garden {

}

//nickname属性通过自定义getter实现
class PrivateUser1 : Garden {
    override val nickname: String
        get() {
            return "a"
        }
}

//初始化时将属性和值关联
class PrivateUser2 : Garden {
    override val nickname: String = "a"

    var address: String = "unspecified"
        set(value: String) {
            Log.e("wcc", field) //unspecified
            field = value
        }

    var email: String = "123456@qq.com"
        private set //修改属性访问器的可见性
}

fun testField() {
    val p = PrivateUser2()
    p.address = "abc"

    //编译报错，email的set方法是私有的
//    p.email = ""
}

/*
    set方法中使用了特殊标识符field，访问支持字段的值；

    有支持字段的属性和没有的区别?
        若显示的引用或使用默认的访问器实现，编译器会为属性生成一个支持字段；
        若是自定义的访问器且没有使用field，支持字段就不会呈现出来；

    属性的访问器的可见性和属性相同；

    修改属性访问器的可见性，例如：在set前加上private;
 */


/**
 * 4.3 编译器生成的方法：数据类和类委托
 */


class Client(val name: String, val code: Int) {

    override fun toString(): String {
        return "$name  $code"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Client) {
            return false
        }

        return this.name == other.name && this.code == other.code
    }

    //HashSet$contains方法会比较hash值，然后才判断对象是否相等
    override fun hashCode(): Int {
        return name.hashCode() * 31 + code
    }
}

data class Client1(val name: String, val code: Int)

fun testClient1() {
    val c1 = Client1("tom", 123)
    val c2 = Client1("tom", 123)
    val result = c1 == c2

    val c3 = c1.copy("jake", 456)
}

/*
    Kotlin中==相当于Java中equals，kotlin中===相当于java中== ；

    ==编译后会调用equals，比较的是对象，而非引用；

    Kotlin中Any相当于Java中Object，后面的?表示可为null；

    在声明类时添加修饰符data，编译器会自动生成一些通用方法的实现，如：toString, hashCode, equals。
        hashCode, equals只会将主构造方法中的所有属性纳入考虑范围；
        equals检测所有属性值是否相等，hashCode返回一个根据所有属性生成的哈希值；

    编译器还为数据类生成了一个copy方法，返回一个对象的副本；

    强烈建议数据类的属性是val，而非var。
        因为这样的实例可能作为HashMap等容器的key，若key在放入容器后被修改，会导致容器无效；
 */


//Collection接口的装饰器
class DelegatingCollection<T> : Collection<T> {
    private val innerList = arrayListOf<T>()

    override val size: Int get() = innerList.size

    override fun isEmpty(): Boolean = innerList.isEmpty()

    override fun contains(element: T): Boolean = innerList.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = innerList.containsAll(elements)

    override fun iterator(): Iterator<T> = innerList.iterator()
}

//推荐写法，效果和上面装饰器一样
class DelegatingCollection1<T> (val innerList: Collection<T> = ArrayList()) : Collection<T> by innerList {
    override fun isEmpty(): Boolean = true
}

class DelegatingCollection2<T> () : Collection<T> by ArrayList<T>()

fun testInnerList() {
    val list = arrayListOf("a")
    val instance = DelegatingCollection1(list)

    Log.e("wcc", "${instance.isEmpty()}") //true，因为isEmpty重写了
    Log.e("wcc", "${instance.size}") //1
}

/*
    by关键字：可以实现接口的装饰器，编译器会自动生成代码；

    若某些行为需要自定义，可以在类中重写方法，例如：isEmpty方法；
 */



/**
 * 4.4 "object"关键字：将声明一个类和创建一个实例结合起来
 */

object Pay : Comparator<File>{
    val a: Int = 1

    fun salary() {

    }

    init {

    }

    override fun compare(f1: File, f2: File): Int {
        return f1.path.compareTo(f2.path, ignoreCase = true)
    }
}

fun testObject() {
    //使用对象声明的变量
    val a = Pay.a
    //使用对象声明的方法
    Pay.salary()

}

class Person1 {
    object Student {

    }
}

/*
    object关键字：声明一个类及该类的单例对象；

    与普通的类一样，对象声明也包含属性，方法，初始化语句等声明；
        但，不需要构造方法(包括主构造方法和从构造方法)。因为在类声明的时候已经创建了实例，不需要其他代码调用构造方法；

    对象声明可以继承类或实现接口；

    可以在任何使用普通对象的地方，使用对象声明；

    对象声明可以嵌套在类中；

    对象声明被编译成了静态字段INSTANCE，在java中使用单例实例的方式：Pay.INSTANCE
 */


class A {
    private val a: Int = 1

    companion object {
        fun bar() {
            print(a)
        }
    }
}

fun testCompanion() {
    //调用方式看起来和Java静态方法一样
    A.bar()
}


//声明拥有多个从构造方法的类
class Using {
    constructor(email: String) {

    }

    constructor(id: Int) {

    }
}

//实现上述逻辑，用伴生对象实现工厂方法来替换
class Using1 private constructor() {
    companion object {
        fun newEmailInstance(email: String) : Using1{
            //访问私有主构造方法
            return Using1()
        }

        fun newIdInstance(id: Int): Using1 {
            //访问私有主构造方法
            return Using1()
        }
    }
}

/*
    顶层函数不能访问类中的私有成员，可以使用伴生对象解决；

    companion object{}:声明伴生对象；

    伴生对象可以访问类中的私有成员，包括私有主构造方法；
 */


class Animal(val name: String) {
    companion object Loader {
        fun fromJson(json: String): Animal {
            return Animal(json)
        }
    }

}

fun testAnimal() {
    Animal.Loader.fromJson("cat")

    Animal.fromJson("cat")
}


interface JSONFactory<T> {
    fun fromJson(json: String) : T
}

class Cat(val name: String) {
    //伴生对象实现接口
    companion object : JSONFactory<Cat> {

        //重写接口的方法
        override fun fromJson(json: String): Cat {
            //伴生对象外面的类名，作为伴生对象的实例
            load(Cat)

            return Cat("cat")
        }
    }
}

fun <T> load(jFactory: JSONFactory<T>) : T {
    TODO()

    //调用伴生对象的扩展函数
    Cat.test()
}

fun Cat.Companion.test() {

}

/*
    伴生对象可以指定对象名，调用方式：类名.伴生对象名.方法 ;

    若伴生对象不指定对象名，默认名字为Companion；

    与对象声明一样，伴生对象可以继承类或实现接口；

    kotlin中没有static关键字，但Kotlin中调用Java的静态属性和方法，与Java的方式一样；

    伴生对象外面的类名，作为伴生对象的实例使用；

    Java中调用Kotlin中伴生对象的方式：Cat.Companion.fromJson("")

    伴生对象可以定义扩展函数，这个扩展函数"看起来就像"伴生对象里的方法一样；
 */


fun testObi() {
    //just for test
    val tv: TextView = TextView(null)

    var a = 0

    tv.setOnClickListener(object : android.view.View.OnClickListener {
        override fun onClick(v: android.view.View?) {
            a = 1
        }
    })
}


/*
    object还可用于对象表达式，也就是java中的匿名内部类

    对象表达式中object用法，与对象声明极为相似，只是没有对象名；

    与Java不同，Kotlin中匿名对象可以实现多个接口或不实现接口；

    与Java不同，对象表达式可以访问不是final的局部变量，且可以改变变量的值；
 */

































