package com.inoo.sutoriapp.utils

import androidx.test.espresso.idling.CountingIdlingResource

@Suppress("unused")
object EspressoIdlingResource {
    private const val RESOURCE = "GLOBAL"

    val idlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        idlingResource.increment()
    }

    fun decrement() {
        if (!idlingResource.isIdleNow) {
            idlingResource.decrement()
        }
    }
}
