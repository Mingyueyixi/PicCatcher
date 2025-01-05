package com.pic.catcher.util.proxy

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ObservableProperty<T>(
    private var value: T,
    private val onChange: (oldValue: T, newValue: T) -> Unit
) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        if (value != newValue) {
            val oldValue = value
            value = newValue
            onChange(oldValue, newValue)
        }
    }
}

open class Observable {
    private val propertyChangeSupport = PropertyChangeSupport(this)

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        propertyChangeSupport.removePropertyChangeListener(listener)
    }

    protected fun firePropertyChange(propertyName: String, oldValue: Any?, newValue: Any?) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue)
    }
}


open class ObservableObject : Observable() {
    protected fun <T> observableProperty(initialValue: T, propertyName: String): ObservableProperty<T> {
        return ObservableProperty(initialValue) { oldValue, newValue ->
            firePropertyChange(propertyName, oldValue, newValue)
        }
    }
}