package m.luigi.eliteboy.elitedangerous.edsm.data

import android.annotation.TargetApi
import android.icu.text.NumberFormat
import android.os.Build
import android.os.Parcel
import android.os.Parcelable

class System() :Parcelable {
    var name: String? = null
    var coords: Coords? = null
    var information: Information? = null
    var primaryStar: PrimaryStar? = null
    var id = 0
    var id64: Long? = null

    var distance = 0.toDouble()

    var bodies: ArrayList<Body>? = null
    var stations: ArrayList<Station>? = null
    var controllingFaction: Faction? = null
    var factions: ArrayList<Faction>? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        id = parcel.readInt()
        id64 = parcel.readValue(Long::class.java.classLoader) as? Long
        distance = parcel.readDouble()
    }

    inner class PrimaryStar {
        var type: String? = null
        var name: String? = null
        var isScoopable: Boolean? = null
    }

    inner class Information {
        var allegiance: String? = null
        var government: String? = null
        var faction: String? = null
        var factionState: String? = null
        var population: Long? = null
        var reserve: String? = null
        var security: String? = null
        var economy: String? = null

        @TargetApi(Build.VERSION_CODES.N)
        fun asMap(): Map<String, String> {
            return mapOf(
                "Allegiance" to allegiance!!,
                "Government" to government!!,
                "Population" to NumberFormat.getIntegerInstance().format(population!!),
                "Security" to security!!,
                "Economy" to economy!!,
                "State" to factionState!!
            )
        }
    }

    inner class Coords {
        var x = 0.toDouble()
        var y = 0.toDouble()
        var z = 0.toDouble()
    }

    companion object CREATOR : Parcelable.Creator<System> {
        override fun createFromParcel(parcel: Parcel): System {
            return System(parcel)
        }

        override fun newArray(size: Int): Array<System?> {
            return arrayOfNulls(size)
        }
        fun updateSystem(original: System, update: System) {
            original.javaClass.declaredFields.forEach {
                it.isAccessible = true
                if (it.get(original) == null) {
                    val updateField = it.get(update)
                    if (updateField != null) {
                        it.set(original, updateField)
                    }
                }
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(id)
        parcel.writeValue(id64)
        parcel.writeDouble(distance)
    }

    override fun describeContents(): Int {
        return 0
    }
}