package com.aiso.qfast.base.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class FragmentTabDelegate <T : FragmentTabDelegate.TabInfo> {

    var tabStateListener: ((List<TabInfo>, Int) -> Unit)? = null
    var tabClickListener: TabClickListener? = null
    var tabChangeListener: ((Int) -> Unit)? = null

    private val tabs = mutableListOf<T>()

    private var attached = false
    private var containerId = 0
    private var defaultSelectTabId: Int? = null
    private lateinit var fragmentManager: FragmentManager

    private var targetFragmentTag: String? = null
    private var currentFragmentTag: String? = null

    fun tabs(): List<T> = tabs

    fun setupManager(manager: FragmentManager, containerId: Int) {
        this.fragmentManager = manager
        this.containerId = containerId
    }

    fun setDefaultSelectTabId(tabId: Int) {
        this.defaultSelectTabId = tabId
    }

    fun onAttach() {
        attached = true
        loadFragment()
    }

    fun onDetach() {
        attached = false
    }

    fun onViewCreated() {
        onAttach()
    }

    fun onDestroy() {
        onDetach()
    }

    fun setCurrentTab(tabId: Int) {
        val targetTab = tabs.firstOrNull { it.tabId == tabId }
        if (targetTab == null) {
            Log.e("TTFragmentTabDelegate", "setCurrentTab 找不到对应 TabInfo，tabId = $tabId")
            return
        }
        val tabClickListener = this.tabClickListener
        if (tabClickListener != null) {
            if (!tabClickListener.allowTabChanged.invoke(targetTab.tabId)) {
                Log.i(
                    "TTFragmentTabDelegate", "setCurrentTab 不允许切换 !allowTabChanged , tabId= $tabId"
                )
                return
            }
            val currentTabInfo = tabs.firstOrNull { it.tag == currentFragmentTag }
            if (currentTabInfo != null && currentTabInfo.tabId == tabId) {
                tabClickListener.onSameTabClicked.invoke(tabId)
                Log.i("TTFragmentTabDelegate", "setCurrentTab 点击相同 tabId , tabId= $tabId")
                return
            }
        }
        changeTab(targetTab.tag)
    }

    fun setCurrentTab(tag: String) {
        val targetTab = tabs.firstOrNull { it.tag == tag }
        if (targetTab == null) {
            Log.e(
                "TTFragmentTabDelegate",
                "setCurrentTab 找不到对应 TabInfo，tag = $tag currentFragmentTag = $currentFragmentTag"
            )
            return
        }
        val tabClickListener = this.tabClickListener
        if (tabClickListener != null) {
            if (!tabClickListener.allowTabChanged.invoke(targetTab.tabId)) {
                Log.i(
                    "TTFragmentTabDelegate", "setCurrentTab 不允许切换 !allowTabChanged , tag= $tag"
                )
                return
            }
            val currentTabInfo = tabs.firstOrNull { it.tag == currentFragmentTag }
            if (currentTabInfo != null && currentTabInfo.tag == tag) {
                tabClickListener.onSameTabClicked.invoke(currentTabInfo.tabId)
                Log.i("TTFragmentTabDelegate", "setCurrentTab 点击相同 tabId , tag = $tag")
                return
            }
        }
        changeTab(targetTab.tag)
    }

    fun changeTab(tag: String) {
        targetFragmentTag = tag

        if (!attached) {
            return
        }
        val ft = navigateToTargetFragment(tag, null)
        ft?.commitNowAllowingStateLoss()
    }

    private fun loadFragment() {
        var targetFragmentTag = this.targetFragmentTag
        targetFragmentTag = if (!targetFragmentTag.isNullOrEmpty()) {
            targetFragmentTag
        } else {
            //默认加载 第一个
            if (defaultSelectTabId != null) {
                tabs.firstOrNull { it.tabId == defaultSelectTabId }?.tag
            } else {
                tabs.firstOrNull()?.tag
            }
        }
        if (targetFragmentTag.isNullOrBlank()) {
            return
        }
        var fragmentTransaction: FragmentTransaction? = null
        tabs.forEach { tab ->
            val tabFragment: Fragment? = fragmentManager.findFragmentByTag(tab.tag)
            if (tabFragment != null && !tabFragment.isDetached && tab.tag != targetFragmentTag) {
                if (fragmentTransaction == null) {
                    fragmentTransaction = fragmentManager.beginTransaction().also {
                        it.setReorderingAllowed(true)
                    }
                }
                fragmentTransaction?.detach(tabFragment)
            }
        }
        fragmentTransaction = navigateToTargetFragment(targetFragmentTag, fragmentTransaction)
        fragmentTransaction?.commitNowAllowingStateLoss()
    }

    private fun navigateToTargetFragment(
        targetFragmentTag: String?, fragmentTransaction: FragmentTransaction?
    ): FragmentTransaction? {
        val newFragmentTransaction = fragmentTransaction ?: fragmentManager.beginTransaction()
            .apply {
                setReorderingAllowed(true)
            }
        val targetTab = tabs.firstOrNull { it.tag == targetFragmentTag } ?: return null
        tabStateListener?.invoke(tabs, targetTab.tabId)
        if (currentFragmentTag != targetTab.tag) {
            if (!currentFragmentTag.isNullOrEmpty()) {
                fragmentManager.findFragmentByTag(
                    currentFragmentTag
                )?.let {
                    newFragmentTransaction.detach(it)
                }
            } else {
                // 日夜切换导致 containerId 上还存在 fragment, 页面没有回收干净……
                // 需要将 fragment detach 处理
                val findFragment = fragmentManager.findFragmentById(containerId)
                if (findFragment != null && findFragment.tag != targetTab.tag) {
                    if (!fragmentManager.isStateSaved) {
                        newFragmentTransaction.detach(findFragment)
                    }
                }
            }
            val targetFragment: Fragment? = fragmentManager.findFragmentByTag(targetTab.tag)
            if (targetFragment == null) {
                val arguments = targetTab.args ?: Bundle()
                newFragmentTransaction.add(
                    containerId, targetTab.fragmentClass, arguments, targetTab.tag
                )
                //Log.w("TTFragmentTabDelegate", "navigateToTargetFragment add " + targetTab.tag);
            } else {
                newFragmentTransaction.attach(targetFragment)
                //Log.w("TTFragmentTabDelegate", "navigateToTargetFragment attach " + targetTab.tag);
            }
            currentFragmentTag = targetTab.tag
            tabChangeListener?.invoke(targetTab.tabId)
        }
        return newFragmentTransaction
    }

    fun addTab(tabInfo: T) {
        tabs.add(tabInfo)
    }

    fun setupData(list: List<T>) {
        this.tabs.clear()
        if (!list.isNullOrEmpty()) {
            tabs.addAll(list)
        }
    }

    fun setupData(vararg data: T) {
        this.tabs.clear()
        if (!data.isNullOrEmpty()) {
            tabs.addAll(data)
        }
    }

    fun getTagByPosition(position: Int) = tabs.getOrNull(position)?.tag

    fun getSaveState() = Bundle().apply {
        Log.w("SkyFragmentTab", "getSaveState currentFragmentTag = $currentFragmentTag")
        putString(BUNDLE_CURRENT_FRAGMENT_TAG, currentFragmentTag)
    }

    fun restoreState(bundle: Bundle) {
        val currentFragmentTag: String? = bundle.getString(BUNDLE_CURRENT_FRAGMENT_TAG)
        Log.e("SkyFragmentTab", "restoreState currentFragmentTag = $currentFragmentTag")
        if (!currentFragmentTag.isNullOrEmpty()) {
            setCurrentTab(currentFragmentTag)
        }
    }

    fun onTabClick(v: View) {
        tabClickListener?.onTabClick?.invoke(v)
    }

    @JvmField var isTabEmpty: Boolean = tabs.isNullOrEmpty()

    interface TabClickListener {
        val onTabClick: (View) -> Unit
        val allowTabChanged: (Int) -> Boolean
        val onSameTabClicked: (Int) -> Unit
    }

    open class TabInfo(
        val tabId: Int, val fragmentClass: Class<out Fragment>, val args: Bundle?
    ) {
        val tag: String = "tab_host:" + fragmentClass.name + ":" + tabId
    }

    companion object {
        private const val BUNDLE_CURRENT_FRAGMENT_TAG = "TTFragmentTabDelegate.FRAGMENT_TAG"
    }
}