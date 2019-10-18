package m.luigi.eliteboy.elitedangerous.edsm.data

import m.luigi.eliteboy.elitedangerous.companionapi.data.Commodity
import m.luigi.eliteboy.elitedangerous.companionapi.data.Module
import m.luigi.eliteboy.elitedangerous.companionapi.data.Ship

class Station {
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

    inner class ControllingFaction {
        var id: Int = 0
        var name: String? = null
    }

    companion object {
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
}