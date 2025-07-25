package com.aiso.qfast.base.ext

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


fun <T : ViewBinding> Fragment.viewBinding(): FragmentViewBindingDelegate<T> {
    return FragmentViewBindingDelegate(this)
}

fun <T : ViewBinding> Fragment.viewBinding(bind: (View) -> T): FragmentViewBindingDelegate<T> {
    return FragmentViewBindingDelegate(this, bind)
}

fun Fragment.isViewBindingAvailable(): Boolean {
    val viewLifecycleOwner = viewLifecycleOwnerLiveData.value
    return viewLifecycleOwner != null
}

class FragmentViewBindingDelegate<T : ViewBinding>(
    private val fragment: Fragment, private val bindMethod: ((View) -> T)? = null
) : ReadWriteProperty<Fragment, T> {

    //private object MainHandler {
    //    private val handler = Handler(Looper.getMainLooper())
    //    fun post(action: () -> Unit): Boolean = handler.post(action)
    //}

    private var binding: T? = null

    init {
        fragment.lifecycle.addObserver(InternalLifecycleObserver())
    }

    override fun getValue(
        thisRef: Fragment, property: KProperty<*>
    ): T {
        binding?.let { return it }

        val viewLifecycleOwner = fragment.viewLifecycleOwnerLiveData.value
        val viewLifecycle = viewLifecycleOwner?.lifecycle
            ?: throw IllegalStateException("fragment 界面还未生成或者已销毁")

        check(viewLifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            "fragment 已经销毁"
        }

        return requireNotNull(bindMethod?.invoke(thisRef.requireView()), {
            "没有赋值 ViewBinding "
        }).also {
            //Log.w("ViewBindingDelegate", "初始化创建 ViewBinding ，$fragment")
            this.binding = it
        }
    }

    override fun setValue(
        thisRef: Fragment, property: KProperty<*>, value: T
    ) {
        this.binding = value
    }

    private inner class InternalLifecycleObserver : DefaultLifecycleObserver {

        private val viewLifecycleOwnerLiveDataObserver = Observer<LifecycleOwner?> {
            val viewLifecycleOwner = it
            if (viewLifecycleOwner == null) {
                binding = null
                return@Observer
            }
        }

        override fun onCreate(owner: LifecycleOwner) {
            fragment.viewLifecycleOwnerLiveData.observeForever(viewLifecycleOwnerLiveDataObserver)
            //fragment.viewLifecycleOwnerLiveData.observe(fragment, { lifecycleOwner ->
            //    lifecycleOwner?.lifecycle?.addObserver(DestroyViewLifecycleObserver())
            //})
        }

        override fun onDestroy(owner: LifecycleOwner) {
            fragment.viewLifecycleOwnerLiveData.removeObserver(viewLifecycleOwnerLiveDataObserver)
            fragment.lifecycle.removeObserver(this)
            // 以防万一，理论上 binding 已经是空的
            binding = null
        }
    }
}