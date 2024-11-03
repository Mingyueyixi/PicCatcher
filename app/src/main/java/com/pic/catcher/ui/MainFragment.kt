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
                setData(arrayListOf(1, 2))
            }


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemBindingViewHolder {
                itemBinding = ItemIconTextBinding.inflate(parent.layoutInflate, parent, false)

                return object : ItemBindingViewHolder(itemBinding) {
                    init {
                        itemBinding.layoutItem.setOnClickListener {
                            val itemValue = getItem(layoutPosition)

                            when (itemValue) {
                                1 -> clickModuleCard()
                                2 -> {
                                    TipViewUtil.showLong(context,"""这个模块会将目标app的图片抓取，并保存到"${PicExportManager.getInstance().exportDir.parent}/app包名"下，请注意在不使用时，一定要将 LSP 管理器的选项关闭，以免一直抓取图片，影响app性能。""")
                                }
//                                3 -> jumpManagerConfigUI(Constrant.VALUE_INTENT_PLUGIN_MODE_MANAGER)
//                                donateCardId -> AppRouter.routeDonateFeat(requireActivity())
                            }
                        }

                    }
                }
            }

            override fun onBindViewHolder(vh: ItemBindingViewHolder, position: Int, parent: ViewGroup) {
                if (position != 0) {
                    applyCommonItemRipple(vh.binding.layoutItem)
                }
                val position = getItem(position)
                if (position == 1) {
                    vh.binding.tvItemTitleSub2.text = "代码分支：" + buildInfo.branch
                    vh.binding.tvItemTitleSub3.text = "提交哈希：" + buildInfo.commit
                    vh.binding.tvItemTitleSub4.text = "构建时间：" + buildInfo.buildTime
                    vh.binding.tvItemTitleSub2.visibility = View.VISIBLE
                    vh.binding.tvItemTitleSub3.visibility = View.VISIBLE
                    vh.binding.tvItemTitleSub4.visibility = View.VISIBLE
                } else {
                    vh.binding.tvItemTitleSub2.visibility = View.GONE
                    vh.binding.tvItemTitleSub3.visibility = View.GONE
                    vh.binding.tvItemTitleSub4.visibility = View.GONE
                }

                when (position) {
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
                        vh.binding.tvItemTitle.setText(R.string.config_des)
                        vh.binding.tvItemTitleSub.setText(R.string.click_here_to_des)
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