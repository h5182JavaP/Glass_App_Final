package com.my.app.glassapp.model.glass

data class GlassGroup(
    val glassTypeType: GlassType,
    val thickness: String? = null,
    val glassDetail: String? = null,
    val glass1Details: String? = null,
    val glass2Details: String? = null,
    val glass3Details: String? = null,
    val airGap: String? = null,
    val pvb: String? = null,
    var itemType: ItemType? = null
) {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return "GlassGroup(glassType=$glassTypeType, thickness=$thickness, glassDetail=$glassDetail, glass1Details=$glass1Details, glass2Details=$glass2Details, glass3Details=$glass3Details, airGap=$airGap, pvb=$pvb)"
    }

}