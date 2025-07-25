package com.aiso.qfast.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T: ViewBinding>: Fragment() {

    private var _binding:T? = null

    val binding:T get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(_binding == null){
            _binding = createBinding(inflater,container)
        }
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            initView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    abstract fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ):T

    open fun initView(){}

}