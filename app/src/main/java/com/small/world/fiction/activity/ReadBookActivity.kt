package com.small.world.fiction.activity

import android.content.Intent
import android.view.LayoutInflater
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.aiso.qfast.base.BaseActivity
import com.aiso.qfast.base.ext.doOnApplyWindowInsets
import com.aiso.qfast.base.ext.showSystemBars
import com.aiso.qfast.utils.MMKVHelper
import com.aiso.qfast.utils.toJson
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.small.world.fiction.bean.Chapter
import com.small.world.fiction.bean.CreateFictionBean
import com.small.world.fiction.bean.LastReadNovelBean
import com.small.world.fiction.config.TextConfig
import com.small.world.fiction.databinding.ActivityReadBookBinding


class ReadBookActivity : BaseActivity<ActivityReadBookBinding>() {
    override fun createBinding(layoutInflater: LayoutInflater): ActivityReadBookBinding {
        return ActivityReadBookBinding.inflate(layoutInflater)
    }

    private var bookShellBean: CreateFictionBean? = null
    private var chapterDataBean: Chapter? = null


    private var currentPageIndex = 0

    private var defaultPage = 0

    override fun initView() {
        super.initView()
        defaultPage = intent.getIntExtra("defaultPage", 0)
        bookShellBean = GsonUtils.fromJson(intent.getStringExtra("novel"), CreateFictionBean::class.java)
        chapterDataBean = GsonUtils.fromJson(intent.getStringExtra("chapter"), Chapter::class.java)
        initWindows()
        setDataContent()
    }

    private fun setDataContent() {
        binding.bookPageView.let {
            it.setTitleAndText(chapterDataBean?.title?.content ?: "", chapterDataBean?.content?.content ?: "",defaultPage)
            it.setOnPageChangeListener { current, total ->
                currentPageIndex = current
            }
            it.onEndNextEvent = {
                if(ActivityUtils.isActivityExistsInStack(FictionEvaluateActivity::class.java)){
                    ActivityUtils.finishActivity(FictionEvaluateActivity::class.java)
                }
                startActivity(Intent(this, FictionEvaluateActivity::class.java).apply {
                    putExtra("novel", GsonUtils.toJson(bookShellBean))
                    putExtra("chapter", GsonUtils.toJson(chapterDataBean))
                })
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

    override fun onPause() {
        val lastReadNovelBean = LastReadNovelBean(
            novel_id = chapterDataBean?.novel_id ?: "",
            chapter_id = chapterDataBean?.chapter_id ?: "",
            lastPageIndex = currentPageIndex-1
        )
        MMKVHelper.getGlobalInstance().putCommon(chapterDataBean?.novel_id ?: "", lastReadNovelBean.toJson())
        super.onPause()
    }

}

