package com.small.world.fiction.custom_views.activity

import android.view.LayoutInflater
import android.widget.Toast
import com.aiso.qfast.base.BaseActivity
import com.small.world.fiction.custom_views.view.AgentBubbleView
import com.small.world.fiction.databinding.ActivityCustomViewBinding

class CustomViewActivity : BaseActivity<ActivityCustomViewBinding>() {
    override fun createBinding(layoutInflater: LayoutInflater): ActivityCustomViewBinding {
        return ActivityCustomViewBinding.inflate(layoutInflater)
    }

    override fun initView() {
        super.initView()
        // 设置监听器
        binding.agentBubble.onBubbleClickListener = object : AgentBubbleView.OnBubbleClickListener {
            override fun onClick() {
                Toast.makeText(this@CustomViewActivity, "气泡被点击", Toast.LENGTH_SHORT).show()
            }
        }

        binding.agentBubble.onOptionSelectedListener = object : AgentBubbleView.OnOptionSelectedListener {
            override fun onOptionSelected(index: Int, option: String) {
                Toast.makeText(this@CustomViewActivity, "选择了: $option", Toast.LENGTH_SHORT).show()
                // 发送选择结果到后端
                sendOptionToBackend(index, option)
            }
        }

        binding.agentBubble.onAreaActionTriggeredListener = object : AgentBubbleView.OnAreaActionTriggeredListener {
            override fun onAreaActionTriggered(areaType: AgentBubbleView.AreaType) {
                val areaName = when (areaType) {
                    AgentBubbleView.AreaType.Area1 -> "区域1"
                    AgentBubbleView.AreaType.Area2 -> "区域2"
                    AgentBubbleView.AreaType.Area3 -> "区域3"
                    else -> "未知区域"
                }
                Toast.makeText(this@CustomViewActivity, "触发了$areaName 的AI编辑动作", Toast.LENGTH_SHORT).show()
            }
        }

        binding.agentBubble.onAreaLongPressListener = object : AgentBubbleView.OnAreaLongPressListener {
            override fun onAreaLongPressed(areaType: AgentBubbleView.AreaType) {
                val areaName = when (areaType) {
                    AgentBubbleView.AreaType.Area1 -> "区域1"
                    AgentBubbleView.AreaType.Area2 -> "区域2"
                    AgentBubbleView.AreaType.Area3 -> "区域3"
                    else -> "未知区域"
                }
                Toast.makeText(this@CustomViewActivity, "$areaName 长按响应: 不回答", Toast.LENGTH_SHORT).show()
            }
        }

        // 示例：设置选项
        val options = listOf("好吃", "一般", "不好吃")
        binding.agentBubble.setOptions(options)

        // 示例：启动加载
        // agentBubble.startLoadingAnimation()
    }

    // 发送选项到后端
    private fun sendOptionToBackend(index: Int, option: String) {
        // 模拟后端交互
        binding.agentBubble.startLoadingAnimation()

        // 模拟网络延迟
        Thread {
            Thread.sleep(2000)
            runOnUiThread {
                // 后端返回后切换模式
                binding.agentBubble.switchToChattingMode()
            }
        }.start()
    }
}