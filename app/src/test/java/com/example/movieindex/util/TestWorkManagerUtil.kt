package com.example.movieindex.util

import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper

class TestWorkManagerUtil(private val context: Context) {

    private val config = Configuration.Builder()
        .setMinimumLoggingLevel(Log.DEBUG)
        .setExecutor(SynchronousExecutor())
        .build()

    fun setupWorkManager() = WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    val testDriverConstraints = WorkManagerTestInitHelper.getTestDriver(context)


}