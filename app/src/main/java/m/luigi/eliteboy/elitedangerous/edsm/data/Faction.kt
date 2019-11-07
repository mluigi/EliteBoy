package m.luigi.eliteboy.elitedangerous.edsm.data

import java.text.NumberFormat

class Faction {
    var id: Int = 0

    var name: String? = null

    var allegiance: String? = null

    var government: String? = null

    var influence: Double = 0.toDouble()

    var state: String? = null

    var recoveringStates: List<State>? = null

    var pendingStates: List<State>? = null

    var isPlayer: Boolean = false

    var lastUpdate: Int = 0

    class State {
        var state: String? = null

        var trend: Int = 0
    }

    fun asMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map["Name"] = name!!
        map["Allegiance"] = allegiance!!
        map["Government"] = government!!
        map["Influence"] = NumberFormat.getPercentInstance().format(influence)
        map["State"] = state!!
        map["Pending State"] = pendingStates.orEmpty().firstOrNull()?.state ?: "None"
        map["Is Player Faction?"] = if (isPlayer) "Yes" else "No"
        return map
    }
}