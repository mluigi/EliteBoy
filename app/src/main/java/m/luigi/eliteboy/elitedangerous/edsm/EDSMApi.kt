package m.luigi.eliteboy.elitedangerous.edsm

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import m.luigi.eliteboy.elitedangerous.companionapi.data.Commodity
import m.luigi.eliteboy.elitedangerous.companionapi.data.Module
import m.luigi.eliteboy.elitedangerous.companionapi.data.deserializers.CommodityDeserializer
import m.luigi.eliteboy.elitedangerous.companionapi.data.deserializers.ModuleDeserializer
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi.SearchType.*
import m.luigi.eliteboy.elitedangerous.edsm.data.Station
import m.luigi.eliteboy.elitedangerous.edsm.data.System
import m.luigi.eliteboy.util.isNullOrZero
import m.luigi.eliteboy.util.onDefault
import m.luigi.eliteboy.util.onIO
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

    enum class FindType {
        NAME,
        SPHERE,
        CUBE
    }

    enum class SearchType(val type: String) {
        INDIPENDENT("Indipendent Systems"),
        ALLIANCE("Alliance Systems"),
        IMPERIAL("Imperial Systems"),
        FEDERAL("Federation Systems"),
        MARKET("Market"),
        BLACK_MARKET("Black Market"),
        SHIPYARD("Shipyard"),
        OUTFITTING("Outfitting"),
        CONTACTS("Contacts"),
        MAT_TRADER("Material Trader"),
        BROKER("Technology Broker"),
        INTERSTELLAR("Interstellar Factors"),
        SEARCHRESCUE("Search and Rescue"),
        UNICART("Universal Cartographics"),
        WORKSHOP("Workshop"),
        REARM("Rearm"),
        REFUEL("Refuel"),
        REPAIR("Repair"),
        MISSIONS("Missions"),
        CREW_LOUNGE("Crew Lounge"),
        TUNING("Tuning"),
        BOOM("Boom"),
        BUST("Bust"),
        CIVLIBERTY("Civil Liberty"),
        CIVUNREST("Civil Unrest"),
        CIVWAR("Civil War"),
        ELECTIONS("Elections"),
        EXPANSION("Expansion"),
        FAMINE("Famine"),
        INCURSION("Incursion"),
        INVESTMENT("Investment"),
        LOCKDOWN("Lockdown"),
        OUTBREAK("Outbreak"),
        BLIGHT("Blight"),
        PIRATE_ATTACK("Pirate Attack"),
        INFESTED("Infested"),
        RETREAT("Retreat"),
        WAR("War")
    }

    private fun makeOptionsString(
        showId: Boolean = true,
        showCoordinates: Boolean = true,
        showPermit: Boolean = false,
        showInformation: Boolean = true,
        showPrimaryStar: Boolean = false
    ): String {
        return (if (showId) "&showId=1" else "") +
                (if (showCoordinates) "&showCoordinates=1" else "") +
                (if (showPermit) "&showPermit=1" else "") +
                (if (showInformation) "&showInformation=1" else "") +
                if (showPrimaryStar) "&showPrimaryStar=1" else ""
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

    private suspend fun findSystems(
        type: FindType,
        system: String = "",
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0,
        size: Int = 0,
        showId: Boolean = true,
        showCoordinates: Boolean = true,
        showPermit: Boolean = false,
        showInformation: Boolean = true,
        showPrimaryStar: Boolean = false
    ): ArrayList<System> {
        var url = BASE_URL
        val systemEncoded = URLEncoder.encode(system, "UTF-8")
        when (type) {
            FindType.NAME -> {
                url += SYSTEMS +
                        "/?systemName=$systemEncoded" +
                        makeOptionsString(
                            showId,
                            showCoordinates,
                            showPermit,
                            showInformation,
                            showPrimaryStar
                        )
            }
            FindType.SPHERE -> {
                if (system == "") {
                    url += SPHERE_SYSTEMS +
                            "/?x=$x&y=$y&z=$z" +
                            makeOptionsString(
                                showId,
                                showCoordinates,
                                showPermit,
                                showInformation,
                                showPrimaryStar
                            ) +
                            if (size in 1..100) "&radius=$size" else ""

                } else {
                    url += SPHERE_SYSTEMS +
                            "/?systemName=$systemEncoded" +
                            makeOptionsString(
                                showId,
                                showCoordinates,
                                showPermit,
                                showInformation,
                                showPrimaryStar
                            ) +
                            if (size in 1..100) "&radius=$size" else ""
                }
            }
            FindType.CUBE -> {
                if (system == "") {
                    url += CUBE_SYSTEMS +
                            "/?x=$x&y=$y&z=$z" +
                            makeOptionsString(
                                showId,
                                showCoordinates,
                                showPermit,
                                showInformation,
                                showPrimaryStar
                            ) +
                            if (size in 1..200) "&size=$size" else ""
                } else {
                    url += CUBE_SYSTEMS +
                            "/?systemName=$systemEncoded" +
                            makeOptionsString(
                                showId,
                                showCoordinates,
                                showPermit,
                                showInformation,
                                showPrimaryStar
                            ) +
                            if (size in 1..200) "&size=$size" else ""
                }
            }
        }
        println(url)
        val connection = URL(url).openConnection()
        connection.doInput = true
        val json =
            onIO { connection.getInputStream().bufferedReader().readText() }
        val systems = ArrayList<System>()

        onDefault {
            val jsonArray = JsonParser.parseString(json).asJsonArray
            jsonArray.forEach {
                systems.add(Gson().fromJson<System>(it.asJsonObject, System::class.java))
            }
        }
        return systems
    }

    suspend fun findSystemsByName(name: String): ArrayList<System> {
        return findSystems(FindType.NAME, name)
    }

    suspend fun findSystemsInSphere(
        initialSystem: String = "",
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0,
        radius: Int = 0
    ): ArrayList<System> {
        return findSystems(FindType.SPHERE, initialSystem, x, y, z, radius)
    }

    suspend fun findSystemsInCube(
        initialSystem: String = "",
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0,
        size: Int = 0
    ): ArrayList<System> {
        return findSystems(FindType.CUBE, initialSystem, x, y, z, size)
    }

    suspend fun getBodies(system: String): System {
        val name = URLEncoder.encode(system, "UTF-8")
        val json = onIO {
            val connection = URL(
                BASE_URL + BODIES +
                        "/?systemName=$name"
            ).openConnection()
            connection.doInput = true
            connection.inputStream.bufferedReader().readText()
        }
        return onDefault { Gson().fromJson<System>(json, System::class.java) }
    }

    suspend fun getBodies(system: System) {
        System.updateSystem(system, getBodies(system.name!!))
    }

    suspend fun getFactions(system: String): System {
        val name = URLEncoder.encode(system, "UTF-8")
        val json = onIO {
            val connection = URL(
                BASE_URL + FACTIONS +
                        "/?systemName=$name"
            ).openConnection()
            connection.doInput = true
            connection.inputStream.bufferedReader().readText()
        }
        return onDefault { Gson().fromJson<System>(json, System::class.java) }
    }

    suspend fun getFactions(system: System) {
        System.updateSystem(system, getFactions(system.name!!))
    }

    suspend fun getStations(system: String): System {
        val json = onIO {
            val name = URLEncoder.encode(system, "UTF-8")
            val connection = URL(
                BASE_URL + STATIONS +
                        "/?systemName=$name"
            ).openConnection()
            connection.doInput = true
            connection.inputStream.bufferedReader().readText()
        }
        return Gson().fromJson<System>(json, System::class.java)
    }

    suspend fun getStations(system: System) {
        System.updateSystem(system, getStations(system.name!!))
    }

    suspend fun getSystemComplete(name: String): System {
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

    private suspend fun filteredNearbySystems(system: String): ArrayList<System> {
        return onIO {
            val systems =
                findSystems(
                    FindType.CUBE,
                    system,
                    size = 50,
                    showInformation = true
                )

            onDefault {
                systems.removeIf {
                    if (it.information == null) {
                        true
                    } else {
                        it.information!!.population.isNullOrZero()
                    }
                }
                systems.sortBy { it.distance }
            }

            systems
        }
    }

    private suspend fun filterBySystem(
        system: String,
        filter: suspend CoroutineScope.(systems: ArrayList<System>) -> Unit
    ): ArrayList<System> {
        return onDefault {
            val systems = filteredNearbySystems(system)

            filter(systems)

            systems.ifEmpty { ArrayList() }
        }
    }

    private suspend fun filterByStation(
        system: String,
        filter: (stations: ArrayList<Station>) -> Unit
    ): ArrayList<System> {
        return onDefault {
            val systems = filteredNearbySystems(system)

            val systemsFound = ArrayList<System>()
            var i = 0
            while (systemsFound.size < 20 && i < systems.size) {
                val sys = systems[i]

                onIO {
                    getStations(sys)
                }

                sys.stations?.let { stations ->
                    filter(stations)
                }

                if (!sys.stations.isNullOrEmpty()) {
                    systemsFound.add(sys)
                }
                i++
            }
            systemsFound
        }
    }

    suspend fun search(
        search: SearchType,
        system: String = "Sol"
    ): ArrayList<System> {
        return when (search) {
            INDIPENDENT -> {
                filterBySystem(system) { systems ->
                    systems.removeIf { it.information!!.allegiance!! == "Indipendent" }
                }
            }
            ALLIANCE -> {
                filterBySystem(system) { systems ->
                    systems.removeIf { it.information!!.allegiance!! == "Alliance" }
                }
            }
            IMPERIAL -> {
                filterBySystem(system) { systems ->
                    systems.removeIf { it.information!!.allegiance!! == "Imperial" }
                }
            }
            FEDERAL -> {
                filterBySystem(system) { systems ->
                    systems.removeIf { it.information!!.allegiance!! == "Federation" }
                }
            }
            SearchType.MARKET -> {
                filterByStation(system) { stations ->
                    stations.removeIf { !it.haveMarket }
                }
            }
            SearchType.SHIPYARD -> {
                filterByStation(system) { stations ->
                    stations.removeIf { !it.haveShipyard }
                }
            }
            SearchType.OUTFITTING -> {
                filterByStation(system) { stations ->
                    stations.removeIf { !it.haveOutfitting }
                }
            }
            BLACK_MARKET,
            CONTACTS,
            MAT_TRADER,
            BROKER,
            INTERSTELLAR,
            SEARCHRESCUE,
            UNICART,
            WORKSHOP,
            REARM,
            REFUEL,
            REPAIR,
            MISSIONS,
            CREW_LOUNGE,
            TUNING -> {
                filterByStation(system) { stations ->
                    stations.removeIf {
                        it.otherServices.isNullOrEmpty()
                                || it.otherServices!!.contains(search.type)
                    }
                }
            }
            BOOM,
            BUST,
            CIVLIBERTY,
            CIVUNREST,
            CIVWAR,
            ELECTIONS,
            EXPANSION,
            FAMINE,
            INCURSION,
            INVESTMENT,
            LOCKDOWN,
            OUTBREAK,
            BLIGHT,
            PIRATE_ATTACK,
            INFESTED,
            RETREAT,
            WAR -> {
                filterBySystem(system) { systems ->
                    systems.removeIf {
                        runBlocking {
                            getFactions(it.name!!)
                        }
                        it.factions.isNullOrEmpty() || it.factions!!.any { it.state!! == search.type }
                    }
                }
            }
        }
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