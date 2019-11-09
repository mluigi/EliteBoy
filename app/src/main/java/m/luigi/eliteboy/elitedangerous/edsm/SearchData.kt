package m.luigi.eliteboy.elitedangerous.edsm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchData(
    val systemName: String = "",
    val refSystem: String = "",
    val allegiance: String = "",
    val government: String = "",
    val economy: String = "",
    val security: String = "",
    val factionState: String = "",
    val ships: String = "",
    val modules: String = "",
    val commodities: String = ""
) : Parcelable