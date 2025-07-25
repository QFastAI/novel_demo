package com.small.world.fiction.activity

import android.content.Intent
import android.view.LayoutInflater
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.aiso.qfast.base.BaseActivity
import com.aiso.qfast.base.ext.doOnApplyWindowInsets
import com.aiso.qfast.base.ext.showSystemBars
import com.aiso.qfast.utils.LogUtils
import com.aiso.qfast.utils.dialog.DialogUtil
import com.aiso.qfast.utils.setNoFastClickListener
import com.aiso.qfast.utils.toJson
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.small.world.fiction.adapter.OtherConsumeChapterAdapter
import com.small.world.fiction.bean.Chapter
import com.small.world.fiction.bean.ChapterGenerationDetails
import com.small.world.fiction.bean.ChapterRequestDetails
import com.small.world.fiction.bean.ContentStatus
import com.small.world.fiction.bean.CreateFictionBean
import com.small.world.fiction.bean.RequestDetails
import com.small.world.fiction.bean.Review
import com.small.world.fiction.bean.UrlStatus
import com.small.world.fiction.config.ChapterConfig
import com.small.world.fiction.config.TextConfig
import com.small.world.fiction.databinding.ActivityFictionEvaluateBinding
import com.small.world.fiction.fragment.DislikeChapterFragment
import com.small.world.fiction.viewmodel.FictionViewModel

class FictionEvaluateActivity : BaseActivity<ActivityFictionEvaluateBinding>() {

    private var bookShellBean: CreateFictionBean? = null
    private var chapterDataBean: Chapter? = null

    private val viewModel by lazy {
        FictionViewModel()
    }

    private val otherConsumeChapters = mutableListOf<Chapter>()

    private val adapter by lazy {
        OtherConsumeChapterAdapter()
    }

    override fun createBinding(layoutInflater: LayoutInflater): ActivityFictionEvaluateBinding {
        return ActivityFictionEvaluateBinding.inflate(layoutInflater)
    }

    override fun initView() {
        super.initView()
        bookShellBean = GsonUtils.fromJson(intent.getStringExtra("novel"), CreateFictionBean::class.java)
        chapterDataBean = GsonUtils.fromJson(intent.getStringExtra("chapter"), Chapter::class.java)
        initWindows()
        initViewClick()
        initOtherData()
    }

    private fun initOtherData() {
//        val chapters = ChapterConfig.chapterData
//        if(chapters.isNotEmpty()){
//            for (chapter in chapters){
//                if(chapter.chapter_id != chapterDataBean?.chapter_id &&
//                    chapter.novel_id == bookShellBean?.novel_id &&
//                    chapterDataBean?.chapter_num == chapter.chapter_num &&
//                    chapter.generation_details?.chapter_type == TextConfig.CREATIVITY){
//                    otherConsumeChapters.add(chapter)
//                }
//            }
//        }
        binding.evaluateOtherConsumeRecycler.layoutManager = LinearLayoutManager(this)
//        adapter.setData(otherConsumeChapters)
        adapter.onReadClickEvent = {
            ActivityUtils.finishActivity(ReadBookActivity::class.java)
            val  intent = Intent(this, ReadBookActivity::class.java).apply {
                putExtra("novel", GsonUtils.toJson(bookShellBean))
                putExtra("chapter", GsonUtils.toJson(it))
            }
            startActivity(intent)
        }
        binding.evaluateOtherConsumeRecycler.adapter = adapter
    }

