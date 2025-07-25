package com.aiso.qfast.base.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.parcelize.Parcelize

@SuppressLint("NewApi")
class FragmentTabHost @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val delegate by lazy(LazyThreadSafetyMode.NONE) {
        FragmentTabDelegate<FragmentTabDelegate.TabInfo>().apply {
            tabStateListener = { tabs, tabId ->
                tabs.forEach {
                    this@FragmentTabHost.findViewById<View>(it.tabId).isActivated =
                        it.tabId == tabId
                }
            }
        }
    }

    fun setupManager(manager: FragmentManager, containerId: Int): FragmentTabHost {
        delegate.setupManager(manager, containerId)
        return this
    }

    fun addTab(tabInfo: FragmentTabDelegate.TabInfo): FragmentTabHost {
        findViewById<View>(tabInfo.tabId).setOnClickListener(this)
        delegate.addTab(tabInfo)
        return this
    }

    fun addTab(
        tabId: Int, fragmentClass: Class<out Fragment>, fragmentArgs: Bundle?
    ): FragmentTabHost {
        findViewById<View>(tabId).setOnClickListener(this)
        delegate.addTab(FragmentTabDelegate.TabInfo(tabId, fragmentClass, fragmentArgs))
        return this
    }

    fun setTabClickListener(listener: FragmentTabDelegate.TabClickListener): FragmentTabHost {
        delegate.tabClickListener = listener
        return this
    }

    fun setTabChangeListener(listener: ((Int) -> Unit)): FragmentTabHost {
        delegate.tabChangeListener = listener
        return this
    }

    /**
     * 会覆盖 控件 初始化生成的 listener ，处理 isActivated 逻辑
     * */
    fun setTabStateChangeListener(listener: ((List<FragmentTabDelegate.TabInfo>, Int) -> Unit)?): FragmentTabHost {
        delegate.tabStateListener = listener
        return this
    }

    override fun onClick(v: View) {
        val tabId = v.id
        setCurrentTab(tabId)
        delegate.onTabClick(v)
    }

    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        delegate.onAttach()
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        delegate.onDetach()
    }

    fun setCurrentTab(tabId: Int) {
        delegate.setCurrentTab(tabId)
    }

    fun getTagByPosition(tabPosition: Int) = delegate.getTagByPosition(tabPosition)

    fun tabs(): List<FragmentTabDelegate.TabInfo> {
        return delegate.tabs()
    }

    val isTabEmpty: Boolean
        get() = delegate.isTabEmpty

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState(), delegate.getSaveState())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        delegate.restoreState(state.bundle)
    }

    @Parcelize
    class SavedState(
        var _state: Parcelable?, var bundle: Bundle
    ) : View.BaseSavedState(_state)
}