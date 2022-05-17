package com.my.app.glassapp.viewholder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my.app.glassapp.R
import com.my.app.glassapp.activity.InquiryFormEditActivity
import com.my.app.glassapp.databinding.RecyclerItemRegularRowBinding
import com.my.app.glassapp.model.*
import java.io.File

class ItemViewHolder(val binding: RecyclerItemRegularRowBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(any: Any?) {
        when(any) {
            is SGUTable -> {
                with(binding) {
                    with(any) {
                        tvSrNo.text = sgu_id.toString()
                        tvWidth.text = sgu_glassWidth
                        tvHeight.text = sgu_glassHeight
                        tvQuantity.text = sgu_quantity
                        tvNotes.text = sgu_note
                        if(sgu_path!=null) {
                            Glide.with(btnPhoto).load(File(sgu_path)).into(btnPhoto)
                        }else{
                            btnPhoto.setImageResource(0)
                        }

                        btnEdit.setOnClickListener { v->
                            val intent = Intent(context, InquiryFormEditActivity::class.java)
                            intent.putExtra("GlassType", "SGU")
                            intent.putExtra("TablePosition", sgu_id.toString())
                            intent.putExtra("AddTypeFlag", true)
                            context.startActivity(intent)
                            activity.finish()
                        }

                    }
                }
            }
            is DGUTable -> {
                with(binding) {
                    with(any) {
                        tvSrNo.text = dgu_id.toString()
                        tvWidth.text = dgu_glassWidth
                        tvHeight.text = dgu_glassHeight
                        tvQuantity.text = dgu_quantity
                        tvNotes.text = dgu_note
                        if(dgu_path!=null) {
                            Glide.with(btnPhoto).load(File(dgu_path)).into(btnPhoto)
                        }else{
                            btnPhoto.setImageResource(0)
                        }

                        btnEdit.setOnClickListener { v->
                            val intent = Intent(context, InquiryFormEditActivity::class.java)
                            intent.putExtra("GlassType", "DGU")
                            intent.putExtra("TablePosition", dgu_id.toString())
                            intent.putExtra("AddTypeFlag", true)
                            context.startActivity(intent)
                            activity.finish()
                        }
                    }
                }
            }
            is LaminationTable -> {
                with(binding) {
                    with(any) {
                        tvSrNo.text = lamination_id.toString()
                        tvWidth.text = lamination_glassWidth
                        tvHeight.text = lamination_glassHeight
                        tvQuantity.text = lamination_quantity
                        tvNotes.text = lamination_note
                        if(lamination_path!=null) {
                            Glide.with(btnPhoto).load(File(lamination_path)).into(btnPhoto)
                        }else{
                            btnPhoto.setImageResource(0)
                        }

                        btnEdit.setOnClickListener { v->
                            val intent = Intent(context, InquiryFormEditActivity::class.java)
                            intent.putExtra("GlassType", "Lamination")
                            intent.putExtra("TablePosition", lamination_id.toString())
                            intent.putExtra("AddTypeFlag", true)
                            context.startActivity(intent)
                            activity.finish()
                        }
                    }
                }
            }
            is LaminatedDGUTable -> {
                with(binding) {
                    with(any) {
                        tvSrNo.text = ldgu_id.toString()
                        tvWidth.text = ldgu_glassWidth
                        tvHeight.text = ldgu_glassHeight
                        tvQuantity.text = ldgu_quantity
                        tvNotes.text = ldgu_note
                        if(ldgu_path!=null) {
                            Glide.with(btnPhoto).load(File(ldgu_path)).into(btnPhoto)
                        }else{
                            btnPhoto.setImageResource(0)
                        }

                        btnEdit.setOnClickListener { v->
                            val intent = Intent(context, InquiryFormEditActivity::class.java)
                            intent.putExtra("GlassType", "LaminatedDGU")
                            intent.putExtra("TablePosition", ldgu_id.toString())
                            intent.putExtra("AddTypeFlag", true)
                            context.startActivity(intent)
                            activity.finish()
                        }
                    }
                }
            }
            is AnnealedTable -> {
                with(binding) {
                    with(any) {
                        tvSrNo.text = annealed_id.toString()
                        tvWidth.text = annealed_glassWidth
                        tvHeight.text = annealed_glassHeight
                        tvQuantity.text = annealed_quantity
                        tvNotes.text = annealed_note
                        if(annealed_path!=null) {
                            Glide.with(btnPhoto).load(File(annealed_path)).into(btnPhoto)
                        }else{
                            btnPhoto.setImageResource(0)
                        }

                        btnEdit.setOnClickListener { v->
                            val intent = Intent(context, InquiryFormEditActivity::class.java)
                            intent.putExtra("GlassType", "Annealed")
                            intent.putExtra("TablePosition", annealed_id.toString())
                            intent.putExtra("AddTypeFlag", true)
                            context.startActivity(intent)
                            activity.finish()
                        }
                    }
                }
            }
        }
    }

    companion object {
        lateinit var context : Context
        lateinit var activity : Activity
        fun getViewHolder(parent: ViewGroup, context: Context, acivity: Activity): RecyclerItemRegularRowBinding {
            this.context = context
            this.activity = acivity
            return RecyclerItemRegularRowBinding.bind(LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_regular_row, parent, false))
        }
    }
}