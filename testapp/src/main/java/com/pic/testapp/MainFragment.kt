package com.pic.testapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.lu.magic.ui.recycler.MultiAdapter
import com.lu.magic.ui.recycler.MultiViewHolder
import com.lu.magic.ui.recycler.SimpleItemType
import com.pic.testapp.action.Action
import com.pic.testapp.action.LoadBitmapAction
import com.pic.testapp.action.LoadHttpPicAction
import com.pic.testapp.base.BaseFragment
import com.pic.testapp.base.BindingFragment
import com.pic.testapp.databinding.FragMainLayoutBinding
import com.pic.testapp.databinding.ItemTextInlineBlockBinding

class MainFragment : BindingFragment<FragMainLayoutBinding>() {

    private lateinit var mAdapter: MultiAdapter<ItemBean>

    override fun onCreateBinding(inflater: LayoutInflater, container: ViewGroup?): FragMainLayoutBinding {
        return FragMainLayoutBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        mBinding.mainRecyclerView.layoutManager = object : FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }

        mBinding.mainRecyclerView.adapter = MultiAdapter<ItemBean>().addItemType(object : SimpleItemType<ItemBean>() {
            override fun createViewHolder(adapter: MultiAdapter<ItemBean>, parent: ViewGroup, viewType: Int): MultiViewHolder<ItemBean> {
                return ItemBindingViewHolder(ItemTextInlineBlockBinding.inflate(layoutInflater, parent, false))
            }
        }).also {
            mAdapter = it
        }
    }

    override fun initData() {
        mAdapter.setData(
            listOf(
                ItemBean("使用okhttp4加载网络图片", LoadHttpPicAction(false)),
                ItemBean("使用系统http api加载网络图片", LoadHttpPicAction(true)),
                ItemBean("loadBitmap", LoadBitmapAction()),
                ItemBean("webview", WebViewFragment::class.java),
                ItemBean("x5Webview", X5WebViewFragment::class.java),
            )
        )
    }


    inner class ItemBindingViewHolder(val itemBinding: ItemTextInlineBlockBinding) : MultiViewHolder<ItemBean>(itemBinding.root) {

        override fun onBindView(adapter: MultiAdapter<ItemBean>, itemModel: ItemBean, position: Int) {
            itemBinding.itemTextContent.setText(itemModel.title)
            itemBinding.itemTextContent.setOnClickListener {
                if (itemModel.action is Class<*>) {
                    if (BaseFragment::class.java.isAssignableFrom(itemModel.action)) {
                        //传入的是BaseFragment子类型
                        try {
//                            var fragment = parentFragmentManager.fragmentFactory.instantiate(
//                                itemModel.action.classLoader,
//                                itemModel.action.name
//                            ) as BaseFragment?
                            addActivityFragment(itemModel.action.getConstructor().newInstance() as BaseFragment)
                        } catch (e: Exception) {
                            Toast.makeText(context, "无法创建对象", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    }
                } else if (itemModel.action is Action) {
                    itemModel.action.doAction(context)
                }
            }

        }

    }

}