package m.luigi.eliteboy.elitedangerous.companionapi.data

class ShipModule {

    var workInProgressModifications: Map<String, OutfittingField>? = null
    var engineer: Engineer? = null
    var module: Module? = null
    var specialModifications: Any? = null

    fun getSpecialModifications(): String {
        var sm = specialModifications.toString()
        if (sm != "null" && sm != "[]") {
            sm = sm.split("=")[0].removePrefix("{")
            return sm
        }
        return ""
    }
}