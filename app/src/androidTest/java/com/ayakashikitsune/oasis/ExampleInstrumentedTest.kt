package com.ayakashikitsune.oasis

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.

        val originalList = (1..14).toList()

        // Get the first 7 items
        val first7Items = originalList.subList(0, 7)

        // Get the last 7 items
        val last7Items = originalList.subList(originalList.size - 7, originalList.size)

        // Print the results
        println("Original List: $originalList")
        println("First 7 Items: $first7Items")
        println("Last 7 Items: $last7Items")
        println("Original List: $originalList")


    }
}