package com.small.world.fiction.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import com.aiso.qfast.base.BaseActivity
import com.aiso.qfast.base.R
import com.aiso.qfast.base.ext.doOnApplyWindowInsets
import com.aiso.qfast.base.ext.showSystemBars
import com.aiso.qfast.utils.MMKVHelper
import com.aiso.qfast.utils.dialog.DialogUtil
import com.aiso.qfast.utils.toJson
import com.blankj.utilcode.util.GsonUtils
import com.google.firebase.auth.FirebaseAuth
import com.small.world.fiction.adapter.BookShellAdapter
import com.small.world.fiction.bean.Chapter
import com.small.world.fiction.bean.ChapterGenerationDetails
import com.small.world.fiction.bean.ChapterRequestDetails
import com.small.world.fiction.bean.ChoicesStatus
import com.small.world.fiction.bean.ContentStatus
import com.small.world.fiction.bean.CreateFictionBean
import com.small.world.fiction.bean.LastReadNovelBean
import com.small.world.fiction.bean.Review
import com.small.world.fiction.bean.UrlStatus
import com.small.world.fiction.config.ChapterConfig
import com.small.world.fiction.config.TextConfig
import com.small.world.fiction.databinding.ActivityBookShellBinding
import com.small.world.fiction.decoration.SpaceItemDecoration
import com.small.world.fiction.fragment.StartCreateFictionFragment
import com.small.world.fiction.viewmodel.FictionViewModel
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class BookShellActivity : BaseActivity<ActivityBookShellBinding>() {

    private val viewModel: FictionViewModel by viewModels()

    private val TAG = "BookShellActivity"

    val listData = mutableListOf<CreateFictionBean>()

    val adapter by lazy {
        BookShellAdapter()
    }

    override fun createBinding(layoutInflater: LayoutInflater): ActivityBookShellBinding {
        return ActivityBookShellBinding.inflate(layoutInflater)
    }

    override fun initView() {
        super.initView()
        initWindows()
        initViewModels()
        binding.bookShellRecycler.layoutManager = GridLayoutManager(this, 3)
        binding.bookShellRecycler.addItemDecoration(SpaceItemDecoration(20, 20))
        binding.bookShellRecycler.adapter = adapter
        adapter.createBookClick = {
            DialogUtil.show(StartCreateFictionFragment(), supportFragmentManager)
        }
        adapter.bookDataClick = {
            val chapterList = ChapterConfig.chapterData
            if(chapterList.isNotEmpty()){
                //获取上一次的阅读数据
                val lastReadData = MMKVHelper.getGlobalInstance().getString(it.novel_id ?: "")
                if(lastReadData?.isNotBlank() == true){
                    val lastReadBean = GsonUtils.fromJson(lastReadData, LastReadNovelBean::class.java)
                    //获取对应的章节
                    for(chapter in ChapterConfig.chapterData){
                        if(chapter.chapter_id == lastReadBean.chapter_id){
                            val intent = Intent(this, ReadBookActivity::class.java)
                            intent.putExtra("novel", it.toJson())
                            intent.putExtra("chapter", chapter.toJson())
                            intent.putExtra("defaultPage", lastReadBean.lastPageIndex)
                            startActivity(intent)
                            break
                        }
                    }
                }else {
                    for (chapter in chapterList) {
                        if (chapter.novel_id == it.novel_id &&
                            chapter.chapter_num == 1 &&
                            chapter.prev_chapter_id == "none" &&
                            chapter.content?.status == TextConfig.GENERATED
                        ) {
                            //这个时候给novel的数据和chapter的数据都得带到阅读页面
                            //主要是方便用户自行创建章节的时候数据的组合
                            val intent = Intent(this, ReadBookActivity::class.java)
                            intent.putExtra("novel", it.toJson())
                            intent.putExtra("chapter", chapter.toJson())
                            startActivity(intent)
                            break
//                        initLittleData(it)
                        }
                    }
                }
            }
        }
        binding.bookShellTopIcon.setOnClickListener {
            startActivity(Intent(this, ReadBookActivity::class.java))
        }
    }

    private fun initLittleData(it: CreateFictionBean) {
        for (index in 1..2){
            //获取用户的邮箱
            val chapterId = UUID.randomUUID().toString()
            val user = FirebaseAuth.getInstance().currentUser
            val userEmail = user?.email
            val newChapter = Chapter(
                chapter_id = chapterId,
                novel_id = it.novel_id,
                prev_chapter_id = "none",
                chapter_num = 1,
                requested_by = userEmail,
                requested_at = getFormattedTime(),
                request_details = ChapterRequestDetails(
                    user_comment = "",
                    generated_comment = if(index == 1) {
                        "男主死了，然后被女主所救，女主被女主所救，然后被男主所救，然后被男主所救，然后被女主所救，然后被女主所救，然后被男主所救"
                    } else {
                        "女主疯了，女主妈妈过来找男主拼命，"
                    }
                ),
                generation_details = ChapterGenerationDetails(
                    chapter_type = "innovative",
                    content_gen_prompt = TextConfig.FIRST_CHAPTER_GEN
                ),
                title = ContentStatus(
                    status = "generated",
                    content = "第一章 消失的台阶"
                ),
                content = ContentStatus(
                    status = "generated",
                    content = TextConfig.pageContent
                ),
                cover = UrlStatus(
                    status = "generated",
                    url = if(index == 1)
                        "https://t9.baidu.com/it/u=3248765920,1556421017&fm=3031&app=3031&size=r3,4&q=100&n=0&g=11n&f=JPEG&fmt=auto&maxorilen2heic=2000000?s=90D37582C0040DFFCA1868B30300D092"
                    else
                        "https://pics1.baidu.com/feed/cf1b9d16fdfaaf51c59be16383bf62e1f21f7ae8.jpeg@f_auto?token=e7654b966138814cf4381e1300af2f74"
                ),
                audiobook = UrlStatus(
                    status = "pending"
                ),
                videobook = UrlStatus(
                    status = "pending"
                ),
                trailer = UrlStatus(
                    status = "pending"
                ),
                next_chapter_choices = ChoicesStatus(
                    status = "pending",
                    choices = listOf("none")
                ),
                review = Review(
                    like_count = 0,
                    neutral_count = 0,
                    dislike_count = 0
                )
            )
            viewModel.createChapter(chapterId, newChapter)
        }
    }

    private fun initViewModels() {
        viewModel.viewModelLaunch(Dispatchers.Main) {
            viewModel.bookShellData.collect {
                if(it != null && it.isSuccess){
                    listData.clear()
                    listData.add(CreateFictionBean(itemType = 2))
                    listData.addAll(it.getOrNull()?.toMutableList<CreateFictionBean>() ?: mutableListOf())
                    listData.let { bookShellList ->
                        adapter.setData(bookShellList)
                    }
                }
            }
        }
        viewModel.getBookShellData()
        viewModel.viewModelLaunch(Dispatchers.Main){
            viewModel.chapterData.collect {
                ChapterConfig.chapterData = it?.getOrNull()?.toMutableList() ?: mutableListOf()

            }
        }
        viewModel.getChapterData()
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedTime(): String {
        val currentTime = Date()
        val timeZone = TimeZone.getDefault()
        val sdf = SimpleDateFormat("yyyy年M月d日 'UTC'XXX HH:mm:ss", Locale.CHINA)
        sdf.timeZone = timeZone
        return sdf.format(currentTime)
    }

    private fun initWindows(){
        window.showSystemBars(
            navigationBarColor = resources.getColor(R.color.main_color),
        )
        binding.root.doOnApplyWindowInsets { view, windowInsetsCompat ->
            val statusBarHeight = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navigationBarHeight = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars()).bottom
            view.updatePadding(top = statusBarHeight, bottom = navigationBarHeight+20)
        }
    }

}