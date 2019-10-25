package m.luigi.eliteboy.util

import android.content.res.AssetManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import m.luigi.eliteboy.elitedangerous.companionapi.data.Ship
import java.text.NumberFormat

object CoriolisDataHelper {

    private var data = JsonObject()

    private fun ships() = data["Ships"].asJsonObject
    private fun modules() = data["Modules"].asJsonObject
    private fun modifications() = data["Modifications"].asJsonObject

    suspend fun init(assets: AssetManager) {
        onIO {
            data = JsonParser.parseReader(assets.open("index.json").reader()).asJsonObject
        }
    }

    suspend fun getShipPriceMap(): Map<String, String> {
        return onDefault {
            val map = mutableMapOf<String, String>()

            ships().entrySet().sortedBy { it.value.asJsonObject["retailCost"].asLong }.forEach {
                with(it.value.asJsonObject) {
                    val coriolisName = this["properties"].asJsonObject["name"].asString
                    map[getEDSMShipName(coriolisName)] = String.format(
                        "%s cr",
                        NumberFormat.getIntegerInstance().format(this["retailCost"].asLong)
                    )
                }
            }
            map
        }
    }

    suspend fun getShipPriceMapFiltered(shipArray: ArrayList<Ship>): Map<String, String> {
        return onDefault {
            val allShips = getShipPriceMap()

            allShips.filter { (k, _) ->
                shipArray.any { it.name == k }
            }
        }
    }

    private fun getEDSMShipName(coriolisName: String): String {
        return coriolisToEDSMShipNameMap[coriolisName] ?: coriolisName
    }


    private val coriolisToEDSMShipNameMap = mapOf(
        "Krait Mk II" to "Krait MkII",
        "Viper Mk IV" to "Viper MkIV",
        "Viper" to "Viper MkIII",
        "Cobra Mk IV" to "Cobra MkIV",
        "Cobra Mk III" to "Cobra MkIII"
    )
}