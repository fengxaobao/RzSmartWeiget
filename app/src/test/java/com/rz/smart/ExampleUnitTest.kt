package com.rz.smart

import org.junit.Test

import org.junit.Assert.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun testThread(){
        for(i in 0..10){
            val t = MyThread("线程$i")
            t.start()
            t.interrupt()
        }
    }
    class MyThread(var s: String) :Thread(){
        override fun run() {
            super.run()
            if(isInterrupted){
                println("我正在进行中${s}=$isInterrupted")
            }
            sleep(300)
            println("我正在进行中${s}")
        }
    }
}