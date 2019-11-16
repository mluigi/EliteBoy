package m.luigi.eliteboy.elitedangerous.companionapi.data

import android.os.Parcel
import android.os.Parcelable

class Ship() :Parcelable {

    var alive: Boolean? = null
    var cockpitBreached: Boolean? = null
    var free: Boolean? = null
    var health: Health? = null
    var id: Long? = null
    var name: String? = null
    var oxygenRemaining: Int? = null
    var shipID: String? = null
    var shipName: String? = null
    var starsystem: StarSystem? = null
    var station: Station? = null
    var value: Value? = null
    var modules: Map<String, ShipModule>? = null
    var basevalue: Long? = null
    var sku: String? = null

    constructor(parcel: Parcel) : this() {
        alive = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        cockpitBreached = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        free = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        id = parcel.readValue(Long::class.java.classLoader) as? Long
        name = parcel.readString()
        oxygenRemaining = parcel.readValue(Int::class.java.classLoader) as? Int
        shipID = parcel.readString()
        shipName = parcel.readString()
        starsystem = parcel.readParcelable(StarSystem::class.java.classLoader)
        station = parcel.readParcelable(Station::class.java.classLoader)
        basevalue = parcel.readValue(Long::class.java.classLoader) as? Long
        sku = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(alive)
        parcel.writeValue(cockpitBreached)
        parcel.writeValue(free)
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeValue(oxygenRemaining)
        parcel.writeString(shipID)
        parcel.writeString(shipName)
        parcel.writeParcelable(starsystem, flags)
        parcel.writeParcelable(station, flags)
        parcel.writeValue(basevalue)
        parcel.writeString(sku)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ship> {
        override fun createFromParcel(parcel: Parcel): Ship {
            return Ship(parcel)
        }

        override fun newArray(size: Int): Array<Ship?> {
            return arrayOfNulls(size)
        }
    }


}