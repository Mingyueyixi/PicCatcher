package com.pic.catcher.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding


abstract class BindingActivity<T : ViewBinding> :BaseActivity(){
    protected lateinit var mBinding: T
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = onInflateBinding()
        setContentView(mBinding.root)
        initView()

    }

    abstract fun onInflateBinding(): T
    abstract fun initView()
}