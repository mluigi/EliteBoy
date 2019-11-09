package m.luigi.eliteboy.elitedangerous.edsm

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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


//TODO: Refactor this mess

@FlowPreview
@ExperimentalCoroutinesApi
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

    enum class SearchType(val type: String, val returnType: Int) {

        /* 0 returns Systems
         * 1 returns Stations
         * 2 returns Systems with factions
         * 3 is like 1 but with type
         */
        INDIPENDENT("Independent Systems", 0),
        ALLIANCE("Alliance Systems", 0),
        IMPERIAL("Imperial Systems", 0),
        FEDERAL("Federation Systems", 0),
        MARKET("Market", 1),
        BLACK_MARKET("Black Market", 1),
        SHIPYARD("Shipyard", 1),
        OUTFITTING("Outfitting", 1),
        CONTACTS("Contacts", 1),
        TRADER("Material Trader", 3),
        BROKER("Technology Broker", 3),
        INTERSTELLAR("Interstellar Factors", 1),
        SEARCHRESCUE("Search and Rescue", 1),
        UNICART("Universal Cartographics", 1),
        WORKSHOP("Workshop", 1),
        REARM("Restock", 1),
        REFUEL("Refuel", 1),
        REPAIR("Repair", 1),
        MISSIONS("Missions", 1),
        CREW_LOUNGE("Crew Lounge", 1),
        TUNING("Tuning", 1),
        BOOM("Boom", 2),
        BUST("Bust", 2),
        CIVLIBERTY("Civil Liberty", 2),
        CIVUNREST("Civil Unrest", 2),
        CIVWAR("Civil War", 2),
        ELECTIONS("Elections", 2),
        EXPANSION("Expansion", 2),
        FAMINE("Famine", 2),
        INCURSION("Incursion", 2),
        INVESTMENT("Investment", 2),
        LOCKDOWN("Lockdown", 2),
        OUTBREAK("Outbreak", 2),
        BLIGHT("Blight", 2),
        PIRATE_ATTACK("Pirate Attack", 2),
        INFESTED("Infested", 2),
        RETREAT("Retreat", 2),
        UNDREP("Under Repairs", 2),
        WAR("War", 2);

        companion object {
            fun getByType(s: String): SearchType {
                return values().first { it.type == s }
            }
        }
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


    suspend fun getSystem(name: String): System {
        val system = URLEncoder.encode(name, "UTF-8")
        val json = onIO {
            val connection = URL(
                BASE_URL + SYSTEM +
                        "/?systemName=$system" +
                        makeOptionsString(
                            showId = true,
                            showCoordinates = true,
                            showPermit = true,
                            showInformation = true,
                            showPrimaryStar = true
                        )
            ).openConnection()
            connection.doInput = true
            connection.getInputStream().bufferedReader().readText()
        }
        return onDefault { Gson().fromJson<System>(json, System::class.java) }
    }

    private suspend fun findSystems(
        type: FindType,
        system: String = "Sol",
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0,
        size: Int = 0,
        showId: Boolean = false,
        showCoordinates: Boolean = true,
        showPermit: Boolean = false,
        showInformation: Boolean = true,
        showPrimaryStar: Boolean = false,
        limit: Int = 0
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
        val json = onIO {
            val connection = URL(url).openConnection()
            connection.doInput = true
            connection.getInputStream().bufferedReader().readText()
        }
        val systems = ArrayList<System>()

        onDefault {
            try {
                val jsonArray = JsonParser.parseString(json).asJsonArray
                if (limit == 0) {
                    jsonArray.forEach {
                        systems.add(Gson().fromJson<System>(it.asJsonObject, System::class.java))
                    }
                } else if (jsonArray.size() > 0) {
                    for (i in 0 until if (limit < jsonArray.size()) limit else jsonArray.size()) {
                        systems.add(
                            Gson().fromJson<System>(
                                jsonArray[i].asJsonObject,
                                System::class.java
                            )
                        )
                    }
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        return systems
    }

    suspend fun findSystemsByName(
        name: String,
        showId: Boolean = false,
        showCoordinates: Boolean = true,
        showPermit: Boolean = false,
        showInformation: Boolean = true,
        showPrimaryStar: Boolean = false,
        limit: Int = 0
    ): ArrayList<System> {
        return findSystems(
            FindType.NAME,
            name,
            showId = showId,
            showCoordinates = showCoordinates,
            showPermit = showPermit,
            showInformation = showInformation,
            showPrimaryStar = showPrimaryStar,
            limit = limit
        )
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
        val json: String? = onIO {
            try {
                val connection = URL(
                    BASE_URL + FACTIONS +
                            "/?systemName=$name"
                ).openConnection()
                connection.doInput = true
                connection.inputStream.bufferedReader().readText()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        return onDefault {
            json?.let {
                Gson().fromJson(json, System::class.java)
            } ?: System()
        }
    }

    suspend fun getFactions(system: System) {
        System.updateSystem(system, getFactions(system.name!!))
    }

    suspend fun getStations(system: String): System {
        val json: String? = onIO {
            try {
                val name = URLEncoder.encode(system, "UTF-8")
                val connection = URL(
                    BASE_URL + STATIONS +
                            "/?systemName=$name"
                ).openConnection()
                connection.doInput = true
                connection.inputStream.bufferedReader().readText()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        return onDefault {
            json?.let {
                Gson().fromJson(json, System::class.java).apply {
                    stations!!.forEach { station ->
                        station.otherServices?.let {
                            if (it.contains("Material Trader")) {
                                getMatTraderType(station, this)
                            }
                            if (it.contains("Technology Broker")) {
                                getTechBrokerType(station, this)
                            }
                        }
                    }
                }
            } ?: System()
        }
    }

    private suspend fun getStations(system: System) {
        System.updateSystem(system, getStations(system.name!!))
    }

    suspend fun getSystemComplete(name: String): System {
        val system = getSystem(name)
        getBodies(system)
        getFactions(system)
        getStations(system)
        return system
    }

    suspend fun getShipyard(marketId: Long): Station {
        val json = onIO {
            val connection = URL(
                BASE_URL + STATIONS + SHIPYARD +
                        "?marketId=$marketId"
            ).openConnection()
            connection.doInput = true
            connection.inputStream.bufferedReader().readText()
        }

        return onDefault { Gson().fromJson<Station>(json, Station::class.java) }
    }

    suspend fun getShipyard(station: Station) {
        Station.updateStation(station, getShipyard(station.marketId))
    }

    private suspend fun getMarket(marketId: Long): Station {
        val json = onIO {
            val connection = URL(
                BASE_URL + STATIONS + MARKET +
                        "?marketId=$marketId"
            ).openConnection()
            connection.doInput = true
            connection.inputStream.bufferedReader().readText()
        }

        return onDefault {
            GsonBuilder().registerTypeAdapter(Commodity::class.java, CommodityDeserializer())
                .create().fromJson<Station>(json, Station::class.java)
        }
    }

    suspend fun getMarket(station: Station) {
        Station.updateStation(station, getMarket(station.marketId))
    }

    suspend fun getOutfitting(marketId: Long): Station {
        val json = onIO {
            val connection = URL(
                BASE_URL + STATIONS + OUTFITTING +
                        "?marketId=$marketId"
            ).openConnection()
            connection.doInput = true
            connection.inputStream.bufferedReader().readText()
        }

        return onDefault {
            GsonBuilder().registerTypeAdapter(Module::class.java, ModuleDeserializer())
                .create().fromJson<Station>(json, Station::class.java)
        }
    }

    suspend fun getOutfitting(station: Station) {
        Station.updateStation(station, getOutfitting(station.marketId))
    }

    suspend fun getCompleteStation(station: Station) {
        getShipyard(station)
        getMarket(station)
        getOutfitting(station)
    }

    private suspend fun filteredNearbySystems(
        system: String = "Sol",
        size: Int = 50
    ): ArrayList<System> {
        return onIO {
            val systems =
                findSystems(
                    FindType.CUBE,
                    system,
                    size = size,
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
        max: Int = 20,
        filter: suspend (system: System) -> Boolean
    ): Flow<System> {
        return flow {
            val systems = filteredNearbySystems(system)
            var i = 0
            var systemsFound = 0
            while (systemsFound < max && i < systems.size) {
                val sys = systems[i]
                if (filter(sys)) {
                    emit(sys)
                    systemsFound++
                }

                i++
            }
        }.flowOn(Dispatchers.Default)
    }

    private suspend fun filterByStation(
        system: String,
        max: Int = 20,
        filter: suspend (station: Station) -> Boolean
    ): Flow<System> {
        return flow {
            val systems = filteredNearbySystems(system)
            var systemsFound = 0
            var i = 0
            while (systemsFound <= max && i < systems.size) {
                val sys = systems[i]

                onIO {
                    getStations(sys)
                }

                onDefault {
                    if (!sys.stations.isNullOrEmpty()) {
                        val stationsToRemove = arrayListOf<Station>()
                        for (station in sys.stations!!) {
                            if (!filter(station)) {
                                stationsToRemove.add(station)
                            }
                        }
                        sys.stations!!.removeAll(stationsToRemove)
                    }
                }

                if (!sys.stations.isNullOrEmpty()) {
                    emit(sys)
                    systemsFound++
                }
                i++
            }
        }.flowOn(Dispatchers.Default)
    }

    suspend fun searchNearest(
        search: SearchType,
        system: String = "Sol"
    ): Flow<System> {
        return when (search) {
            INDIPENDENT -> {
                filterBySystem(system) {
                    (it.information!!.allegiance ?: "") == "Independent"
                }
            }
            ALLIANCE -> {
                filterBySystem(system) {
                    (it.information!!.allegiance ?: "") == "Alliance"
                }
            }
            IMPERIAL -> {
                filterBySystem(system) {
                    (it.information!!.allegiance ?: "") == "Empire"
                }
            }
            FEDERAL -> {
                filterBySystem(system) {
                    (it.information!!.allegiance ?: "") == "Federation"

                }
            }
            SearchType.MARKET -> {
                filterByStation(system) {
                    it.haveMarket
                }
            }
            SearchType.SHIPYARD -> {
                filterByStation(system) {
                    it.haveShipyard
                }
            }
            SearchType.OUTFITTING -> {
                filterByStation(system) {
                    it.haveOutfitting
                }
            }
            BLACK_MARKET,
            CONTACTS,
            TRADER,
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
                filterByStation(system, 30) {
                    !it.otherServices.isNullOrEmpty()
                            && it.otherServices!!.contains(search.type)

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
            UNDREP,
            WAR -> {
                filterBySystem(system) {
                    getFactions(it)
                    !it.factions.isNullOrEmpty() && it.factions!!.any { it.state!! == search.type }
                }
            }
        }
    }

    suspend fun searchSystem(data: SearchData): Flow<System> {
        val sysName = data.systemName
        val refName = data.refSystem
        val allegiance = data.allegiance
        val government = data.government
        val economy = data.economy
        val security = data.security
        val state = data.factionState

        return flow {
            if (!sysName.isBlank()) {
                val systems = findSystemsByName(sysName)
                systems.forEach {
                    emit(it)
                }
            } else {
                val systems = filteredNearbySystems(if (refName.isBlank()) "Sol" else refName, 50)
                systems.forEach {
                    var all = false
                    var gov = false
                    var eco = false
                    var sec = false
                    var sta = false
                    if (!allegiance.isBlank()) {
                        if (it.information!!.allegiance == allegiance) {
                            all = true
                        }
                    } else {
                        all = true
                    }

                    if (!government.isBlank()) {
                        if (it.information!!.government == government) {
                            gov = true
                        }
                    } else {
                        gov = true
                    }
                    if (!economy.isBlank()) {
                        if (it.information!!.economy == economy) {
                            eco = true
                        }
                    } else {
                        eco = true
                    }
                    if (!security.isBlank()) {
                        if (it.information!!.security == security) {
                            sec = true
                        }
                    } else {
                        sec = true
                    }
                    if (!state.isBlank()) {
                        if (it.information!!.factionState == state) {
                            sta = true
                        }
                    } else {
                        sta = true
                    }
                    if (all && gov && eco && sec && sta) {
                        emit(it)
                    }
                }
            }
        }
    }

    suspend fun searchStation(data: SearchData, max: Int = 20): Flow<System> {
        val refName = data.refSystem
        val allegiance = data.allegiance
        val government = data.government
        val economy = data.economy
        val ships = data.ships.split(", ")
        val modules = onDefault {
            data.modules.split(", ").map {
                if (it.contains("Alloy") || it.contains("Composite")) {
                    it.split(" - ").first()
                } else {
                    it
                }
            }
        }
        val commodities = data.commodities.split(", ")

        return filterByStation(if (refName.isBlank()) "Sol" else refName) {
            var all = false
            var gov = false
            var eco = false
            var shi = false
            var mod = false
            var com = false

            if (!allegiance.isBlank()) {
                if (it.allegiance == allegiance) {
                    all = true
                }
            } else {
                all = true
            }
            if (!government.isBlank()) {
                if (it.government == government) {
                    gov = true
                }
            } else {
                gov = true
            }
            if (!economy.isBlank()) {
                if (it.economy == economy) {
                    eco = true
                }
            } else {
                eco = true
            }


            if (!ships.isNullOrEmpty()) {
                if (it.haveShipyard) {
                    getShipyard(it)
                    if (it.ships!!.map { it.name!! }.containsAll(ships)) {
                        shi = true
                    }
                }
            } else {
                shi = true
            }

            if (!modules.isNullOrEmpty()) {
                if (it.haveOutfitting) {
                    getOutfitting(it)
                    if (it.outfitting!!.map { it.name!! }.containsAll(modules)) {
                        mod = true
                    }
                }
            } else {
                mod = true
            }

            if (!commodities.isNullOrEmpty()) {
                if (it.haveMarket) {
                    getMarket(it)
                    if (it.commodities!!.map { it.name }.containsAll(commodities)) {
                        com = true
                    }
                }
            } else {
                com = true
            }

            all && gov && eco && shi && mod && com
        }
    }


    suspend fun checkApiKey(): Boolean {
        val json = onIO {
            val connection = URL(
                BASE_URL + RANKS +
                        "/?commanderName=${URLEncoder.encode(commander, "UTF-8")}" +
                        "&apiKey=${if (apiKey == "") "s" else apiKey}"
            ).openConnection()
            connection.doInput = true
            connection.getInputStream().bufferedReader().readText()
        }
        val msgnum = onDefault { JsonParser.parseString(json).asJsonObject["msgnum"].asInt }
        return msgnum == 100
    }

    //I have to do this, i am forced

    private suspend fun getMatTraderType(station: Station, system: System) {
        val page = getEDSMPage(station, system)

        station.traderType = onDefault {
            when {
                page.contains("<strong class=\"scramble\">Encoded</strong>") -> "Encoded"
                page.contains("<strong class=\"scramble\">Manufactured</strong>") -> "Manufactured"
                page.contains("<strong class=\"scramble\">Raw</strong>") -> "Raw"
                else -> ""
            }
        }
    }

    private suspend fun getTechBrokerType(station: Station, system: System) {
        val page = getEDSMPage(station, system)
        station.brokerType = onDefault {
            when {
                page.contains("<strong class=\"scramble\">Guardian</strong>") -> "Guardian"
                page.contains("<strong class=\"scramble\">Human</strong>") -> "Human"
                else -> ""
            }
        }
    }


    private suspend fun getEDSMPage(station: Station, system: System): String {
        val url = "https://www.edsm.net/en/system/stations/id/" +
                "${system.id}/name/${URLEncoder.encode(system.name, "UTF-8")}" +
                "/details/idS/${station.id}/nameS/${URLEncoder.encode(station.name, "UTF-8")}"
        return onIO { URL(url).readText() }
    }
}