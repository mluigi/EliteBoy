package m.luigi.eliteboy.elitedangerous.companionapi.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class StarSystem(
    var id: Long? = null,
    var name: String? = null,
    var systemaddress: Long? = null,
    var faction: String? = null,
    var services: Services? = null
) : Parcelable