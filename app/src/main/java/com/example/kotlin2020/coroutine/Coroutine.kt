package com.example.kotlin2020.coroutine

import kotlinx.coroutines.*

/**
 * 协程练习
 */
class Coroutine {

    fun testCoroutine() {
        val coroutineScope = GlobalScope

        coroutineScope.launch(Dispatchers.Main) {
            val deferred1 = async {
                // do async work
            }

            val deferred2 = async {
                // do async work
            }

            //拿到data1, data2数据后更新UI
            val data1 = deferred1.await()
            val data2 = deferred2.await()
        }

        //线程切换
        coroutineScope.launch(Dispatchers.Main) {
            //do something in main thread

            coroutineScope.launch(Dispatchers.IO) {
                // do async work

                coroutineScope.launch(Dispatchers.Main) {
                    //update UI
                }
            }
        }

        //线程切换用挂起处理后
        coroutineScope.launch(Dispatchers.Main) {
            //do something in main thread

            withContext(Dispatchers.IO) {
                // do async work
            }

            withContext(Dispatchers.Main) {
                //update UI
            }
        }

        //使用挂起函数封装后(用同步的方式写异步的代码，这就是非阻塞式挂起)
        coroutineScope.launch(Dispatchers.Main) {
            //do something in main thread
            doWork()
            updateUI()
        }

    }

    //挂起指的是切换线程
    suspend fun doWork() = withContext(Dispatchers.IO) {
        // do async work
    }

    suspend fun updateUI() = withContext(Dispatchers.Main) {
        //update ui
    }

    //不知道切哪个线程
    suspend fun defineSuspend() {
        withContext(Dispatchers.IO) {

        }
    }

    //耗时操作
    suspend fun doWork1() = withContext(Dispatchers.IO) {
        // do async work
    }

    suspend fun interval() {
        delay(3000)
        //do something
    }

}

/*
    协程是一种变成思想，不局限于特定的语言；

    协程就是launch，async闭包中的代码块；

    启动协程的三种方式：launch, async, runBlocking(适用于单元测试，由于它是阻塞线程的，开发中不会用到它)

    挂起：指的是挂起协程。其实就是切换线程
        具体是：协程执行到挂起函数，切换线程到挂起函数指定的线程，完成工作后再切回到原来的协程所在的线程；
        简单来说是：线程稍后会被自动切换回来的调度操作。切换回来的动作叫resume;

    resume是协程的功能，因此只能在协程或挂起函数中调用挂起函数。挂起函数最终必须被协程调用；

    suspend修饰的函数必须调用挂起函数，否则不知道切换到哪个线程；

    suspend修饰符意义：告诉开发者直接或间接的(挂起函数调用它)在协程中调用它;

    自定义suspend函数的场景：
        1，耗时操作。如：I/O操作(文件读写，网络请求，图片模糊处理等)，CPU计算工作;
        2，延迟一段时间，再去做什么事(这段代码直接执行可能并不慢)；

    非阻塞：用单线程阻塞的代码风格，写出了非阻塞的代码；
        阻塞式的代码风格，非阻塞指没有阻塞主线程更新UI；


    Dispatchers.Main：主线程，包括：调用suspend函数，更新UI，更新LiveData对象；
    Dispatchers.IO：主线程之外的I/O操作(文件读写，网络请求，图片模糊处理等)；
    Dispatchers.Default：主线程之外占用大量CPU资源的计算。如：JSON解析，列表排序；
 */