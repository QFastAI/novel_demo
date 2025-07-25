package com.small.world.fiction.fragment

import android.os.Bundle
import android.view.View
import com.aiso.qfast.base.BaseBottomSheetDialogFragment
import com.aiso.qfast.base.ext.viewBinding
import com.aiso.qfast.utils.setNoFastClickListener
import com.small.world.fiction.R
import com.small.world.fiction.databinding.DislikeChapterFragmentBinding

class DislikeChapterFragment : BaseBottomSheetDialogFragment(R.layout.dislike_chapter_fragment) {

    val binding: DislikeChapterFragmentBinding by viewBinding(DislikeChapterFragmentBinding::bind)

    var onContinueNextChapter: () -> Unit = {}
    private var submitType = 0

    var submitChapterCoreEvent: (submitType: Int,chapterCore: String) -> Unit = {submitType,chapterCore ->}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewClick()
    }

    private fun initViewClick() {
        binding.evaluateCancelBtn.setNoFastClickListener {
            dismiss()
        }
        binding.continueNextChapter.setNoFastClickListener {
            //继续下一章
            onContinueNextChapter()
        }
        binding.tabSuggest.setNoFastClickListener {
            submitSuggest()
        }
        binding.tabPushConsume.setNoFastClickListener {
            createByUser()
        }
        binding.dislikeChapterSureBtn.setNoFastClickListener {
            //提交
            val chapterCore = binding.creativeTitleEdit.text.toString()
            submitChapterCoreEvent.invoke(submitType,chapterCore)
            dismiss()
        }
    }

    private fun submitSuggest(){
        binding.tabSuggest.setBackgroundResource(R.drawable.bg_tab_selected)
        binding.tabPushConsume.setBackgroundResource(R.drawable.transparent_shape)
        binding.creativeTitleText.text = "您想要的情节发展"
        binding.creativeTitleEdit.hint = "填写您想要的本章节情节发展"
        binding.dislikeChapterSureBtn.text = "提交"
    }

    private fun createByUser(){
        binding.tabSuggest.setBackgroundResource(R.drawable.transparent_shape)
        binding.tabPushConsume.setBackgroundResource(R.drawable.bg_tab_selected)
        binding.creativeTitleText.text = "您的创意"
        binding.creativeTitleEdit.hint = "填写您的创意"
        binding.dislikeChapterSureBtn.text = "提交创意"
    }

}