package com.pic.catcher.ui

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.ViewGroup
import com.lu.magic.util.log.LogUtil
import com.pic.catcher.R
import com.pic.catcher.adapter.BindingListAdapter
import com.pic.catcher.base.BindingActivity
import com.pic.catcher.bean.EditItem
import com.pic.catcher.bean.ItemType
import com.pic.catcher.bean.SwitchItem
import com.pic.catcher.bean.TextItem
import com.pic.catcher.config.ModuleConfig
import com.pic.catcher.databinding.ActivityConfigBinding
import com.pic.catcher.databinding.ItemConfigEditBinding
import com.pic.catcher.databinding.ItemConfigSwitchBinding
import com.pic.catcher.databinding.ItemConfigTextBinding
import com.pic.catcher.util.ext.dp
import com.pic.catcher.util.ext.setPadding
import com.pic.catcher.util.ext.toIntElse

class ConfigActivity : BindingActivity<ActivityConfigBinding>() {

    private lateinit var mAdapter: ConfigListAdapter
    private lateinit var moduleConfig: ModuleConfig

    override fun onInflateBinding(): ActivityConfigBinding {
        return ActivityConfigBinding.inflate(layoutInflater, null, false)
    }

    override fun initView() {
        moduleConfig = ModuleConfig.instance
        // 设置标题
        mBinding.toolbar.setTitle(R.string.app_title_config)
        mBinding.toolbar.setNavigationIcon(R.drawable.ic_back)
        // 设置导航图标（例如返回按钮）
        mBinding.toolbar.setNavigationOnClickListener {
            finish()
        }

        mAdapter = ConfigListAdapter().apply {
            setData(
                listOf(
                    SwitchItem(getString(R.string.config_catch_net_pic), moduleConfig.isCatchNetPic).apply {
                        //属性变更监听
                        addPropertyChangeListener {
                            moduleConfig.isCatchNetPic = checked
                        }
                    },
                    SwitchItem(getString(R.string.config_catch_webview_pic), moduleConfig.isCatchWebViewPic).apply {
                        //属性变更监听
                        addPropertyChangeListener {
                            moduleConfig.isCatchWebViewPic = checked
                        }
                    },
                    SwitchItem(getString(R.string.config_catch_glide_pic), moduleConfig.isCatchGlidePic).apply {
                        //属性变更监听
                        addPropertyChangeListener {
                            moduleConfig.isCatchGlidePic = checked
                        }
                    },
                    EditItem(getString(R.string.config_min_space_size), moduleConfig.minSpaceSize.toString(), InputType.TYPE_CLASS_NUMBER).apply {
                        addPropertyChangeListener {
                            moduleConfig.minSpaceSize = value.toIntElse(0)
                        }
                    },
                )
            )
        }
        mBinding.listView.adapter = mAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        moduleConfig.save()
    }


    inner class ConfigListAdapter : BindingListAdapter<ItemType>() {
        override fun getViewTypeCount(): Int {
            return 3
        }

        override fun getItemViewType(position: Int): Int {
            return when (getItem(position)) {
                is SwitchItem -> {
                    ItemType.TYPE_SWITCH
                }

                is EditItem -> {
                    ItemType.TYPE_EDIT
                }

                is TextItem -> {
                    ItemType.TYPE_TEXT
                }

                else -> {
                    ItemType.TYPE_TEXT
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {

            return when (viewType) {
                ItemType.TYPE_SWITCH -> {
                    BindingHolder(ItemConfigSwitchBinding.inflate(layoutInflater, parent, false))
                }

                ItemType.TYPE_EDIT -> {
                    BindingHolder(ItemConfigEditBinding.inflate(layoutInflater, parent, false))
                }

                else -> {
                    BindingHolder(ItemConfigTextBinding.inflate(layoutInflater, parent, false))
                }
            }
        }

        override fun onBindViewHolder(vh: BindingHolder, position: Int, parent: ViewGroup) {
            vh.binding.root.setPadding(h = 18.dp, v = 0)

            when (val item = getItem(position)) {
                is SwitchItem -> {
                    val holder = vh.binding as ItemConfigSwitchBinding
                    holder.itemTitle.text = item.title
                    holder.itemSwitch.setOnCheckedChangeListener({ v, isChecked ->
                        if (item.checked == isChecked) {
                            return@setOnCheckedChangeListener
                        }
                        item.checked = isChecked
                        //监听必须在设置itemSwitch.isChecked之前，防止漏掉。
                        //因为视图复用
                        LogUtil.d("itemSwitch change", v, "item", item.toJson())
                    })
                    holder.itemSwitch.isChecked = item.checked
                }

                is EditItem -> {
                    val holder = vh.binding as ItemConfigEditBinding
                    holder.itemTitle.text = item.name
                    holder.itemEdit.inputType = item.inputType
                    holder.itemEdit.setText(item.value)
                    var textWatcher = holder.itemEdit.getTag(holder.itemEdit.id)
                    if (textWatcher != null && textWatcher is TextWatcher) {
                        holder.itemEdit.removeTextChangedListener(textWatcher)
                    }
                    textWatcher = object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        }

                        override fun afterTextChanged(s: Editable?) {
                            item.value = s?.toString()
                        }

                    }
                    holder.itemEdit.setTag(holder.itemEdit.id, textWatcher)
                    holder.itemEdit.addTextChangedListener(textWatcher)
                }

                is TextItem -> {
                    val holder = vh.binding as ItemConfigTextBinding
                    holder.itemTitle.text = item.name
                    holder.itemValue.text = item.value
                }
            }
        }
    }
}
