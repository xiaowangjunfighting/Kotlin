package com.example.kotlin2020.coroutine

import android.util.Log
import com.example.kotlin2020.kotlinplus.logE
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*
import javax.security.auth.login.LoginException

class CoroutinePlus01 {

    fun testCoroutine() {

        GlobalScope.launch {
            delay(1000)
            logE(currentThread() + "   1")
        }
        logE(currentThread() + "    2")

        runBlocking {
            delay(2000)
        }

        logE(currentThread() + "    3")

    }

    /*
        先打印2，过1秒后打印1，再过1秒打印3；
        GlobalScope是一个单例对象;
        GlobalScope.launch可替换为thread {  }，delay()替换为Thread.sleep()

        创建协程阻塞的方法：必须等闭包内代码执行完成后，才执行闭包后面的代码；
        创建协程非阻塞的方法：不会阻塞闭包后面代码的执行；

        非阻塞：launch
        阻塞：runBlocking


     */

    fun testCoroutine1() {
/*
        runBlocking {
            val job = launch {
                repeat(3) {
                    logE("$it")
                    delay(1000)
                }
            }
            delay(1500)
            logE("after delay...")
            job.cancel()
            job.join()
            logE("after join...")
        }
*/
         runBlocking {
             val startTime = System.currentTimeMillis()

             val job = launch(Dispatchers.Default) {
                 var nextTime = startTime
                 var i = 0
                 while (i < 5) { //一个执行计算的循环，只是为了占用 CPU
//                 while (isActive) { //CoroutineScope的扩展属性
                     if (System.currentTimeMillis() >= nextTime) {
                         logE("${i++}")
                         nextTime += 500
                     }
                 }
             }
             delay(1300)
             logE("after delay")
             job.cancelAndJoin()
             logE("after cancelAndJoin") //1
         }
    }

    /*
        job.cancel()：取消协程
        job.join()：等待协程执行完成后，再执行
        cancelAndJoin：即便是取消协程，以后等协程内任务完成后，才执行1处代码；(效果和join类似)
            若希望取消后，不再继续执行协程内任务(效果和cancel类似)? 可以用isActive判断处理;
     */

    fun testCoroutine2() {
/*
        runBlocking {
            val job = launch {
                try {
                    repeat(100) {
                        logE("$it")
                        delay(500)
                    }
                } finally {
                    logE("finally")
                }
            }

            delay(1300)
            logE("after delay")
            job.cancelAndJoin()
            logE("after cancelAndJoin")
        }
*/

        runBlocking {
            val job = launch {
                try {
                    repeat(100) {
                        logE("$it")
                        delay(500)
                    }
                } finally {
                    withContext(NonCancellable) {
                        logE("finally")
                        delay(2000)
                        logE("not cancel")
                    }
                }
            }

            delay(1300)
            logE("after delay")
            job.cancelAndJoin()
            logE("after cancelAndJoin")
        }

        //test ---------------

        logE("www")

        GlobalScope.launch {
            logE("launch1")
        }

        logE("launch2")
    }

    /*
        cancelAndJoin可以取消协程里的repeat闭包的内容；
        用try...finally包装协程里的代码块，在协程被取消时，可以在finally中执行最后的一些操作；
        重新挂起一个被取消的协程，可以在finally中使用withContext(NonCancellable) {}
     */

    fun testCoroutine3() {
        runBlocking {
            //kotlinx.coroutines.TimeoutCancellationException: Timed out waiting for 3000 ms
            /*withTimeout(3000) {
                repeat(5) {
                    logE(currentThread() + " $it")
                    delay(1000)
                }
            }*/

            val result = withTimeoutOrNull(3000) {
                repeat(2) {
                    logE(currentThread() + " $it")
                    delay(1000)
                }

                "result"
            }
        }
    }

    /*
        withTimeout设置超时时间，该时间内协程没有执行完，则报TimeoutCancellationException异常
        withTimeoutOrNull：超时返回null；不超时返回t，例如"result";
     */
}



fun currentThread() = Thread.currentThread().name

