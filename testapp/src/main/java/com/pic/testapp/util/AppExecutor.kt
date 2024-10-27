package com.pic.testapp.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object  AppExecutor {
    private val mDiskIO: Executor by lazy { DiskIOThreadExecutor() }
    private val mNetworkIO: Executor by lazy { Executors.newFixedThreadPool(3) }
    private val mMainThread: Executor by lazy { MainThreadExecutor() }
    fun runOnDiskIo(command: Runnable) {
        mDiskIO.execute(command)
    }

    fun runOnNetworkIo(command: Runnable) {
        mNetworkIO.execute(command)
    }

    fun runOnUiThread(command: Runnable) {
        mMainThread.execute(command)
    }

    class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                command.run()
                return
            }
            mainThreadHandler.post(command)
        }
    }

    class DiskIOThreadExecutor : Executor {
        private val mDiskIO = Executors.newSingleThreadExecutor()
        override fun execute(command: Runnable) {
            mDiskIO.execute(command)
        }
    }


}
