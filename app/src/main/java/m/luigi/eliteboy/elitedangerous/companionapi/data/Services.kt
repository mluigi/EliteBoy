package m.luigi.eliteboy.elitedangerous.companionapi.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Services(
    var commodities: String? = null,
    var contacts: String? = null,
    var crewlounge: String? = null,
    var dock: String? = null,
    var exploration: String? = null,
    var outfitting: String? = null,
    var powerplay: String? = null,
    var rearm: String? = null,
    var refuel: String? = null,
    var repair: String? = null,
    var searchrescue: String? = null,
    var shipyard: String? = null
) : Parcelable