package m.luigi.eliteboy.util

import android.content.res.AssetManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import m.luigi.eliteboy.elitedangerous.companionapi.data.Ship
import java.text.NumberFormat
import java.util.*

object CoriolisDataHelper {

    private var data = JsonObject()

    private fun ships() = data["Ships"].asJsonObject
    private fun modules() = data["Modules"].asJsonObject
    private fun standardModules() = modules()["standard"].asJsonObject
    private fun hardpointModules() = modules()["hardpoints"].asJsonObject
    private fun internalModules() = modules()["internal"].asJsonObject
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

    suspend fun getShipBulkheadsPriceMap(ship: String): Map<String, String> {
        return onDefault {
            val map = mutableMapOf<String, String>()
            with(ships()[ship].asJsonObject) {
                val bulkheads = this["bulkheads"].asJsonArray
                val bulkheadsPrices = bulkheads
                    .map { it.asJsonObject["cost"].asLong }
                    .sorted()
                    .map {
                        String.format(
                            "%s cr",
                            NumberFormat.getIntegerInstance().format(it)
                        )
                    }
                val shipId = getEDSMShipId(ship)
                map["${shipId}_armour_grade1"] = bulkheadsPrices[0]
                map["${shipId}_armour_grade2"] = bulkheadsPrices[1]
                map["${shipId}_armour_grade3"] = bulkheadsPrices[2]
                map["${shipId}_armour_mirrored"] = bulkheadsPrices[3]
                map["${shipId}_armour_reactive"] = bulkheadsPrices[4]

            }

            map
        }
    }

    suspend fun getBulkheadsPriceMap(modules: Map<String, String>): Map<String, String> {
        return onDefault {
            val map = mutableMapOf<String, String>()
            ships().entrySet().forEach { (shipId, value) ->
                if (modules.keys.any { it.startsWith(shipId) }) {
                    val shipName = value.asJsonObject["properties"].asJsonObject["name"].asString
                    getShipBulkheadsPriceMap(shipId).forEach { (bulkId, price) ->
                        map[modules.getValue(bulkId) + " - ${getEDSMShipName(shipName)}"] =
                            price
                    }
                }
            }
            map
        }
    }

    suspend fun getStandardModulePriceMap(): Map<String, String> {
        return onDefault {
            val map = mutableMapOf<String, String>()
            standardModules().entrySet().forEach {
                it.value.asJsonArray.sortedBy { it.asJsonObject["cost"].asLong }.forEach {
                    val module = it.asJsonObject
                    map[module["symbol"].asString.toLowerCase(Locale.getDefault())] = String.format(
                        "%s cr",
                        NumberFormat.getIntegerInstance().format(
                            module["cost"].asLong
                        )
                    )
                }
            }
            map
        }
    }

    suspend fun getHardpointModulePriceMap(): Map<String, String> {
        return onDefault {
            val map = mutableMapOf<String, String>()
            hardpointModules().entrySet().forEach {
                it.value.asJsonArray.sortedBy { it.asJsonObject["cost"].asLong }.forEach {
                    val module = it.asJsonObject
                    map[module["symbol"].asString.toLowerCase(Locale.getDefault())] = String.format(
                        "%s cr",
                        NumberFormat.getIntegerInstance().format(
                            module["cost"].asLong
                        )
                    )
                }
            }
            map
        }
    }

    suspend fun getInternalModulePriceMap(): Map<String, String> {
        return onDefault {
            val map = mutableMapOf<String, String>()
            internalModules().entrySet().forEach {
                it.value.asJsonArray.sortedBy { it.asJsonObject["cost"].asLong }.forEach {
                    val module = it.asJsonObject
                    map[module["symbol"].asString.toLowerCase(Locale.getDefault())] = String.format(
                        "%s cr",
                        NumberFormat.getIntegerInstance().format(
                            module["cost"].asLong
                        )
                    )
                }
            }
            map
        }
    }

    suspend fun getModulePriceMapFiltered(modules: Map<String, String>): Map<String, String> {
        return onDefault {
            val map = mutableMapOf<String, String>()
            val standard = getStandardModulePriceMap()
            val hardpoints = getHardpointModulePriceMap()
            val internal = getInternalModulePriceMap()
            map.putAll(getBulkheadsPriceMap(modules))
            standard.filter { it.key in modules.keys }.forEach { id, price ->
                map[modules.getValue(id)] = price
            }
            hardpoints.filter { it.key in modules.keys }.forEach { id, price ->
                map[modules.getValue(id)] = price
            }
            internal.filter { it.key in modules.keys }.forEach { id, price ->
                map[modules.getValue(id)] = price
            }
            map
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

    private fun getEDSMShipId(coriolisId: String): String {
        return coriolistoEDSMShipIdMap[coriolisId] ?: coriolisId
    }

    private val coriolistoEDSMShipIdMap = mapOf(
        "beluga" to "belugaliner",
        "cobra_mk_iii" to "cobramkiii",
        "cobra_mk_iv" to "cobramkiv",
        "imperial_cutter" to "cutter",
        "diamondback_explorer" to "diamondbackxl",
        "imperial_eagle" to "empire_eagle",
        "imperial_courier" to "empire_courier",
        "imperial_clipper" to "empire_trader",
        "federal_corvette" to "federation_corvette",
        "federal_assault_ship" to "federation_dropship_mkii",
        "federal_dropship" to "federation_dropship",
        "federal_gunship" to "federation_gunship",
        "fer_de_lance" to "ferdelance",
        "krait_phantom" to "krait_light",
        "type_6_transporter" to "type6",
        "type_7_transport" to "type7",
        "type_9_heavy" to "type9",
        "type_10_defender" to "type9_military",
        "alliance_challenger" to "typex_3",
        "alliance_chieftain" to "typex",
        "alliance_crusader" to "typex_2",
        "fer_de_lance" to "ferdelance",
        "keelback" to "independant_trader",
        "viper_mk_iv" to "viper_mkiv"
    )
}