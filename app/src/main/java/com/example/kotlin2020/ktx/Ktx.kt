package com.example.kotlin2020.ktx

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.arraySetOf
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Target
import androidx.palette.graphics.get
import io.reactivex.Flowable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Ktx {

    //ktx-core
    fun testSp(context: Context) {
        context.getSharedPreferences("books", Context.MODE_PRIVATE)
            .edit(commit = false) {
                putInt("count", 10)
            }
    }

    //fragment-ktx
    fun testFragment(activity: AppCompatActivity, fragment: Fragment) {
        activity.supportFragmentManager.commit {
            addToBackStack(null)
            add(-1, fragment)
        }
    }

    //Palette KTX
    fun testPalette(bitmap: Bitmap, target: Target) {
        val p = Palette.from(bitmap).generate()
        val swatch = p[target]
    }

    //Collection KTX
    fun testCollection() {
        val set = arraySetOf(1, 2, 3) + arraySetOf(3, 4, 5)
        val newSet = set + 7 + 8
    }

    //Reactive Streams KTX
    fun testFlowableToLiveData(data: Flowable<List<Any>>) {
        LiveDataReactiveStreams.fromPublisher(data)
    }


    //ViewModel KTX
    class NameViewModel : ViewModel() {

        //扩展属性viewModelScope可以轻松创建协程
        fun makeNetworkRequest() {
            viewModelScope.launch {
                //...
            }


        }

    }



}