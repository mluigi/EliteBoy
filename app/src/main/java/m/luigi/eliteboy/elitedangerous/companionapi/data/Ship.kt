package m.luigi.eliteboy.elitedangerous.companionapi.data

class Ship {

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

}