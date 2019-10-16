package m.luigi.eliteboy.elitedangerous.edsm.data

class System {
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
    }

    inner class Coords {
        var x = 0.toDouble()
        var y = 0.toDouble()
        var z = 0.toDouble()
    }

    companion object {
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
}