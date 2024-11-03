package com.pic.catcher.base;

/**
 * @author Mingyueyixi
 * @date 2024/9/28 19:39
 * @description 为了获得极简的代码量，不引入androidx life 库，自行实现类似风格的api
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
