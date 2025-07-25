package com.aiso.qfast.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar

abstract class BaseActivity<T:ViewBinding> : AppCompatActivity() {

    protected lateinit var binding:T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = createBinding(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    protected abstract fun createBinding(layoutInflater:LayoutInflater):T

    open fun initView(){
        immersionBar{
            reset()
            statusBarDarkFont(true)
            navigationBarColor(R.color.main_color)
        }
    }

    open fun configImmersionBar(bar:ImmersionBar){}

}