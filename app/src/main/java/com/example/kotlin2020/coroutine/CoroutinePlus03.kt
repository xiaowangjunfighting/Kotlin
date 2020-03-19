package com.example.kotlin2020.coroutine

import android.view.GestureDetector
import com.example.kotlin2020.kotlinplus.logE
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.math.log
import kotlin.system.measureTimeMillis

/**
 * 异步流
 */
class CoroutinePlus03 {

    //挂起函数
    suspend fun foo() : List<Int>{
        logE("${currentThread()}")
        delay(1500) //模拟耗时
        return listOf(1, 2, 3)
    }

    fun testCoroutine() {
        runBlocking {
            foo().forEach {
                logE("$it")
            }
            logE("${currentThread()}")
        }
    }
    /*
        一秒后打印集合
    */


    //Flow(流)
    fun foo1(): Flow<Int> = flow {
        logE("flow start")
        for (i in 0 until 5) {
            delay(500) //模拟异步操作
            this.emit(i) //发射值
        }
    }

    fun testCoroutine1() {
        runBlocking {
            launch {
                logE("launch")
                delay(3000)
            }

            foo1().collect {
                logE("$it") //收集值
            }
        }
    }
    /*
        注意：foo1不是挂起函数；
        FlowCollector$emit发射值，Flow$collect收集值，类似于RxJava；
     */

    //流是冷的
    fun testCoroutine2() {
        runBlocking {
            val flow = foo1()
            logE("start")
            flow.collect {
                logE("$it")
            }
            logE("end")
        }
    }
    /*
        打印结果：start
                flow start
                0
                1
                2
                3
                4
                end

        若没有调用collect方法开始收集，foo方法中流不会启动；
     */


    //流取消
    fun foo2(): Flow<Int> = flow {
        for (i in 0 until 3) {
//            delay(500)
            Thread.sleep(500)
            logE("emmit: $i")
            emit(i)
        }
    }

    fun testCoroutine3() {
        runBlocking {
            withTimeoutOrNull(1300) {
                foo2().collect{logE("collet: $it")}
            }
            logE("end")
        }
    }
    /*
        调用挂起函数delay打印结果：
            emmit: 0
            collet: 0
            emmit: 1
            collet: 1
            end

        调用挂起函数Thread.sleep打印结果：
            emmit: 0
            collet: 0
            emmit: 1
            collet: 1
            emmit: 2
            collet: 2
            end
        withTimeoutOrNull方法可以给协程里代码设置超时;
            若想取消流，则需要保证流中调用了挂起函数，例如delay;
     */


    //流的其他构造方式
    fun testCoroutine4() {
        runBlocking {
            listOf(1, 2).asFlow().collect { logE("list: $it")}

            sequence {
                yield(3)
                yield(4)
            }.asFlow().collect { logE("sequence: $it")}

            (5 until 6).asFlow().collect { logE("IntRange: $it") }

            arrayOf(6, 7).asFlow().collect { logE("array: $it") }

            flowOf(8,9,10).collect { logE("flowof: $it") }
        }
    }
    /*
        区间，集合，序列，数据，都可以通过asFlow方法转化为流。类似RxJava的Observable$just
        asFlow方法的输入参数是一个可变参数类型，也可以创建流。类似RxJava的Observable$fromIterable

     */

    //操作符map
    fun testCoroutine5() {
        runBlocking {
            listOf(1, 2).asFlow()
                .map {i -> foo3(i)}
                .collect { logE("$it")}
        }
    }

    suspend fun foo3(i: Int) : String{
        delay(1000) //模拟耗时操作
        return "${i + 1}"
    }
    /*
        流的操作符是冷启动的，可以调用挂起函数；
     */


    //操作符transform, take
    fun testCoroutine6() {
        runBlocking {
            listOf(1, 2, 3).asFlow()
                .take(2)
                .filter { true }
                .transform { request ->
                    this.emit(performRequest(request))
                }
                .collect { logE("$it")}
        }
    }

    suspend fun performRequest(request: Int) : String{
        delay(1000)
        return "repsonse $request"
    }

    /*
        transform操作符可以模拟map，filter，以及做一些复杂的变换操作；
        take(n)操作符：只取前n个发射的值；
        filter：过滤发射的值
     */

