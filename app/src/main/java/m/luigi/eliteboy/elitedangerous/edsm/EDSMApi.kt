package m.luigi.eliteboy.elitedangerous.edsm

import com.google.gson.Gson
import com.google.gson.JsonParser
import m.luigi.eliteboy.elitedangerous.edsm.data.System
import java.net.URL
import java.net.URLEncoder

class EDSMApi(var commander: String = "", var apiKey: String = "") {
    companion object {
        const val BASE_URL = "https://www.edsm.net"
        const val STATUS = "/api-status-v1/elite-server"

        const val SYSTEM = "/api-v1/system"
        const val SYSTEMS = "/api-v1/systems"
        const val SPHERE_SYSTEMS = "/api-v1/sphere-systems"
        const val CUBE_SYSTEMS = "/api-v1/cube-systems"

        const val BODIES = "/api-system-v1/bodies"
        const val STATIONS = "/api-system-v1/stations"
        const val FACTIONS = "/api-system-v1/factions"

        const val GET_LOGS = "/api-logs-v1/get-logs"
        const val GET_RANKS = "/api-commander-v1/get-ranks"
        const val GET_MATS = "api-commander-v1/get-materials"
        const val UPDATE_SHIP = "/api-commander-v1/update-ship"
        const val SELL_SHIP = "/api-commander-v1/sell-ship"

        const val FROM_SOFTWARE = "EliteBoy"
        const val FROM_SOFTWARE_VERSION = "1.0"

        enum class SearchType {
            NAME,
            SPHERE,
            CUBE
        }
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
                "&showPrimaryStar=1").openConnection()
        connection.doInput = true
        val json = connection.getInputStream().bufferedReader().readText()
        return Gson().fromJson<System>(json, System::class.java)
    }

    private fun findSystems(type: SearchType, system: String = "", x: Double = 0.0, y: Double = 0.0, z: Double = 0.0, size: Int = 0): ArrayList<System> {
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
        val jsonArray = JsonParser().parse(json).asJsonObject.getAsJsonArray("systems")
        val systems = ArrayList<System>()
        jsonArray.forEach {
            systems.add(Gson().fromJson<System>(it.asJsonObject, System::class.java))
        }
        return systems
    }

    fun findSystemsByName(name: String): ArrayList<System> {
        return findSystems(SearchType.NAME, name)
    }

    fun findSystemsInSphere(initialSystem: String = "", x: Double = 0.0, y: Double = 0.0, z: Double = 0.0, radius: Int = 0): ArrayList<System> {
        return findSystems(SearchType.SPHERE, initialSystem, x, y, z, radius)
    }

    fun findSystemsInCube(initialSystem: String = "", x: Double = 0.0, y: Double = 0.0, z: Double = 0.0, size: Int = 0): ArrayList<System> {
        return findSystems(SearchType.CUBE, initialSystem, x, y, z, size)
    }

    fun getBodies(system: String): System {
        val name = URLEncoder.encode(system, "UTF-8")
        val connection = URL(
            BASE_URL + BODIES +
                "/?systemName=$name").openConnection()
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
                "/?systemName=$name").openConnection()
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
                "/?systemName=$name").openConnection()
        connection.doInput = true
        val json = connection.inputStream.bufferedReader().readText()
        return Gson().fromJson<System>(json, System::class.java)
    }

    fun getStations(system: System) {
        System.updateSystem(system, getStations(system.name!!))
    }

    fun checkApiKey(): Boolean {
        val connection = URL(
            BASE_URL + GET_RANKS +
                "/?commanderName=${URLEncoder.encode(commander, "UTF-8")}" +
                "&apiKey=$apiKey").openConnection()
        connection.doInput = true
        val json = connection.getInputStream().bufferedReader().readText()
        val msgnum = JsonParser().parse(json).asJsonObject["msgnum"].asInt
        return msgnum == 100
    }
}