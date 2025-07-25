package com.small.world.fiction.fragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.aiso.qfast.base.BaseBottomSheetDialogFragment
import com.aiso.qfast.base.dialog.AppAlertDialog
import com.aiso.qfast.base.ext.viewBinding
import com.aiso.qfast.utils.LogUtils
import com.aiso.qfast.utils.setNoFastClickListener
import com.google.firebase.auth.FirebaseAuth
import com.small.world.fiction.R
import com.small.world.fiction.adapter.SelectTypeAdapter
import com.small.world.fiction.bean.Chapter
import com.small.world.fiction.bean.ChapterGenerationDetails
import com.small.world.fiction.bean.ChapterRequestDetails
import com.small.world.fiction.bean.ChoicesStatus
import com.small.world.fiction.bean.ContentStatus
import com.small.world.fiction.bean.Cover
import com.small.world.fiction.bean.CreateFictionBean
import com.small.world.fiction.bean.GenerationDetails
import com.small.world.fiction.bean.OutlineBean
import com.small.world.fiction.bean.RequestDetails
import com.small.world.fiction.bean.Review
import com.small.world.fiction.bean.Title
import com.small.world.fiction.bean.Trailer
import com.small.world.fiction.bean.UrlStatus
import com.small.world.fiction.config.TextConfig
import com.small.world.fiction.databinding.CreateFictionSheetFragmentBinding
import com.small.world.fiction.decoration.SpaceItemDecoration
import com.small.world.fiction.viewmodel.FictionViewModel
import com.small.world.fiction.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class StartCreateFictionFragment: BaseBottomSheetDialogFragment(R.layout.create_fiction_sheet_fragment) {

    private val viewModel: FictionViewModel by viewModels()

    private val userViewModel: UserViewModel by viewModels()

    private val binding by viewBinding(CreateFictionSheetFragmentBinding::bind)

    private val topicAdapter by lazy {
        SelectTypeAdapter()
    }
    private val topicSelectedList = mutableListOf<String>()

    private val roleAdapter by lazy {
        SelectTypeAdapter()
    }
    private val roleSelectedList = mutableListOf<String>()

    private val plotsAdapter by lazy {
        SelectTypeAdapter()
    }
    private val plotsSelectedList = mutableListOf<String>()

    private var tabType = "全部"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initViewClick()
        initViewModels()
    }

    private fun initViewModels() {
        viewModel.viewModelLaunch(Dispatchers.Main) {
            viewModel.createFictionResult.collect { result ->
                if(result != null){
                    LogUtils.d("result=========: $result")
                    dismiss()
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.selectTopicRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.selectTopicRecycler.addItemDecoration(SpaceItemDecoration(20,20))
        binding.selectTopicRecycler.adapter = topicAdapter
        topicAdapter.selectTypeClick = {
            if(topicSelectedList.contains(it)){
                topicSelectedList.remove(it)
            }else{
                topicSelectedList.add(it)
            }
            topicAdapter.setSelectedList(topicSelectedList)
        }
        topicAdapter.setData(TextConfig.TOPIC_ARRAY)

        binding.selectRoleRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.selectRoleRecycler.addItemDecoration(SpaceItemDecoration(20,20))
        binding.selectRoleRecycler.adapter = roleAdapter
        roleAdapter.selectTypeClick = {
            if(roleSelectedList.contains(it)){
                roleSelectedList.remove(it)
            }else {
                roleSelectedList.add(it)
            }
            roleAdapter.setSelectedList(roleSelectedList)
        }
        roleAdapter.setData(TextConfig.ROLES_ARRAY)

        binding.selectPlotsRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.selectPlotsRecycler.addItemDecoration(SpaceItemDecoration(20,20))
        binding.selectPlotsRecycler.adapter = plotsAdapter
        plotsAdapter.selectTypeClick = {
            if (plotsSelectedList.contains(it)){
                plotsSelectedList.remove(it)
            } else {
                plotsSelectedList.add(it)
            }
            plotsAdapter.setSelectedList(plotsSelectedList)
        }
        plotsAdapter.setData(TextConfig.PLOTS_ARRAY)
    }

    @Suppress("INFERRED_TYPE_VARIABLE_INTO_POSSIBLE_EMPTY_INTERSECTION")
    private fun initViewClick() {
        binding.createCancelBtn.setNoFastClickListener {
            dismiss()
        }
        binding.randomGenerateBtn.setNoFastClickListener {
            //从topic中取出三个元素
            val topicList = TextConfig.TOPIC_ARRAY.shuffled().take(3)
            topicSelectedList.addAll(topicList)
            topicAdapter.setSelectedList(topicSelectedList)

            val roleList = TextConfig.ROLES_ARRAY.shuffled().take(3)
            roleSelectedList.addAll(roleList)
            roleAdapter.setSelectedList(roleSelectedList)

            val plotsList = TextConfig.PLOTS_ARRAY.shuffled().take(3)
            plotsSelectedList.addAll(plotsList)
            plotsAdapter.setSelectedList(plotsSelectedList)
        }
        binding.tabAll.setNoFastClickListener {
            tabType = "全部"
            binding.tabAll.isSelected = true
            binding.tabAll.setBackgroundResource(R.drawable.bg_tab_selected)
            binding.tabMale.setBackgroundResource(R.drawable.transparent_shape)
            binding.tabFemale.setBackgroundResource(R.drawable.transparent_shape)
        }
        binding.tabMale.setNoFastClickListener {
            tabType = "男生"
            binding.tabMale.isSelected = true
            binding.tabMale.setBackgroundResource(R.drawable.bg_tab_selected)
            binding.tabAll.setBackgroundResource(R.drawable.transparent_shape)
            binding.tabFemale.setBackgroundResource(R.drawable.transparent_shape)
        }
        binding.tabFemale.setNoFastClickListener {
            tabType = "女生"
            binding.tabFemale.isSelected = true
            binding.tabFemale.setBackgroundResource(R.drawable.bg_tab_selected)
            binding.tabAll.setBackgroundResource(R.drawable.transparent_shape)
            binding.tabMale.setBackgroundResource(R.drawable.transparent_shape)
        }
        binding.startCreateFiction.setNoFastClickListener {
            val fictionTitle = binding.creativeTitleEdit.text.toString()
            if(fictionTitle.isBlank()){
                //弹出来确认是否自动创建的标题
                AppAlertDialog.Builder(requireContext())
                    .setTitle("确认是否自动生成标题？")
                    .setMessage("系统根据您选择的标签，自动帮您生成一个小说标题，后续可自行修改，是否继续？")
                    .setCancelable(true)
                    .setNegativeButton("自定义标题")
                    .setPositiveButton("继续",object : DialogInterface.OnClickListener{
                        override fun onClick(
                            dialog: DialogInterface?,
                            which: Int
                        ) {
                            var fictionTitle = "[标题生成中]$tabType、${topicSelectedList.joinToString("、")}"
                            fictionTitle.apply {
                                substring(0,16)
                            }
                            //生成标题
                            submitFictionData(fictionTitle)
                        }
                    }).show()
                return@setNoFastClickListener
            }
            submitFictionData(fictionTitle)
        }
    }

    private fun submitFictionData(title:String){
        var fictionDesc:String? = binding.creativeCoreEdit.text.toString()
        if(fictionDesc?.isBlank() == true){
            fictionDesc = null
        }
        val uuid = UUID.randomUUID().toString()
        //获取用户的邮箱
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email
        val fictionBean = CreateFictionBean(
            requested_by = userEmail,
            requested_at = getFormattedTime(),
            request_details = RequestDetails(
                targetAudience = tabType,
                plots = plotsSelectedList,
                roles = roleSelectedList,
                themes = topicSelectedList
            ),
            generation_details = GenerationDetails(
                outline_gen_prompt = fictionDesc
            ),
            title = Title(
                status = "generated",
                content = title
            ),
            outline = OutlineBean(
                status = "pending"
            ),
            cover = Cover(
                status = "generated",
                url = "https://bkimg.cdn.bcebos.com/pic/b151f8198618367a26974c6625738bd4b31ce56b"
            ),
            trailer = Trailer(
                status = "pending"
            )
        )

        val chapterId = UUID.randomUUID().toString()
        val chapter = Chapter(
            chapter_id = chapterId,
            novel_id = uuid,
            prev_chapter_id = "none",
            chapter_num = 1,
            requested_by = userEmail,
            requested_at = getFormattedTime(),
            request_details = ChapterRequestDetails(
                user_comment = ""
            ),
            generation_details = ChapterGenerationDetails(
                content_gen_prompt = TextConfig.FIRST_CHAPTER_GEN
            ),
            title = ContentStatus(
                status = "generated",
                content = "第1章  暴富前的准备"
            ),
            content = ContentStatus(
                status = "generated",
                content = TextConfig.pageContent
            ),
            cover = UrlStatus(
                status = "generated",
                url = "https://bkimg.cdn.bcebos.com/pic/b151f8198618367a26974c6625738bd4b31ce56b"
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

        viewModel.createFiction(uuid,fictionBean)
        viewModel.createChapter(chapterId, chapter)
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedTime(): String {
        val currentTime = Date()
        val timeZone = TimeZone.getDefault()
        val sdf = SimpleDateFormat("yyyy年M月d日 'UTC'XXX HH:mm:ss", Locale.CHINA)
        sdf.timeZone = timeZone
        return sdf.format(currentTime)
    }

}