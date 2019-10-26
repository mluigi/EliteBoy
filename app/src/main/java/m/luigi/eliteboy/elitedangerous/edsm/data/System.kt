package m.luigi.eliteboy.elitedangerous.edsm.data

import android.icu.text.NumberFormat
import android.os.Parcel
import android.os.Parcelable
import m.luigi.eliteboy.util.onIO
import kotlin.math.pow
import kotlin.math.sqrt

class System() : Parcelable {
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

    private fun getDistance(
        x1: Double = 0.0,
        y1: Double = 0.0,
        z1: Double = 0.0,
        x2: Double = 0.0,
        y2: Double = 0.0,
        z2: Double = 0.0
    ): Double {
        return sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2) + (z1 - z2).pow(2))
    }

    fun distanceTo(
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0
    ): Double {
        return getDistance(
            coords!!.x,
            coords!!.y,
            coords!!.z,
            x,
            y,
            z
        )
    }

    fun distanceTo(system: System): Double {
        return distanceTo(
            system.coords!!.x,
            system.coords!!.y,
            system.coords!!.z
        )
    }


    companion object CREATOR : Parcelable.Creator<System> {
        override fun createFromParcel(parcel: Parcel): System {
            return System(parcel)
        }

        override fun newArray(size: Int): Array<System?> {
            return arrayOfNulls(size)
        }

        suspend fun updateSystem(original: System, update: System) {
            onIO {
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