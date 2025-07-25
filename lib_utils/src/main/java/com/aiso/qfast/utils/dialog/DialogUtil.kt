package com.aiso.qfast.utils.dialog

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

object DialogUtil {

    @JvmOverloads
    @JvmStatic
    fun show(
        dialogFragment: DialogFragment,
        tag: Class<*>,
        manager: FragmentManager?,
        commitNow: Boolean = false,
    ) {
        if (manager == null || manager.isStateSaved) {
            return
        }
        val fragment = manager.findFragmentByTag(tag.name)
        if (fragment == null) {
            manager.beginTransaction().let {
                it.add(dialogFragment, tag.name)
                if (commitNow) {
                    it.commitNowAllowingStateLoss()
                } else {
                    it.commitAllowingStateLoss()
                }
            }
        }
    }

    inline fun <reified T : DialogFragment> show(
        dialogFragment: T,
        manager: FragmentManager?,
        commitNow: Boolean = false,
    ) {
        show(dialogFragment, T::class.java, manager, commitNow)
    }

    @JvmStatic
    fun dismiss(tag: Class<*>, manager: FragmentManager?) {
        if (manager == null || manager.isStateSaved) {
            return
        }
        val fragment = manager.findFragmentByTag(tag.name)
        if (fragment is DialogFragment) {
            fragment.dismissAllowingStateLoss()
        }
    }

    inline fun <reified T : DialogFragment> dismiss(manager: FragmentManager?) {
        dismiss(T::class.java, manager)
    }

    @JvmStatic
    fun exists(tag: Class<*>, manager: FragmentManager): Boolean {
        val fragment = manager.findFragmentByTag(tag.name)
        return fragment is DialogFragment
    }

    inline fun <reified T : DialogFragment> exists(manager: FragmentManager): Boolean {
        return exists(T::class.java, manager)
    }

    @JvmStatic
    fun dismissAll(manager: FragmentManager?) {
        if (manager == null || manager.isStateSaved) {
            return
        }
        val fragments = manager.fragments
        val transaction = manager.beginTransaction()
        for (fragment in fragments) {
            if (fragment is DialogFragment) {
                transaction.remove(fragment)
            }
        }
        transaction.commitAllowingStateLoss()
    }
}