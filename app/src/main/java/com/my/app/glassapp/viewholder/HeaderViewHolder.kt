package com.my.app.glassapp.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.my.app.glassapp.R
import com.my.app.glassapp.databinding.RecyclerItemHeaderRowBinding

class HeaderViewHolder(val binding: RecyclerItemHeaderRowBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun getViewHolder(parent: ViewGroup): RecyclerItemHeaderRowBinding {
            return RecyclerItemHeaderRowBinding.bind(LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_header_row, parent, false))
        }
    }
}