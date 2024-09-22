package com.pic.catcher.base

import java.util.concurrent.CopyOnWriteArrayList

class CustomLifecycleOwnerDelegate() : CustomLifecycleOwner{
    private val mCustomLifecycleList: CopyOnWriteArrayList<CustomLifecycle> = CopyOnWriteArrayList()
    private var mCurrentState = CustomLifecycle.State.INITIALIZED

    fun setCurrentState(state: CustomLifecycle.State) {
        mCurrentState = state
        val it = mCustomLifecycleList.iterator()
        while (it.hasNext()){
            val customLifecycle = it.next()
            customLifecycle.onLifeStateChanged(this, state)
        }
    }

    override fun getCurrentState(): CustomLifecycle.State {
        return mCurrentState
    }

    override fun addObserver(life: CustomLifecycle) {
        mCustomLifecycleList.add(life)
    }

    override fun removeObserver(life: CustomLifecycle) {
        mCustomLifecycleList.remove(life)
    }

}