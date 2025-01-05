package com.pic.catcher.adapter

import androidx.viewbinding.ViewBinding

abstract class BindingListAdapter<T> : CommonListAdapter<T, BindingListAdapter.BindingHolder>() {

    class BindingHolder(val binding: ViewBinding) : AbsListAdapter.ViewHolder(binding.root) {

    }
}