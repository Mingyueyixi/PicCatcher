package com.pic.catcher.base
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 自动解绑定的ViewBinding，依赖kotlin的委托模式
 */
class LifecycleAutoViewBinding<F : CustomLifecycleOwner, V : ViewBinding> : ReadWriteProperty<F, V>, CustomLifecycle {

    private var binding: V? = null

    override fun getValue(thisRef: F, property: KProperty<*>): V {
        binding?.let {
            return it
        }
        throw IllegalStateException("Can't access ViewBinding before onCreateView and after onDestroyView!")
    }

    override fun setValue(thisRef: F, property: KProperty<*>, value: V) {
        if (thisRef.getCurrentState() == CustomLifecycle.State.DESTROYED) {
            throw IllegalStateException("Can't set ViewBinding after onDestroyView!")
        }
        thisRef.addObserver(this)
        binding = value
    }

    override fun onLifeStateChanged(source: CustomLifecycleOwner, state: CustomLifecycle.State) {
        if (state == CustomLifecycle.State.DESTROYED) {
            binding = null
            source.removeObserver(this)
        }
    }
}