package com.example.kotlin2020

import android.util.Log
import android.view.View
import android.widget.TextView

//寻找集合中最大的元素
fun findTheOldest(people: List<Person>) {
    var oldestPerson: Person? = null
    var maxName: String = ""

    for (element in people) {
        if (element.name.compareTo(maxName) > 0) {
            maxName = element.name
            oldestPerson = element
        }
    }
    if (oldestPerson != null) {
        Log.e("wcc", oldestPerson.name + " " + oldestPerson.isMale)
    }
}

fun testLambda() {
    val list = arrayListOf(Person("bce", false), Person("bcd", false))
    findTheOldest(list)


    //Kotlin库函数替换上述代码
    val list1  = arrayListOf(Person("bce", true), Person("bcd", false))
    val person = list1.maxBy { it.name }
    if (person != null) {
        Log.e("wcc", "${person.name} ${person.isMale}")
    }
}

/*
    { it.name }就是lambda表达式；

    若lambda是函数或属性的委托，则可以用成员引用替换；

    Java集合的大多数处理，可以用lambda或成员引用的库函数替换，而无需自己实现；
 */

fun testLambda1() {
    val sum = {x: Int, y: Int -> x + y}
    Log.e("wcc", sum(1, 2).toString())

    run { Log.e("wcc", "run...") }


    val list = arrayListOf(Person("bce", false), Person("bcd", false))
    //使用lambda不简化的情况：
    list.maxBy({ p: Person -> p.name })

    //将lambda放到函数括号的外面
    list.maxBy() { p: Person -> p.name }

    //去掉函数的空括号
    list.maxBy { p: Person -> p.name }

    //lambda参数类型可以推导出来，省略后：
    list.maxBy {p -> p.name}

    //使用默认参数名称it，简化后：
    list.maxBy { it.name }

    //lambda由语句构成，最后一个表达式就是lambda的结果
    val a = {x: Int, y: Int ->
        x + y
        x * y
    }
}

/*
    lambda：把一小段行为进行编码，可以把它当做值传递；

    lambda声明可以存储在一个变量中，或传递给一个函数的参数；

    lambda语法：{ x: Int, y: Int -> x + y }
        lambda始终在花括号内，前半部分是参数，后半部分是函数体；
        与Java不同的是，实参列表没有用括号括起来；

    若lambda存储到变量中，可以将变量名当做函数使用。函数的参数为lambda的参数列表，函数体为lambda的函数体；

    若需要将一段逻辑封装到lambda中(无意义)，可使用库函数run执行传给它的lambda;

    若lambda表达式是函数的最后一个参数，可以将lambda放到函数括号的外面。
        再次基础上，若是唯一参数，可以去掉空括号；

    若lambda的参数类型可以被推导出来，就不需要显示的指定它；
        建议：先不写参数类型，等编译器报错再指定；
             若显示参数类型能增强可读性，指定也是可以的；

    若函数只有一个lambda参数，且lambda参数类型可以推导出来，会生成一个默认参数名称it;

    建议：若出现lambda嵌套的情况，显示声明每个lambda参数的可读性更高。默认参数it切不可滥用，代码是给人读的；

    用变量存储lambda，就没有可推导的上下文，需要显示声明lambda参数类型；
 */

fun printMessagesWithPrefix(msg: Collection<String>, prefix: String) {
    msg.forEach {
        Log.e("wcc", "$prefix $it")
    }
}

fun printProblemCounts(responses: Collection<String>) {
    var clientErrors = 0
    var serverErrors = 0

    responses.forEach {
        if (it.startsWith("4")) {
            clientErrors++
        } else if (it.startsWith("5")) {
            serverErrors++
        }
    }

    Log.e("wcc", "clientErrors = $clientErrors serverErrors = $serverErrors")
}

fun testLambda2() {
    printMessagesWithPrefix(arrayListOf("tom", "jake"), "nice")

    printProblemCounts(arrayListOf("404", "200", "503"))

}


/*
    forEach的参数是一个lambda，可以用于遍历集合。虽然代码比for语句更简化，但性能没有其他优势；

    lambda内访问外部的变量，称这些变量被lambda捕捉；

    当lambda捕捉final变量时，它的值和使用这个值的lambda代码一起存储；
    当lambda捕捉非final变量时，先用一个包装器封装变量的值，再将包装器引用和lambda代码一起存储；
 */


fun testRef() {
    val list = arrayListOf(Person("tome", true), Person("jake", false))
    list.maxBy {person: Person -> person.name}
    //成员引用简化
    list.maxBy(Person::name)

    run(:: testLambda1)

    val action = {msg: String, p: Person -> sendEmail(msg, p)}
    //使用方法引用简化后
    val action1 = ::sendEmail

    val createPerson = { name: String, isMale: Boolean -> Person(name, isMale) }
    //构造方法引用简化后
    val createPerson1= ::Person
    val p = createPerson("tom", false)


    val e = { s: String -> s.lastCharater() }
    val e1 = String::lastCharater

}