    private fun initViewClick() {
        binding.let {
            it.evaluateBackIcon.setNoFastClickListener {
                finish()
            }
            it.evaluateOrdinaryBtn.setNoFastClickListener {
                showLikeLayout(1)
//                jumpToNextChapter()
            }
            it.evaluateLikeBtn.setNoFastClickListener {
                showLikeLayout(1)
            }
            it.evaluateDislikeBtn.setNoFastClickListener {
                showLikeLayout(2)
            }
            it.goCreateConsune.setNoFastClickListener {
                //弹出自创弹窗
                val dislikeChapterFragment = DislikeChapterFragment()
                dislikeChapterFragment.onContinueNextChapter = {
                    jumpToNextChapter()
                }
                dislikeChapterFragment.submitChapterCoreEvent = { submitType,chapterCore ->
                    //这里用户填写了自己的创作核心，需要添加一条新的章节数据
                    val createChapter = chapterDataBean?.copy()
                    createChapter?.apply {

                        request_details = ChapterRequestDetails(
                            user_comment = "",
                            generated_comment = chapterCore
                        )
                        content = ContentStatus(
                            status = TextConfig.PENDING
                        )
                        cover = UrlStatus(
                            status = TextConfig.PENDING
                        )
                        audiobook = UrlStatus(
                            status = TextConfig.PENDING
                        )
                        videobook = UrlStatus(
                            status = TextConfig.PENDING
                        )
                        trailer = UrlStatus(
                            status = TextConfig.PENDING
                        )
                        review = Review(//因为需要生成新的章节，则需要把所有的用户操作数量置0
                            like_count = 0,
                            neutral_count = 0,
                            dislike_count = 0
                        )
                    }
                    LogUtils.d("createNewChapter====${createChapter.toJson()}")
                    viewModel.createChapter(chapterDataBean?.chapter_id ?: "",createChapter!!)
                }
                DialogUtil.show(dislikeChapterFragment,supportFragmentManager)
            }
        }
    }

    /**
     * type: 1 喜欢 一般   2  不喜欢
     */
    fun showLikeLayout(type:Int){
        showRecommend()
        when(type){
            1 -> {
                binding.otherConsumeText.text = "为您推荐一下章节"
                val chapterList = mutableListOf<Chapter>()
                //获取系统生成三个方向的章节
                ChapterConfig.chapterData.forEach { chapterData ->
                    if(chapterDataBean?.novel_id == chapterData.novel_id &&
                        chapterData.request_details?.generated_comment?.isBlank() == true&&
                        chapterData.chapter_num == ((chapterDataBean?.chapter_num ?: 0)+1)){
                        chapterList.add(chapterData)
                    }
                }
                //跟新适配器的数据
                adapter.setData(chapterList)
            }
            2 -> {
                binding.otherConsumeText.text = "为您推荐其他读者的创意"
                val chapterList = mutableListOf<Chapter>()
                //获取系统生成三个方向的章节
                ChapterConfig.chapterData.forEach { chapterData ->
                    if(chapterDataBean?.novel_id == chapterData.novel_id &&
                        chapterData.request_details?.generated_comment?.isNotBlank() == true&&
                        chapterData.chapter_num == chapterDataBean?.chapter_num){
                        chapterList.add(chapterData)
                    }
                }
                //跟新适配器的数据
                adapter.setData(chapterList)
            }
        }
    }

    fun showRecommend(){
        binding.otherConsumeText.isVisible = true
        binding.otherConsumeLayout.isVisible = true

        binding.evaluateBackTip.isVisible = false
        binding.evaluateOperationLayout.isVisible = false
    }

    private fun jumpToNextChapter(jumpType : Int = 0) {
        ChapterConfig.chapterData.forEach { chapter ->
            if(chapter.novel_id == bookShellBean?.novel_id &&
                chapter.chapter_num == ((chapterDataBean?.chapter_num ?:0)+1)
                && chapter.generation_details?.chapter_type != TextConfig.CREATIVITY){
                //0 是喜欢的类型  1是一般的类型
                ActivityUtils.finishActivity(ReadBookActivity::class.java)
                val  intent = Intent(this, ReadBookActivity::class.java).apply {
                    putExtra("novel", GsonUtils.toJson(bookShellBean))
                    putExtra("chapter", GsonUtils.toJson(chapter))
                }
                startActivity(intent)
                //喜欢，一般，或者从创建章节进入的，则直接跳转下一章并结束当前页面
                this@FictionEvaluateActivity.finish()
            }
        }
    }

    private fun initWindows() {
        window.showSystemBars(
            navigationBarColor = resources.getColor(com.aiso.qfast.base.R.color.main_color),
        )
        binding.root.doOnApplyWindowInsets { view, windowInsetsCompat ->
            val statusBarHeight =
                windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navigationBarHeight =
                windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars()).bottom
            view.updatePadding(top = statusBarHeight, bottom = navigationBarHeight + 20)
        }
    }
}