package com.pic.catcher.base

import com.pic.catcher.base.ViewModelProviders.Companion.cleanViewModelStore
import com.pic.catcher.base.ViewModelProviders.Companion.getViewModel
import com.pic.catcher.base.ViewModelProviders.Companion.putViewModel
import com.pic.catcher.base.ViewModelProviders.Factory

class ViewModelProvider(private val owner: CustomLifecycleOwner) {
    private val lifecycle: CustomLifecycle = object : CustomLifecycle {
        override fun onLifeStateChanged(source: CustomLifecycleOwner, state: CustomLifecycle.State) {
            if (state == CustomLifecycle.State.DESTROYED) {
                cleanViewModelStore(owner)
            }
        }
    }

    init {
        owner.addObserver(lifecycle)
    }

    fun <T : BaseViewModel>get(clazz: Class<T>): T {
        return get(clazz, Factory())
    }

    fun <T : BaseViewModel> get(clazz: Class<T>, factory: Factory): T {
        var result = getViewModel(owner, clazz)
        if (result == null || !clazz.isAssignableFrom(result.javaClass)) {
            result = factory.create(clazz)
            putViewModel(owner, result)
        }
        return result as T
    }
}

class ViewModelProviders {

    class Factory {
        fun <T : BaseViewModel> create(clazz: Class<T>): T {
            return clazz.getConstructor().newInstance()
        }
    }


    companion object {
        private val viewModelStore = HashMap<String, HashMap<String, BaseViewModel>>()
        internal fun putViewModel(owner: CustomLifecycleOwner, viewModel: BaseViewModel) {
            val ownerKey = getLifecycleOwnerKey(owner)
            var map = viewModelStore[ownerKey]
            if (map == null) {
                map = HashMap()
                viewModelStore[ownerKey] = map
            }
            map[getViewModelStoreKey(viewModel)] = viewModel
        }

        internal fun cleanViewModelStore(owner: CustomLifecycleOwner) {
            viewModelStore.remove(getLifecycleOwnerKey(owner))
        }

        private fun getViewModelStoreKey(clazz: BaseViewModel): String {
            return getViewModelStoreKey(clazz.javaClass)
        }

        private fun getLifecycleOwnerKey(owner: CustomLifecycleOwner): String {
            return owner.toString()
        }

        private fun getViewModelStoreKey(clazz: Class<out BaseViewModel>): String {
            return clazz.name
        }


        internal fun getViewModel(owner: CustomLifecycleOwner, clazz: Class<out BaseViewModel>): BaseViewModel? {
            return viewModelStore[getLifecycleOwnerKey(owner)]?.get(getViewModelStoreKey(clazz))
        }

        fun from(lifecycleOwner: CustomLifecycleOwner): ViewModelProvider {
            return ViewModelProvider(lifecycleOwner)
        }

    }

}