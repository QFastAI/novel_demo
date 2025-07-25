package com.small.world.fiction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aiso.qfast.utils.setNoFastClickListener
import com.small.world.fiction.R
import com.aiso.qfast.base.R as BaseR
import com.small.world.fiction.adapter.SelectTypeAdapter.ItemViewHolder
import com.small.world.fiction.databinding.SelectItemLayoutBinding

class SelectTypeAdapter: RecyclerView.Adapter<ItemViewHolder>() {

    var selectTypeClick: ((String) -> Unit)? = null
    var selectTypeList: MutableList<String> = mutableListOf()

    var selectedTypeList = mutableListOf<String>()

    fun setSelectedList(selectedTypeList: MutableList<String>) {
        this.selectedTypeList = selectedTypeList
        notifyDataSetChanged()
    }

    fun setData(selectTypeList: MutableList<String>) {
        this.selectTypeList = selectTypeList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return  ItemViewHolder(
            SelectItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        holder.let {
            it.onBind(selectTypeList[position])
        }
    }

    override fun getItemCount(): Int {
        return selectTypeList.size
    }

    inner class ItemViewHolder(
        val binding: SelectItemLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root){
        fun onBind(showTitle: String) {
            binding.showItemTitle.text = showTitle
            if(selectedTypeList.contains(showTitle)){
                binding.showItemTitle.setBackgroundResource(R.drawable.random_back_shape)
            }else{
                binding.showItemTitle.setBackgroundResource(R.drawable.transparent_shape)
            }
            binding.root.setNoFastClickListener {
                selectTypeClick?.invoke(showTitle)
            }
        }
    }
}

