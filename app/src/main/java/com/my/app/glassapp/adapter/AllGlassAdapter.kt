package com.my.app.glassapp.adapter

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.my.app.glassapp.model.glass.GlassGroup
import com.my.app.glassapp.model.glass.ItemType
import com.my.app.glassapp.viewholder.HeaderViewHolder
import com.my.app.glassapp.viewholder.ItemViewHolder
import com.my.app.glassapp.viewholder.MainHeaderViewHolder
import com.my.app.glassapp.viewholder.TitleViewHolder

class AllGlassAdapter(context: Context, acivity: Activity) :
    ListAdapter<Any, RecyclerView.ViewHolder>(AllGlassCallBack) {
    val context = context
    val acivity = acivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemType.TABLE_HEADER.value -> {
                HeaderViewHolder(HeaderViewHolder.getViewHolder(parent))
            }
            ItemType.TABLE_TITLE.value -> {
                TitleViewHolder(TitleViewHolder.getViewHolder(parent, context, acivity))
            }
            ItemType.MAIN_HEADER.value -> {
                MainHeaderViewHolder(MainHeaderViewHolder.getViewHolder(parent))
            }
            else -> ItemViewHolder(ItemViewHolder.getViewHolder(parent, context, acivity))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> holder.bind(getItem(position))
            is TitleViewHolder -> holder.bind(getItem(position))
            is MainHeaderViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun submitList(list: MutableList<Any>?) {
        super.submitList(list)
    }

    //never use 0 for any other item
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ItemType -> {
                (getItem(position) as ItemType).value
            }
            is GlassGroup -> {
                ((getItem(position) as GlassGroup).itemType ?: ItemType.MAIN_HEADER).value
            }
            else -> ItemType.ITEM.value
        }
    }
}

object AllGlassCallBack : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return false
    }

}