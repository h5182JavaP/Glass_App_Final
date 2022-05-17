package com.my.app.glassapp.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.my.app.glassapp.R
import com.my.app.glassapp.databinding.RecyclerItemMainHeaderBinding
import com.my.app.glassapp.model.glass.GlassGroup

class MainHeaderViewHolder(val binding: RecyclerItemMainHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Any?) {
        with(binding) {
            if (item is GlassGroup) {
                tvHeaderSGU.text = "Glass Type : ${item.glassTypeType.toString()}"
            }
        }
    }

    companion object {
        fun getViewHolder(parent: ViewGroup): RecyclerItemMainHeaderBinding {
            return RecyclerItemMainHeaderBinding.bind(LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_main_header, parent, false))
        }
    }
}