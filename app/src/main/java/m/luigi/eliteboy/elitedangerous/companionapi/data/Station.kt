package m.luigi.eliteboy.elitedangerous.companionapi.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Station(
    var id: Long? = null,
    var name: String? = null
) : Parcelable