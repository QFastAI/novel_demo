package com.small.world.fiction.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aiso.qfast.utils.ImageLoader.loadImage
import com.aiso.qfast.utils.setNoFastClickListener
import com.small.world.fiction.bean.BookShellBean
import com.small.world.fiction.bean.CreateFictionBean
import com.small.world.fiction.config.TextConfig
import com.small.world.fiction.databinding.BookShellItemBinding
import com.small.world.fiction.databinding.CreateBookItemBinding

class BookShellAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var bookShellList = mutableListOf<CreateFictionBean>()

    var createBookClick: ((CreateFictionBean) -> Unit)? = null

    var bookDataClick: ((CreateFictionBean) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(bookShellList: MutableList<CreateFictionBean>){
        this.bookShellList = bookShellList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        when(viewType){
            2 -> {
                return CreateBookHolder(
                    CreateBookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    createBookClick
                )
            }
            else -> {
                return BookShellHolder(
                    BookShellItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    bookDataClick
                )
            }
        }
        return CreateBookHolder(
            CreateBookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            createBookClick
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if(holder is CreateBookHolder){
            holder.onBind(bookShellList[position])
        }else if(holder is BookShellHolder){
            holder.onBind(bookShellList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return bookShellList[position].itemType
    }

    override fun getItemCount(): Int {
        return bookShellList.size
    }
}

class CreateBookHolder(
    private val binding: CreateBookItemBinding,
    private val createBookClick: ((CreateFictionBean) -> Unit)?
): RecyclerView.ViewHolder(binding.root){
    fun onBind(bookShellBean: CreateFictionBean){
        binding.root.setNoFastClickListener {
            createBookClick?.invoke(bookShellBean)
        }
    }
}

class BookShellHolder(
    private val binding: BookShellItemBinding,
    private val bookDataClick: ((CreateFictionBean) -> Unit)?
): RecyclerView.ViewHolder(binding.root){
    fun onBind(bookShellBean: CreateFictionBean) {
        binding.bookShellItemName.text = bookShellBean.title?.content ?: ""

        if (bookShellBean.cover?.status == TextConfig.GENERATED) {
            binding.bookShellItemCover.isVisible = true
            binding.bookShellItemCover.loadImage(
                bookShellBean.cover?.url ?: "",
                topleft = 40.0f,
                topright = 40.0f,
                bottomright = 40.0f,
                bottomleft = 40.0f
            )
            binding.bookItemWaitText.isVisible = false
        }

        binding.root.setNoFastClickListener {
            bookDataClick?.invoke(bookShellBean)
        }
    }
}

