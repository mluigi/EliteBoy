package m.luigi.eliteboy.elitedangerous.edsm

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import m.luigi.eliteboy.elitedangerous.companionapi.data.Commodity
import m.luigi.eliteboy.elitedangerous.companionapi.data.Module
import m.luigi.eliteboy.elitedangerous.companionapi.data.deserializers.CommodityDeserializer
import m.luigi.eliteboy.elitedangerous.companionapi.data.deserializers.ModuleDeserializer
import m.luigi.eliteboy.elitedangerous.edsm.data.Station
import m.luigi.eliteboy.elitedangerous.edsm.data.System
import java.net.URL
import java.net.URLEncoder

object EDSMApi {
    var commander: String = ""
    var apiKey: String = ""

    private const val BASE_URL = "https://www.edsm.net"
    private const val STATUS = "/api-status-v1/elite-server"

    private const val SYSTEM = "/api-v1/system"
    private const val SYSTEMS = "/api-v1/systems"
    private const val SPHERE_SYSTEMS = "/api-v1/sphere-systems"
    private const val CUBE_SYSTEMS = "/api-v1/cube-systems"

    private const val BODIES = "/api-system-v1/bodies"
    private const val STATIONS = "/api-system-v1/stations"
    private const val FACTIONS = "/api-system-v1/factions"
    private const val MARKET = "/market"
    private const val SHIPYARD = "/shipyard"
    private const val OUTFITTING = "/outfitting"

    private const val GET_LOGS = "/api-logs-v1/get-logs"
    private const val RANKS = "/api-commander-v1/get-ranks"
    private const val MATS = "api-commander-v1/get-materials"
    private const val CREDS = "/api-commander-v1/get-credits"
    private const val UPDATE_SHIP = "/api-commander-v1/update-ship"
    private const val SELL_SHIP = "/api-commander-v1/sell-ship"

    private const val FROM_SOFTWARE = "EliteBoy"
    private const val FROM_SOFTWARE_VERSION = "1.0"

    enum class SearchType {
        NAME,
        SPHERE,
        CUBE
    }


    fun getSystem(name: String): System {
        val system = URLEncoder.encode(name, "UTF-8")
        val connection = URL(
            BASE_URL + SYSTEM +
                    "/?systemName=$system" +
                    "&showId=1" +
                    "&showCoordinates=1" +
                    "&showPermit=1" +
                    "&showInformation=1" +
                    "&showPrimaryStar=1"
        ).openConnection()
        connection.doInput = true
        val json = connection.getInputStream().bufferedReader().readText()
        return Gson().fromJson<System>(json, System::class.java)
    }

    private fun findSystems(
        type: SearchType,
        system: String = "",
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0,
        size: Int = 0
    ): ArrayList<System> {
        var url = BASE_URL
        val systemEncoded = URLEncoder.encode(system, "UTF-8")
        when (type) {
            SearchType.NAME -> {
                url += SYSTEMS +
                        "/?systemName=$systemEncoded" +
                        "&showId=1" +
                        "&showCoordinates=1" +
                        "&showPermit=1" +
                        "&showInformation=1" +
                        "&showPrimaryStar=1"
            }
            SearchType.SPHERE -> {
                if (system == "") {
                    url += SPHERE_SYSTEMS +
                            "/?x=$x&y=$y&z=$z" +
                            "&showId=1" +
                            "&showCoordinates=1" +
                            "&showPermit=1" +
                            "&showInformation=1" +
                            "&showPrimaryStar=1" +
                            if (size > 0) {
                                "&radius=$size"
                            } else {
                            }
                } else {
                    url += SPHERE_SYSTEMS +
                            "/?systemName=$systemEncoded" +
                            "&showId=1" +
                            "&showCoordinates=1" +
                            "&showPermit=1" +
                            "&showInformation=1" +
                            "&showPrimaryStar=1" +
                            if (size > 0) {
                                "&radius=$size"
                            } else {
                            }
                }
            }
            SearchType.CUBE -> {
                if (system == "") {
                    url += CUBE_SYSTEMS +
                            "/?x=$x&y=$y&z=$z" +
                            "&showId=1" +
                            "&showCoordinates=1" +
                            "&showPermit=1" +
                            "&showInformation=1" +
                            "&showPrimaryStar=1" +
                            if (size > 0) {
                                "&size=$size"
                            } else {
                            }
                } else {
                    url += CUBE_SYSTEMS +
                            "/?systemName=$systemEncoded" +
                            "&showId=1" +
                            "&showCoordinates=1" +
                            "&showPermit=1" +
                            "&showInformation=1" +
                            "&showPrimaryStar=1" +
                            if (size > 0) {
                                "&size=$size"
                            } else {
                            }
                }
            }
        }
        val connection = URL(url).openConnection()
        connection.doInput = true
        val json = "{systems:" + connection.getInputStream().bufferedReader().readText() + "}"
        val jsonArray = JsonParser.parseString(json).asJsonObject.getAsJsonArray("systems")
        val systems = ArrayList<System>()
        jsonArray.forEach {
            systems.add(Gson().fromJson<System>(it.asJsonObject, System::class.java))
        }
        return systems
    }

