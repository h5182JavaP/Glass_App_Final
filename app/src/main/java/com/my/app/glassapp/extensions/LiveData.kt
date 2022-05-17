package com.my.app.glassapp.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.my.app.glassapp.model.*
import com.my.app.glassapp.model.glass.GlassGroup
import com.my.app.glassapp.model.glass.GlassType
import com.my.app.glassapp.model.glass.ItemType

fun zipLiveData(vararg liveItems: LiveData<*>): LiveData<ArrayList<Any?>> {
    return MediatorLiveData<ArrayList<Any?>>().apply {
        val zippedObjects = ArrayList<Any?>()
        liveItems.forEach { it ->
            addSource(it) { item ->
                val groupByValues = (item as Collection<Any?>).groupBy {
                    return@groupBy getGlassGroup(it)?.toString()
                }
                val groupOfGlass = groupByValues.keys
                groupOfGlass.forEach {
                    groupByValues[it]?.let { it1 ->
                        zippedObjects.add(getGlassGroup(it1.firstOrNull())?.apply {
                            itemType = ItemType.MAIN_HEADER
                        }) // ItemType.MAIN_HEADER)
                        zippedObjects.add(getGlassGroup(it1.firstOrNull())?.apply {
                            itemType = ItemType.TABLE_TITLE
                        }) // ItemType.TABLE_TITLE
                        zippedObjects.add(ItemType.TABLE_HEADER)
                        zippedObjects.addAll(it1)
                    }
                }
            }
        }
        postValue(zippedObjects)
        //zippedObjects.clear()
    }
}

private fun getGlassGroup(it: Any?): GlassGroup? {
    return when (it) {
        is SGUTable -> GlassGroup(glassTypeType = GlassType.SGU,
            thickness = it.sgu_thickness?.trim(),
            glassDetail = it.sgu_materialDetails?.lowercase()?.trim())

        is DGUTable -> GlassGroup(glassTypeType = GlassType.DGU,
            glass1Details = it.dgu_glass_1,
            glass2Details = it.dgu_glass_2,
            airGap = it.dgu_gap)

        is LaminationTable -> GlassGroup(glassTypeType = GlassType.LAMINATION,
            glass1Details = it.lamination_glass_1,
            glass2Details = it.lamination_glass_2,
            pvb = it.lamination_pvb)

        is LaminatedDGUTable -> GlassGroup(glassTypeType = GlassType.LAMINATEDDGU,
            glass1Details = it.ldgu_glass_1,
            glass2Details = it.ldgu_glass_2,
            glass3Details = it.ldgu_glass_3,
            pvb = it.ldgu_pvb,
            airGap = it.ldgu_gap)

        is AnnealedTable -> GlassGroup(glassTypeType = GlassType.ANNEALED,
            thickness = it.annealed_thickness,
            glassDetail = it.annealed_materialDetails)
        else -> {
            null
        }
    }
}