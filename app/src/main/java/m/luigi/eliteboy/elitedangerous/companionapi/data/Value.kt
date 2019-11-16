package m.luigi.eliteboy.elitedangerous.companionapi.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Value(
    var cargo: Int? = null,
    var hull: Int? = null,
    var modules: Int? = null,
    var total: Int? = null,
    var unloaned: Int? = null
) : Parcelable