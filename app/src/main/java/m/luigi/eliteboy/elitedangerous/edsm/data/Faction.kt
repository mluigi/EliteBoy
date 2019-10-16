package m.luigi.eliteboy.elitedangerous.edsm.data

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

}