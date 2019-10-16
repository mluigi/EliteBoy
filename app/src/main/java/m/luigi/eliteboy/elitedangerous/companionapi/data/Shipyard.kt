package m.luigi.eliteboy.elitedangerous.companionapi.data

class Shipyard {
    var economies: Map<String, Economy>? = null
    var exported: Map<String, String>? = null
    var id: Long? = null
    var imported: Map<String, String>? = null
    var modules: Map<String, Module>? = null
    var name: String? = null
    var outpostType: String? = null
    var services: Services? = null
    var ships: Map<String, Any>? = null
}