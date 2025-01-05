package com.pic.catcher.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lu.magic.util.SizeUtil
import com.lu.magic.util.kxt.toElseString
import com.lu.magic.util.ripple.RectangleRippleBuilder
import com.lu.magic.util.ripple.RippleApplyUtil
import com.lu.magic.util.thread.AppExecutor
import com.pic.catcher.BuildConfig
import com.pic.catcher.R
import com.pic.catcher.adapter.AbsListAdapter
import com.pic.catcher.adapter.CommonListAdapter
import com.pic.catcher.base.BaseFragment
import com.pic.catcher.base.LifecycleAutoViewBinding
import com.pic.catcher.config.AppConfigUtil
import com.pic.catcher.databinding.FragmentMainBinding
import com.pic.catcher.databinding.ItemIconTextBinding
import com.pic.catcher.plugin.PicExportManager
import com.pic.catcher.route.AppRouter
import com.pic.catcher.util.ext.layoutInflate


class MainFragment : BaseFragment() {
    private var itemBinding: ItemIconTextBinding by LifecycleAutoViewBinding<MainFragment, ItemIconTextBinding>()
    private var mainBinding: FragmentMainBinding by LifecycleAutoViewBinding<MainFragment, FragmentMainBinding>()
    private val buildInfo = com.pic.catcher.AppBuildInfo.of()

    private val donateCardId = 10086

    private var mListAdapter: CommonListAdapter<Int, ItemBindingViewHolder>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentMainBinding.inflate(inflater, container, false).let {
            mainBinding = it
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rippleRadius = SizeUtil.dp2px(resources, 8f).toInt()

        mListAdapter = object : CommonListAdapter<Int, ItemBindingViewHolder>() {
            init {
                setData(arrayListOf(1, 2, 3))
            }


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemBindingViewHolder {
                itemBinding = ItemIconTextBinding.inflate(parent.layoutInflate, parent, false)

                return object : ItemBindingViewHolder(itemBinding) {
                    init {
                        itemBinding.layoutItem.setOnClickListener {
                            val itemValue = getItem(layoutPosition)

                            when (itemValue) {
                                1 -> {
                                    clickModuleCard()
                                }

                                2 -> {
                                    TipViewUtil.showLong(context, getString(R.string.app_module_des_format, PicExportManager.getInstance().exportDir.parent))
                                }

                                3 -> {
                                    AppRouter.routeConfigPage(context)
                                }
                            }
                        }

                    }
                }
            }

            override fun onBindViewHolder(vh: ItemBindingViewHolder, position: Int, parent: ViewGroup) {
                val itemValue = getItem(position)
                if (itemValue != 0) {
                    applyCommonItemRipple(vh.binding.layoutItem)
                }

                if (itemValue == 1) {
                    vh.binding.tvItemTitleSub2.text = getString(R.string.app_code_branch, buildInfo.branch)
                    vh.binding.tvItemTitleSub3.text = getString(R.string.app_commit_hash_format, buildInfo.commit)
                    vh.binding.tvItemTitleSub4.text = getString(R.string.app_build_time_format, buildInfo.buildTime)
                    vh.binding.tvItemTitleSub2.visibility = View.VISIBLE
                    vh.binding.tvItemTitleSub3.visibility = View.VISIBLE
                    vh.binding.tvItemTitleSub4.visibility = View.VISIBLE
                } else {
                    vh.binding.tvItemTitleSub2.visibility = View.GONE
                    vh.binding.tvItemTitleSub3.visibility = View.GONE
                    vh.binding.tvItemTitleSub4.visibility = View.GONE
                }

                when (itemValue) {
                    1 -> {
                        if (com.pic.catcher.SelfHook.getInstance().isModuleEnable) {
                            vh.binding.ivItemIcon.setImageResource(R.drawable.ic_icon_check)
                            vh.binding.tvItemTitle.setText(R.string.module_have_active)
                            applyModuleStateRipple(vh.binding.layoutItem, true)
                        } else {
                            vh.binding.ivItemIcon.setImageResource(R.drawable.ic_icon_warning)
                            vh.binding.tvItemTitle.setText(R.string.module_not_active)
                            applyModuleStateRipple(vh.binding.layoutItem, false)
                        }
                        vh.binding.tvItemTitleSub.text = (getString(R.string.module_version) + "：" + getVersionText())
                    }

                    2 -> {
                        vh.binding.ivItemIcon.setImageResource(R.drawable.ic_icon_des)
                        vh.binding.tvItemTitle.setText(R.string.app_use_help)
                        vh.binding.tvItemTitleSub.setText(R.string.click_here_to_des)
                    }

                    3 -> {
                        vh.binding.ivItemIcon.setImageResource(R.drawable.ic_icon_edit)
                        vh.binding.tvItemTitle.setText(R.string.app_config)
                        vh.binding.tvItemTitleSub.setText(R.string.click_here_to_edit_config)
                    }

                    donateCardId -> {
                        val donateCard = AppConfigUtil.config.mainUi?.donateCard
                        vh.binding.tvItemTitle.text = donateCard?.title.toElseString(
                            getString(R.string.donate)
                        )
                        vh.binding.tvItemTitleSub.text = donateCard?.des.toElseString(
                            getString(R.string.donate_description)
                        )
                        vh.binding.ivItemIcon.setImageResource(R.drawable.ic_icon_dollar)
                    }

                }

            }

            private fun applyCommonItemRipple(v: View) {
                RectangleRippleBuilder(Color.TRANSPARENT, Color.GRAY, rippleRadius).let {
                    RippleApplyUtil.apply(v, it)
                }
            }

            private fun applyModuleStateRipple(v: View, enable: Boolean) {
                val contentColor = if (enable) {
                    view.context.getColor(R.color.app_primary)
                } else {
                    0xFFFF6027.toInt()
                }
                RectangleRippleBuilder(contentColor, Color.GRAY, rippleRadius).let {
                    RippleApplyUtil.apply(v, it)
                }
            }

        }


// 设置了ripple， 子view拿走了事件，此处不响应
//        mainBinding.listView.setOnItemClickListener { _, view, position, _ ->
//
//        }
        mainBinding.listView.adapter = mListAdapter
//        AppConfigUtil.load { config, isRemote ->
//            if (isDetached || isRemoving) {
//                return@load
//            }
//            val donateCard = config.mainUi?.donateCard ?: return@load
//            if (donateCard.show) {
//                showDonateCard()
//            }
//        }
    }

    private fun clickModuleCard() {
        val moduleCard = AppConfigUtil.config.mainUi?.moduleCard
        if (moduleCard == null || moduleCard.link.isNullOrBlank()) {
            AppRouter.routeReleasesNotePage(activity, "更新日记")
//            AppRouter.routeCheckAppUpdateFeat(requireActivity())
        } else {
            AppRouter.route(activity, moduleCard.link)
//            AppRouter.routeReleasesNotePage(requireActivity(), "更新日记")
        }
    }

    private fun showDonateCard() {
        AppExecutor.executeMain {
            mListAdapter?.let { adapter ->
                if (adapter.dataList.contains(donateCardId)) {
                    return@executeMain
                }
                adapter.addData(donateCardId)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun getVersionText(): String {
        return if (BuildConfig.DEBUG) {
            "v${BuildConfig.VERSION_NAME}-debug"
        } else {
            "v${BuildConfig.VERSION_NAME}-release"
        }
    }

    open class ItemBindingViewHolder(@JvmField var binding: ItemIconTextBinding) :
        AbsListAdapter.ViewHolder(binding.root)


}