    //末端操作符：collect, first, single, reduce, toList, toSet
    fun testCoroutine7() {
        runBlocking {
            val result = listOf(1, 2, 3, 4, 5).asFlow()
//                .map { it + 1 }
//                .first() //2

//                .take(1)
//                .single()

                .reduce { accumulator, value ->
                    logE("accumulator: $accumulator    value: $value")
                    accumulator + value //15 (求和)
                }
            logE("result: $result")
        }
    }
    /*
        first：返回第一个值
        single：上游只能发射一个值(否则报异常)，并返回这个值;

     */


    //flowOn更改流发射的上下文
    fun testCoroutine8() {
        runBlocking {
            flow {
                for (i in 0 until 3) {
                    Thread.sleep(1000)
                    emit(i)
                    logE("${currentThread()}  emit: $i")
                }
            }
                .flowOn(Dispatchers.IO)
                .collect {
                logE("${currentThread()}:  collect: $it")
            }
        }
    }
    /*
        打印结果：
            DefaultDispatcher-worker-2  emit: 0
            main:  collect: 0
            DefaultDispatcher-worker-2  emit: 1
            main:  collect: 1
            DefaultDispatcher-worker-2  emit: 2
            main:  collect: 2
     */

    //buffer
    fun testCoroutine9() {
        runBlocking {
            val time = measureTimeMillis {
                flow {
                    for (i in 0 until 3) {
                        delay(1000)
                        emit(i)
                        logE("emmit: $i")
                    }
                }
                    .buffer()
                    .collect {
                        delay(2000)
                        logE("collect: $it")
                    }
            }
            logE("cost time: $time")
        }
    }
    /*
        上游发射数据延迟1秒，下游接受数据延迟2秒，总共耗时9028ms;
        若添加buffer方法，耗时7074ms（并发运行发射元素的代码）；
     */

    //流异常
    fun testException() {
        runBlocking {
            try {
                flow {
                    for (i in 1..3) {
                        emit(i)
                    }
                }.collect {
                    logE("$it")
                    check(it <= 1) //大于1抛出异常
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    /*
        i为2时抛出异常，flow不再发射值；
     */


    //异常透明性
    fun testCatch() {
        runBlocking {
            flow {
                for (i in 1..3) {
                    emit(i)
                }
            }
            .map{value ->
                check(value <= 1)
                "string $value"
            }
            .catch { cause ->
                this.emit("$cause")
            }
            .collect {
                logE("$it")
            }
        }
    }
    /*
        打印结果：
            string 1
            java.lang.IllegalStateException: Check failed.

        抛出异常后，Flow不再发射流；
     */


    //onCompletion
    fun testOnCompletion() {
        runBlocking {
            listOf(1, 2, 3).asFlow()
                .onCompletion { cause ->
                    logE("done")
                }
                .collect {
                    logE("$it")
                }
        }
    }
    /*
        打印结果：
            1
            2
            3
            done

        onCompletion：流处理完成后调用
     */

    fun testOnCompletionAndCatch() {
        runBlocking {
            flow {
                emit(1)
                emit(4)
                check(false)
                emit(3)
            }
            .onCompletion {cause ->
                logE("onCompletion:    $cause")
            }
            .catch {cause ->
                logE("catch:   $cause")
            }
            .collect {
                logE("$it")
            }
        }
    }

    /*
        打印结果：
            1
            2
            onCompletion:    java.lang.IllegalStateException: Check failed.
            catch:   java.lang.IllegalStateException: Check failed.

            flow抛出异常，后面的代码不会再执行；
            onCompletion和catch都可以处理抛出的Throwable;

            注意：若catch在onCompletion前面调用，onCompletion中cause为null
     */

    fun testOnCompletionAndCatch1() {
        runBlocking {
            flow {
                emit(1)
                emit(2)
            }
                .onCompletion {cause ->
                    logE("onCompletion:    $cause")
                }
                .catch {cause ->
                    logE("catch:   $cause")
                }
                .collect {
                    check(it == 1)
                    logE("$it")
                }
        }
    }

    /*
        打印结果：
            1
            onCompletion:    null

        onCompletion，catch只能处理上游抛出的异常；
     */


}



























