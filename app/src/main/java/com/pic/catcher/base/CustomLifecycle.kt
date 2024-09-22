package com.pic.catcher.base;

/**
 * @author Mingyueyixi
 * @date 2024/9/28 19:39
 * @description
 */
interface CustomLifecycle {

    enum class State {
        INITIALIZED,
        CREATED,
        STARTED,
        RESUMED,
        PAUSED,
        STOPPED,
        DESTROYED,
    }

    fun onLifeStateChanged(source: CustomLifecycleOwner, state: State)
}

interface CustomLifecycleOwner {
    fun getCurrentState(): CustomLifecycle.State
    fun addObserver(life: CustomLifecycle)
    fun removeObserver(life: CustomLifecycle)
}
