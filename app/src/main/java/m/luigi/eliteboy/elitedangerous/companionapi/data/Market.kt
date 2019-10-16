package m.luigi.eliteboy.elitedangerous.companionapi.data

class Market {
    var commodities: List<Commodity>? = null
    var economies: Map<String, Economy>? = null
    var exported: Map<String, String>? = null
    var id: Long? = null
    var imported: Map<String, String>? = null
    var name: String? = null
    var outpostType: String? = null
    var prohibited: Map<String, String>? = null
    var services: Services? = null
}