fun sendEmail(msg: String, person: Person) {

}

fun String.lastCharater() : Char {
    return 'a'
}


/*
    成员引用语法：类::成员(成员可以方法，可以是属性)
        Kotlin和Java8一样，把函数转换成一个值就可以传递它；
        成员引用创建了一个调用单个方法或访问单个属性的函数值；
        成员引用和lambda表达式有一样的类型，可以互相转换；

    成员引用可以引用顶层函数，格式 ::顶层函数名

    将lambda的参数列表，刚好委托给某一个函数的参数列表，则可使用方法引用；

    构造方法引用：存储或延迟创建类实例；

    成员引用适用于扩展函数，格式：类名::扩展函数名，尽管扩展函数不是类的真正的成员；
 */

data class Machine(val name: String, var age: Int)

fun testFilterAndMap() {
    val list = arrayListOf(Machine("made in china", 10), Machine("made in USA", 20))
    val listFilter = list.filter { it.age > 15 }
    Log.e("wcc", listFilter.toString()) // [Machine(name=made in USA, age=20)]

    val listMap = list.map { it.age + 1 }
    Log.e("wcc", listMap.toString()) // [11, 21]

    val bothList = list.filter { it.age > 15 }.map { machine ->
        machine.age += 10
        machine
    }
    Log.e("wcc", bothList.toString()) // [Machine(name=made in USA, age=30)]

    val map = hashMapOf("a" to 1, "c" to 3)
    Log.e("wcc", map.filterKeys { it > "b" }.toString()) // {c=3}
    Log.e("wcc", map.filterValues { it > 1 }.toString()) // {c=3}
    Log.e("wcc", map.mapKeys { it.key + "z" }.toString()) // {az=1, cz=3}
    Log.e("wcc", map.mapValues { it.value + 10 }.toString()) // {a=11, c=13}
}

/*
    filter函数：可以从集合中移除不需要的元素，返回true的元素被留下。函数返回存储了元素的集合；

    map函数：对集合中的元素执行一些操作并把结果收集到新的集合。函数返回lambda返回值的集合；

    filter, Map可以链式调用。集合中的元素都会执行一遍lambda的代码，需要避免不必要的重复操作；

    Map相关的函数：filterKeys: 根据key过滤；
                 filterValues: 根据value过滤；
                 mapKeys: 修改key；
                 mapValues: 修改value；
 */

fun testjudge() {
    val list = arrayListOf(Machine("made in china", 10), Machine("made in USA", 20))

    //是否所有元素age>=10
    val a = list.all { it.age >= 10 } // true

    //是否有元素age<15
    val b = list.any { it.age < 15 } // true

    //元素age>=10的个数
    val c = list.count { it.age >= 10 } // 2

    val machine = list.find { it.age >= 10 } // Machine(name=made in china, age=10)

    Log.e("wcc", "$a $b $c $machine")
}

/*
    all函数：集合中所有元素满足某一条件吗？
    any函数：集合中有元素满足某一条件吗？
    count函数：集合中元素满足某一条件的个数
    find函数：返回集合中满足某一条件的元素。若有，则返回第一个；若没有，返回null;
 */

fun testGroupBy() {
    val list = arrayListOf(
        Machine("made in china", 10),
        Machine("made in USA", 20),
        Machine("made in England", 20))

    //年龄相同的人分组
    val map = list.groupBy { it.age}
    map.forEach { Log.e("wcc", "${it.key}  ${it.value}") }

    /*
        log如下：
                10  [Machine(name=made in china, age=10)]
                20  [Machine(name=made in USA, age=20), Machine(name=made in England, age=20)]
     */
}

/*
    groupBy函数：将集合元素按照某一标准进行分组。
        返回值：Map类型。key是lambda函数体的返回值，value是同一组元素的集合；
 */

fun testFlatmapAndFlatten() {
    val strs = listOf("abc", "def")
    //lambda返回类型是List
    Log.e("wcc", strs.flatMap { it.toList() }.toString()) // [a, b, c, d, e, f]

    val f = listOf(listOf(1, 2), listOf(3, 4))
    Log.e("wcc", f.flatten().toString()) // [1, 2, 3, 4]
}

/*
    CharSequence.toList函数：将字符串转化为字符列表List<Char>

    flatMap函数：先将原集合中元素根据一定逻辑转化为一个集合，然后将多个集合合并为一个集合;

    flatten函数：没有flatMap第一步的变换，只是将多个集合中元素合并到一个集合。它是Iterable<Iterable<T>>的扩展函数；
 */

