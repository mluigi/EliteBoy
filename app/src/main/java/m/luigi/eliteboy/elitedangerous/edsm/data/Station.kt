package m.luigi.eliteboy.elitedangerous.edsm.data

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
    var updateTime: MutableMap<String,String?>? = null

    class ControllingFaction {
        var id: Int = 0
        var name: String? = null
    }
}