    fun findSystemsByName(name: String): ArrayList<System> {
        return findSystems(SearchType.NAME, name)
    }

    fun findSystemsInSphere(
        initialSystem: String = "",
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0,
        radius: Int = 0
    ): ArrayList<System> {
        return findSystems(SearchType.SPHERE, initialSystem, x, y, z, radius)
    }

    fun findSystemsInCube(
        initialSystem: String = "",
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0,
        size: Int = 0
    ): ArrayList<System> {
        return findSystems(SearchType.CUBE, initialSystem, x, y, z, size)
    }

    fun getBodies(system: String): System {
        val name = URLEncoder.encode(system, "UTF-8")
        val connection = URL(
            BASE_URL + BODIES +
                    "/?systemName=$name"
        ).openConnection()
        connection.doInput = true
        val json = connection.inputStream.bufferedReader().readText()
        return Gson().fromJson<System>(json, System::class.java)
    }

    fun getBodies(system: System) {
        System.updateSystem(system, getBodies(system.name!!))
    }

    fun getFactions(system: String): System {
        val name = URLEncoder.encode(system, "UTF-8")
        val connection = URL(
            BASE_URL + FACTIONS +
                    "/?systemName=$name"
        ).openConnection()
        connection.doInput = true
        val json = connection.inputStream.bufferedReader().readText()
        return Gson().fromJson<System>(json, System::class.java)
    }

    fun getFactions(system: System) {
        System.updateSystem(system, getFactions(system.name!!))
    }

    fun getStations(system: String): System {
        val name = URLEncoder.encode(system, "UTF-8")
        val connection = URL(
            BASE_URL + STATIONS +
                    "/?systemName=$name"
        ).openConnection()
        connection.doInput = true
        val json = connection.inputStream.bufferedReader().readText()
        return Gson().fromJson<System>(json, System::class.java)
    }

    fun getStations(system: System) {
        System.updateSystem(system, getStations(system.name!!))
    }

    fun getSystemComplete(name: String): System {
        val system = getSystem(name)
        getBodies(system)
        getFactions(system)
        getStations(system)
        return system
    }

    private fun getShipyard(marketId: Long): Station {
        val connection = URL(
            BASE_URL + STATIONS + SHIPYARD +
                    "?marketId=$marketId"
        ).openConnection()
        connection.doInput = true
        val json = connection.inputStream.bufferedReader().readText()

        return Gson().fromJson<Station>(json, Station::class.java)
    }

    fun getShipyard(station: Station) {
        Station.updateStation(station, getShipyard(station.marketId))
    }

    private fun getMarket(marketId: Long): Station {
        val connection = URL(
            BASE_URL + STATIONS + MARKET +
                    "?marketId=$marketId"
        ).openConnection()
        connection.doInput = true
        val json = connection.inputStream.bufferedReader().readText()

        return GsonBuilder().registerTypeAdapter(Commodity::class.java, CommodityDeserializer())
            .create().fromJson<Station>(json, Station::class.java)
    }

    fun getMarket(station: Station) {
        Station.updateStation(station, getMarket(station.marketId))
    }

    private fun getOutfitting(marketId: Long): Station {
        val connection = URL(
            BASE_URL + STATIONS + OUTFITTING +
                    "?marketId=$marketId"
        ).openConnection()
        connection.doInput = true
        val json = connection.inputStream.bufferedReader().readText()

        return GsonBuilder().registerTypeAdapter(Module::class.java, ModuleDeserializer())
            .create().fromJson<Station>(json, Station::class.java)
    }

    fun getOutfitting(station: Station) {
        Station.updateStation(station, getOutfitting(station.marketId))
    }

    fun getCompleteStation(station: Station) {
        getShipyard(station)
        getMarket(station)
        getOutfitting(station)
    }


    fun checkApiKey(): Boolean {
        val connection = URL(
            BASE_URL + RANKS +
                    "/?commanderName=${URLEncoder.encode(commander, "UTF-8")}" +
                    "&apiKey=${if (apiKey == "") "s" else apiKey}"
        ).openConnection()
        connection.doInput = true
        val json = connection.getInputStream().bufferedReader().readText()
        val msgnum = JsonParser.parseString(json).asJsonObject["msgnum"].asInt
        return msgnum == 100
    }
}