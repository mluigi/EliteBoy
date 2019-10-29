package m.luigi.eliteboy.elitedangerous.edsm.data

import android.icu.text.NumberFormat
import kotlin.math.roundToInt

class Body {
    var id: Int = 0
    var id64: Any? = null
    var name: String? = null
    var type: String? = null
    var subType: String? = null
    var offset: Int? = null
    var distanceToArrival: Double? = null
    var isMainStar: Boolean = false
    var isScoopable: Boolean = false
    var age: Int? = null
    var luminosity: String? = null
    var absoluteMagnitude: Double? = null
    var solarMasses: Double? = null
    var solarRadius: Double? = null
    var surfaceTemperature: Int? = null
    var orbitalPeriod: Double? = null
    var semiMajorAxis: Double? = null
    var orbitalEccentricity: Double? = null
    var orbitalInclination: Double? = null
    var argOfPeriapsis: Double? = null
    var rotationalPeriod: Double? = null
    var rotationalPeriodTidallyLocked: Boolean? = null
    var axialTilt: Double? = null
    var belts: List<Belts>? = null
    var materials: Map<String, Double>? = null

    var isLandable: Boolean? = null
    var gravity: Double? = null
    var earthMasses: Double? = null
    var radius: Double? = null
    var volcanismType: String? = null
    var atmosphereType: String? = null
    var terraformingState: String? = null

    class Belts {
        var name: String? = null
        var type: String? = null
        var mass: String? = null
        var innerRadius: Int = 0
        var outerRadius: Int = 0
    }

    fun asMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()

        map["Name"] = name!!
        map["Type"] = "$subType"
        if (isScoopable) map["Scoopable"]="Yes"
        map["Distance to arrival"] = String.format("%s Ls", NumberFormat.getIntegerInstance().format(distanceToArrival!!.roundToInt()))


        return map
    }
}