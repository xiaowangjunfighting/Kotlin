package com.example.kotlin2020.coroutine

import android.net.ConnectivityManager
import android.util.Log
import com.example.kotlin2020.kotlinplus.logE
import kotlinx.coroutines.*
import java.lang.ArithmeticException
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

/**
 * 组合挂起函数
 */
class CoroutinePlus02 {
    suspend fun doSth1() : Int {
        delay(1000)
        return 1
    }

    suspend fun doSth2(): Int {
        delay(2000)
        return 2
    }

    //默认
    fun testCoroutine() {
        runBlocking {
            val time = measureTimeMillis {
                val one = doSth1()
                val two = doSth2()
                logE("${one + two}") //3
            }
            logE("$time") //3011
        }
    }
    /*
        默认是顺序调用；
        使用场景：后一个异步任务的执行，依赖于前一个异步任务的执行结果；
     */

    //async并发
    fun testCoroutine1() {
        val time = measureTimeMillis {
            runBlocking {
                val one = async {
                    logE("one")
                    doSth1()
                }

                val two = async {
                    logE("two")
                    doSth2()
                }
                val result = one.await() + two.await()
                logE("$result") //3
            }
        }
        logE("$time") //2077
    }

    //惰性async
    fun testCoroutine2() {
        runBlocking {
            val time = measureTimeMillis {
                val one = async(start = CoroutineStart.LAZY) {
                    logE("one")
                    doSth1()
                }

                val two = async(start = CoroutineStart.LAZY) {
                    logE("two")
                    doSth2()
                }

                one.start()
                two.start()

                val result = one.await() + two.await()
                logE("$result") //3
            }
            logE("$time") //2089
        }
    }
    /*
        使用场景：两个异步任务同时执行，用async方法创建协程；

        async方法返回Deferred，是Job的子接口，也可以取消协程；
        使用Deferred$await方法获取异步任务的结果；

        给async方法start参数设置CoroutineStart.LAZY后，协程会懒启动；
            协程内代码不会立即执行，除非调用Deferred$start或Deferred$await；
            若不调用start，只调用await，则两个异步任务会顺序执行；
     */

    fun testCoroutine3() {
        runBlocking {
            val time = measureTimeMillis {
                val sum = coroutineScope {
                    val one = async { doSth1() }
                    val two = async { doSth2() }
                    one.await() + two.await()
                }
                logE("$sum") //3
            }
            logE("$time") //2022
        }
    }

    fun testCoroutine4() {
        runBlocking {
            try {
                sum1()
            } catch (e: Exception) {
                logE("runBlocking exception")
            }
        }
    }

    suspend fun sum1() : Int{
        return coroutineScope {
            val one = async {
                try {
                    logE("async1")
                    delay(10000) //模拟一个延迟，等待另一个并发任务抛出异常
                    10
                } finally {
                    logE("async1 finally")
                }
            }

            val two = async {
                logE("async2")
                throw ArithmeticException()
                20
            }
            one.await() + two.await()
        }

    }

    /*
        打印结果：
            async1
            async2
            async1 finally
            runBlocking exception

        一个协程抛出异常，作用域中的所有的协程都会取消，包括：第一个async，父协程；
     */

    fun testCoroutine5() {
        runBlocking {

            val request = launch {
                GlobalScope.launch {
                    logE("GlobalScope.launch 1")
                    delay(1000)
                    logE("GlobalScope.launch 2") //不会被取消
                }


                launch {
                    delay(100)
                    logE("launch 1")
                    delay(1000)
                    logE("launch 2") //被取消

                }
            }

            delay(500)
            request.cancel()
            delay(1000)
            logE("final")
        }
    }

    /*
        GlobalScope是一个单例对象，实现了CoroutineScope接口；

        父协程取消，会递归取消所有子协程；
            若GlobalScope启动一个协程时，父协程无法取消它；
     */
}