fun testSequence() {
    val list = arrayListOf(Machine("made in china", 10), Machine("made in USA", 20))

    //这行代码创建了两个集合来存储map和filter的返回值
    val a = list.map { it.name }.filter { it.startsWith("made in") }

    val b = list.asSequence()
        .map { it.name }
        .filter { it.startsWith("made in") }
        .toList() //返回一个集合
    Log.e("wcc", b.toString()) // [made in china, made in USA]

    //序列只会处理1，2，不会处理3， 4
    val c = listOf(1, 2, 3, 4)
        .asSequence()
        .map { it * it }
        .find { it > 3 } //返回一个元素


    //另一种创建序列的方式
    val d = generateSequence(0){ it + 1 }
        .takeWhile { it <= 100 }
        .sum() //返回一个数字
    Log.e("wcc", d.toString()) //5050
}

/*
    普通的链式调用，需要创建一些额外的集合。若集合元素个数是百万级，则需要开辟较大的内存；

    asSequence函数：将集合转化为序列；
    toList函数：将序列转化为集合

    序列和集合的API一样，序列中元素求值是惰性的；

    序列不能完全替换集合。比如：用下标访问元素，需要将序列转化为集合；

    序列的函数变换不会立即执行，只有在获取结果时才会被应用。比如调用toList方法；

    序列的执行结果有三种情况：集合t，元素，数字(例如：toLis, find, sum)

    集合和序列执行区别？
        集合：使用map操作所有元素并返回一个集合，然后用filter操作生成的集合；
        序列：对一个元素执行完所有操作(map, filter)后，再处理下一个元素；

    序列操作函数的顺序，也会影响性能；
 */

fun testSAM(name: String) {
    val tv = TextView(null)

    //显示创建一个lambda匿名对象
    tv.setOnClickListener(object : View.OnClickListener {
        override fun onClick(v: View?) {

        }
    })

    //1
    tv.setOnClickListener{view ->
        print(name)
    }

    //SAM构造方法
    tv.setOnClickListener(View.OnClickListener {

    })
}

fun createRunnable() : Runnable {
    //SAM构造方法
    val a = Runnable {

    }


    //SAM构造方法
    return Runnable {

    }
}

//1处lambda生成了匿名类TestSAM$1
/*class TestSAM$1(val name: String) : OnClickListener {
    override fun onClick(v: View?) {
        print(name)
    }
}*/


/*
    函数式接口：只有一个抽象方法的接口，或SAM接口(单抽象方法)；

    Java中函数式接口参数，可以用lambda替换。
        若lambda没有引用局部变量，匿名类实例可以重用；否则，每次调用会创建新的匿名类实例。
        若显示创建匿名类实例，每次调用会创建新的匿名类实例。因此lambda有时候性能更好；

    kotlin1.0起，对于Java中函数式接口，lambda都会创建一个匿名类，但并不适用于集合使用kotlin扩展方法；

    函数式接口参数可以通过上下文推断参数类型。若函数的返回值或变量是一个函数式接口，没有上下文可推断，则需要用SAM构造方法；

    SAM构造方法格式：接口名 {}

    lambda中没有代表匿名类实例的this，在lambda中使用this表示其所在类的实例。
        若需要表示匿名类的实例，可以用实现了接口的匿名对象；

    若多个重载方法的参数都是函数式接口，则需要使用SAM构造方法指定接口名；
 */

fun alphabet(): String {
    val sb = StringBuilder()
    return with(sb, {
        for (element in 'A'..'Z') {
            this.append(element)
        }
        this.toString()
    })
}

//(lambda放到外面，省略this，表达式函数体)简化后：
fun alphabet1() = with(StringBuilder()) {
    for (element in 'A'..'Z') {
        append(element)
    }
    toString()
}

//使用apply函数实现上述逻辑
fun alphabet2() = StringBuilder().apply {
    for (element in 'A'..'Z') {
        append(element)
    }
}.toString()

fun testApply() {
    TextView(null).apply {
        this.text = "我的"
        textSize = 16f
        setPadding(0,0,0,0)
    }
}

fun testBuildString() = buildString {
    for (element in 'A'..'Z') {
        append(element)
    }
}


/*
    with函数：接受两个参数的函数。第一个参数传入类实例，第二个参数是lambda；
        第二个参数叫作带接受者的lambda，this指向第一个参数sb(this可省略)；

    with函数使用场景：避免多次重复使用变量名sb；

    若希望lambda中toString方法是外部类的方法，可以使用this@OuterClass.toString();

    with函数返回的是lambdad的结果，若需要返回接受者对象，可以用apply函数；

    任何对象都可以调用apply函数。其中一个使用场景：创建一个类实例，并初始化它的一些属性；

    with和apply函数是最基本、最通用带有接受者的lambda。
        buildString函数会更复杂点，它会创建StringBuilder对象并调用toString;

    buildString内部实现：StringBuilder().apply(builderAction).toString()
 */





















