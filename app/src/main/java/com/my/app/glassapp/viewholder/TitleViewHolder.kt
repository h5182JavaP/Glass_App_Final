package com.my.app.glassapp.viewholder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.my.app.glassapp.R
import com.my.app.glassapp.activity.InquiryFormEditActivity
import com.my.app.glassapp.databinding.RecyclerItemTitleRowBinding
import com.my.app.glassapp.model.glass.GlassType
import com.my.app.glassapp.model.glass.GlassGroup

class TitleViewHolder(val binding: RecyclerItemTitleRowBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private var dguGap: String? = null
    var laminationPVB: String? = null
    var lDguGap: String? = null
    var laminatedPVB: String? = null
    var gapCategories = arrayOf("06", "08", "10", "12", "14", "15", "16", "18", "20", "22")
    var pvbCategories = arrayOf("0.38", "0.16", "1.14", "1.52")

    fun bind(item: Any?) {
        with(binding) {
            if (item is GlassGroup) {
                when (item.glassTypeType) {
                    GlassType.SGU -> {
                        HeaderRowID.text = "${item.thickness} ${item.glassDetail}"

                        cvAddSGU.setOnClickListener { v->
                            val intent = Intent(context, InquiryFormEditActivity::class.java)
                            intent.putExtra("GlassType", "SGU")
                            intent.putExtra("TablePosition", 0)
                            intent.putExtra("AddTypeFlag", true)
                            intent.putExtra("AddTypeFlag1", true)

                            context.startActivity(intent)
                            activity.finish()
                        }
                    }
                    GlassType.DGU -> {
                        if (item.airGap == "0") {
                            dguGap = gapCategories.get(0)
                        } else if (item.airGap == "1") {
                            dguGap = gapCategories.get(1)
                        } else if (item.airGap == "2") {
                            dguGap = gapCategories.get(2)
                        } else if (item.airGap == "3") {
                            dguGap = gapCategories.get(3)
                        } else if (item.airGap == "4") {
                            dguGap = gapCategories.get(4)
                        } else if (item.airGap == "5") {
                            dguGap = gapCategories.get(5)
                        } else if (item.airGap == "6") {
                            dguGap = gapCategories.get(6)
                        } else if (item.airGap == "7") {
                            dguGap = gapCategories.get(7)
                        } else if (item.airGap == "8") {
                            dguGap = gapCategories.get(8)
                        } else if (item.airGap == "9") {
                            dguGap = gapCategories.get(9)
                        }
                        HeaderRowID.text =
                            "${dguGap} ${item.glass1Details} ${item.glass2Details}"

                        cvAddSGU.setOnClickListener { v->
                            val intent = Intent(context, InquiryFormEditActivity::class.java)
                            intent.putExtra("GlassType", "DGU")
                            intent.putExtra("TablePosition", 0)
                            intent.putExtra("AddTypeFlag", true)
                            intent.putExtra("AddTypeFlag1", true)

                            context.startActivity(intent)
                            activity.finish()
                        }
                    }
                    GlassType.LAMINATION -> {
                        if (item.pvb == "0") {
                            laminationPVB = pvbCategories[0]
                        } else if (item.pvb == "1") {
                            laminationPVB = pvbCategories[1]
                        } else if (item.pvb == "2") {
                            laminationPVB = pvbCategories[2]
                        } else if (item.pvb == "3") {
                            laminationPVB = pvbCategories[3]
                        }
                        HeaderRowID.text =
                            "${laminationPVB} ${item.glass1Details} ${item.glass2Details}"

                        cvAddSGU.setOnClickListener { v->
                            val intent = Intent(context, InquiryFormEditActivity::class.java)
                            intent.putExtra("GlassType", "Lamination")
                            intent.putExtra("TablePosition", 0)
                            intent.putExtra("AddTypeFlag", true)
                            intent.putExtra("AddTypeFlag1", true)

                            context.startActivity(intent)
                            activity.finish()
                        }
                    }
                    GlassType.LAMINATEDDGU -> {
                        if (item.airGap == "0") {
                            lDguGap = gapCategories[0]
                        } else if (item.airGap == "1") {
                            lDguGap = gapCategories[1]
                        } else if (item.airGap == "2") {
                            lDguGap = gapCategories[2]
                        } else if (item.airGap == "3") {
                            lDguGap = gapCategories[3]
                        } else if (item.airGap == "4") {
                            lDguGap = gapCategories[4]
                        } else if (item.airGap == "5") {
                            lDguGap = gapCategories[5]
                        } else if (item.airGap == "6") {
                            lDguGap = gapCategories[6]
                        } else if (item.airGap == "7") {
                            lDguGap = gapCategories[7]
                        } else if (item.airGap == "8") {
                            lDguGap = gapCategories[8]
                        } else if (item.airGap == "9") {
                            lDguGap = gapCategories[9]
                        }

                        if (item.pvb == "0") {
                            laminatedPVB = pvbCategories[0]
                        } else if (item.pvb == "1") {
                            laminatedPVB = pvbCategories[1]
                        } else if (item.pvb == "2") {
                            laminatedPVB = pvbCategories[2]
                        } else if (item.pvb == "3") {
                            laminatedPVB = pvbCategories[3]
                        }

                        "${laminatedPVB} ${lDguGap} ${item.glass1Details} ${item.glass2Details} ${item.glass3Details}"
                        cvAddSGU.setOnClickListener { v->
                            val intent = Intent(context, InquiryFormEditActivity::class.java)
                            intent.putExtra("GlassType", "LaminatedDGU")
                            intent.putExtra("TablePosition", 0)
                            intent.putExtra("AddTypeFlag", true)
                            intent.putExtra("AddTypeFlag1", true)

                            context.startActivity(intent)
                            activity.finish()
                        }
                    }
                    GlassType.ANNEALED -> {
                        HeaderRowID.text = "${item.thickness} ${item.glassDetail}"

                        cvAddSGU.setOnClickListener { v->
                            val intent = Intent(context, InquiryFormEditActivity::class.java)
                            intent.putExtra("GlassType", "Annealed")
                            intent.putExtra("TablePosition", 0)
                            intent.putExtra("AddTypeFlag", true)
                            intent.putExtra("AddTypeFlag1", true)

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
        fun getViewHolder(parent: ViewGroup, context: Context, activity: Activity): RecyclerItemTitleRowBinding {
            this.context = context
            this.activity = activity
            return RecyclerItemTitleRowBinding.bind(LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_title_row, parent, false))
        }
    }
}