package m.luigi.eliteboy.elitedangerous.edsm.data

import android.icu.text.NumberFormat
import android.os.Parcel
import android.os.Parcelable
import m.luigi.eliteboy.elitedangerous.companionapi.data.Commodity
import m.luigi.eliteboy.elitedangerous.companionapi.data.Module
import m.luigi.eliteboy.elitedangerous.companionapi.data.Ship
import kotlin.math.roundToInt


class Station() : Parcelable {
    var id: Int = 0
    var marketId: Long = 0
    var id64: Long? = null
    var name: String? = null
    var type: String? = null
    var distanceToArrival: Double = 0.toDouble()
    var allegiance: String? = null
    var government: String? = null
    var economy: String? = null
    var secondEconomy: String? = null
    var haveMarket: Boolean = false
    var haveShipyard: Boolean = false
    var haveOutfitting: Boolean = false
    var controllingFaction: ControllingFaction? = null
    var otherServices: ArrayList<String>? = null
    var updateTime: MutableMap<String, String?>? = null
    var ships: ArrayList<Ship>? = null
    var commodities: ArrayList<Commodity>? = null
    var outfitting: ArrayList<Module>? = null
    var traderType: String? = null
    var brokerType: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        marketId = parcel.readLong()
        id64 = parcel.readValue(Long::class.java.classLoader) as? Long
        name = parcel.readString()
        type = parcel.readString()
        distanceToArrival = parcel.readDouble()
        allegiance = parcel.readString()
        government = parcel.readString()
        economy = parcel.readString()
        secondEconomy = parcel.readString()
        haveMarket = parcel.readByte() != 0.toByte()
        haveShipyard = parcel.readByte() != 0.toByte()
        haveOutfitting = parcel.readByte() != 0.toByte()
    }

    inner class ControllingFaction {
        var id: Int = 0
        var name: String? = null
    }

    fun asMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map["Type"] = type!!
        map["Distance to Arrival"] = String.format(
            "%s Ls",
            NumberFormat.getIntegerInstance().format(distanceToArrival.roundToInt())
        )

        map["Allegiance"] = allegiance!!
        map["Government"] = government!!
        map["Faction"] = controllingFaction!!.name!!
        map["Economy"] = economy!!
        map["Second Economy"] = secondEconomy ?: "None"
        map["Has Market"] = if (haveMarket) "Yes" else "No"
        map["Has Shipyard"] = if (haveShipyard) "Yes" else "No"
        map["Has Outfitting"] = if (haveOutfitting) "Yes" else "No"
        map["Other Services"] = otherServices!!.joinToString("\n")
        if (!traderType.isNullOrBlank()) {
            map["Trader Type"] = traderType!!
        }
        if (!brokerType.isNullOrBlank()) {
            map["Broker Type"] = brokerType!!
        }

        return map
    }

    companion object CREATOR : Parcelable.Creator<Station> {
        override fun createFromParcel(parcel: Parcel): Station {
            return Station(parcel)
        }

        override fun newArray(size: Int): Array<Station?> {
            return arrayOfNulls(size)
        }

        fun updateStation(original: Station, update: Station) {
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
        parcel.writeInt(id)
        parcel.writeLong(marketId)
        parcel.writeValue(id64)
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeDouble(distanceToArrival)
        parcel.writeString(allegiance)
        parcel.writeString(government)
        parcel.writeString(economy)
        parcel.writeString(secondEconomy)
        parcel.writeByte(if (haveMarket) 1 else 0)
        parcel.writeByte(if (haveShipyard) 1 else 0)
        parcel.writeByte(if (haveOutfitting) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }
}