package com.example.kotlin2020.coroutine

import android.app.Application
import android.os.AsyncTask
import android.view.View
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin2020.kotlinplus.logE
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

@WorkerThread
suspend fun thread() {
    logE("thread   --${Thread.currentThread().name}")
}

fun testThread(view: View) {
    logE("testThread")
    GlobalScope.launch(Dispatchers.Main) {
        thread()
    }
}

class Vn : ViewModel() {
    init {
        viewModelScope.launch {
            //默认是主线程
        }

        GlobalScope.launch {
            //默认是子线程
        }
    }
}
/*
    CoroutineScope：kotlin中所有协程都在CoroutineScope中执行。
        CoroutineScope有方法：launch(返回job), async；(当不需要有结果返回时用launch，否则用async)
        当job取消，那么范围内所有的协程都会被取消；

    viewModelScope：ViewModel的扩展属性；
        协程范围默认是Dispatchers.main;
        ViewModel被回收后，协程会自动取消；

    协程的好处？
        1，解决了异步代码中需要使用接口回调的问题，回调嵌套也会降低可读性；
        2，可以catch子线程的异步代码，try...catch使用起来就像在同步代码中一样；
                主线程处理子线程的异常，只能通过接口回调。例如：RxJava框架中回调onError方法；

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTitle(title: Title)

    interface MainNetwork {
    @GET("next_title.json")
    suspend fun fetchNextTitle(): String
    Room和Retrofit的suspend方法，可以直接在Dispatchers.main中调用。(Room，Retrofit内部做了处理)
}
 */

class VM(application: Application) : AndroidViewModel(application) {

    //定义一个可观察的数据
    val data: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    //方式1：执行特定的逻辑doAsyncWork
    fun refreshTitle() {
        viewModelScope.launch { //main thread
            data.value = false

            val result = withContext(Dispatchers.IO) {
                try {
                    doAsyncWork()
                } catch (e: Exception) {
                    throw IllegalArgumentException()
                }
            }

            data.value = result == "abc"
        }
    }

    suspend fun doAsyncWork(): String {
        return "abc"
    }

    //方式2：使用高阶函数将逻辑抽离出来
    fun loadData(action: suspend () -> String) {
        viewModelScope.launch {
            data.value = false
            val result = withContext(Dispatchers.IO) {
                try {
                    action()
                } catch (e: Exception) {
                    throw IllegalArgumentException()
                }
            }
            data.value = result == "abc"
        }
    }

    //测试代码
    fun testSuspendLambda() {
        refreshTitle() //only do one thing(doAsyncWork)

        loadData {
            //do what you want do, eg: call doAsyncWork
            doAsyncWork()
        }
    }
}

/*
    挂起的函数类型格式：suspend (Int) -> Unit
        suspend的lambda中可以调用挂起函数；
        调用挂起函数的场景：协程，挂起函数，挂起的lambda；

    传统的重构：方法A抽取一段代码到方法B中，A调用B；
    使用高阶函数重构：方法A中抽取一段逻辑；(并非代码)。
        1，方法A中定义函数类型参数，然后invoke高阶函数；
        2，调用逻辑：方法A被其他方法调用；

 */

