package com.small.world.fiction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aiso.qfast.utils.ImageLoader.loadImage
import com.aiso.qfast.utils.setNoFastClickListener
import com.small.world.fiction.bean.Chapter
import com.small.world.fiction.databinding.OtherConsumeChapterItemBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class OtherConsumeChapterAdapter: RecyclerView.Adapter<OtherConsumeChapterViewHolder>() {

    var listData = listOf<Chapter>()

    var onReadClickEvent: ((Chapter) -> Unit)? = null

    fun setData(listData: List<Chapter>) {
        this.listData = listData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OtherConsumeChapterViewHolder {
        return OtherConsumeChapterViewHolder(
            OtherConsumeChapterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: OtherConsumeChapterViewHolder,
        position: Int
    ) {
        holder.onBind(onReadClickEvent,listData[position])
    }

    override fun getItemCount(): Int = if(listData.size < 3)listData.size else 3
}

class OtherConsumeChapterViewHolder(
    private val binding: OtherConsumeChapterItemBinding
): RecyclerView.ViewHolder(binding.root) {
    fun onBind(
        onReadClickEvent: ((Chapter) -> Unit)?,
        chapter: Chapter
    ){
        binding.otherConsumeItemImage.loadImage(
            chapter.cover?.url ?: "",
            12f,12f,12f,12f)
        binding.otherConsumeItemTitle.text = chapter.title?.content
        binding.otherConsumeItemContent.text = chapter.generation_details?.content_gen_prompt?.trim()
        binding.otherConsumeItemTime.text = formatTime(chapter.requested_at?:"")

        binding.otherConsumeItemRead.setNoFastClickListener {
            onReadClickEvent?.invoke(chapter)
        }
    }

    fun formatTime(input: String): String {
        // 原始格式解析器（"2025年7月23日 UTC+08:00 14:27:14"）
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日 'UTC+08:00' HH:mm:ss")

        // 输出格式
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val zonedDateTime = ZonedDateTime.parse(input, inputFormatter.withZone(java.time.ZoneId.of("UTC+08:00")))
        return outputFormatter.format(zonedDateTime)
    }
}