package com.example.kotlin2020.other

//导入顶层函数，属性
import com.example.kotlin2020.k
import com.example.kotlin2020.topFunction
import com.example.kotlin2020.v
import com.example.kotlin2020.w

//导入扩展函数，并修改导入的函数名称
import com.example.kotlin2020.lastChar as last

class Other {
    fun test() {
        topFunction()

        k = 2
        print(k)

        print(v)

        print(w)

//        "Kotlin".lastChar()
        "Kotlin".last()


    }

    object AA {

    }

    companion object B{

    }

}

object AAA {

}

