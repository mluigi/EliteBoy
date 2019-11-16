package m.luigi.eliteboy.util


const val federationIcon = "https://edassets.org/static/img/factions/Federation.png"
const val empireIcon = "https://edassets.org/static/img/factions/Empire.png"
const val allianceIcon = "https://edassets.org/static/img/factions/Alliance.png"
val explorationRankImages = arrayOf(
    "https://edassets.org/static/img/pilots-federation/explorer/rank-1.png",
    "https://edassets.org/static/img/pilots-federation/explorer/rank-2.png",
    "https://edassets.org/static/img/pilots-federation/explorer/rank-3.png",
    "https://edassets.org/static/img/pilots-federation/explorer/rank-4.png",
    "https://edassets.org/static/img/pilots-federation/explorer/rank-5.png",
    "https://edassets.org/static/img/pilots-federation/explorer/rank-6.png",
    "https://edassets.org/static/img/pilots-federation/explorer/rank-7.png",
    "https://edassets.org/static/img/pilots-federation/explorer/rank-8.png",
    "https://edassets.org/static/img/pilots-federation/explorer/rank-9.png"
)
val tradingRankImages = arrayOf(
    "https://edassets.org/static/img/pilots-federation/trading/rank-1-trading.png",
    "https://edassets.org/static/img/pilots-federation/trading/rank-2-trading.png",
    "https://edassets.org/static/img/pilots-federation/trading/rank-3-trading.png",
    "https://edassets.org/static/img/pilots-federation/trading/rank-4-trading.png",
    "https://edassets.org/static/img/pilots-federation/trading/rank-5-trading.png",
    "https://edassets.org/static/img/pilots-federation/trading/rank-6-trading.png",
    "https://edassets.org/static/img/pilots-federation/trading/rank-7-trading.png",
    "https://edassets.org/static/img/pilots-federation/trading/rank-8-trading.png",
    "https://edassets.org/static/img/pilots-federation/trading/rank-9-trading.png"
)
val combatRankImages = arrayOf(
    "https://edassets.org/static/img/pilots-federation/combat/rank-1-combat.png",
    "https://edassets.org/static/img/pilots-federation/combat/rank-2-combat.png",
    "https://edassets.org/static/img/pilots-federation/combat/rank-3-combat.png",
    "https://edassets.org/static/img/pilots-federation/combat/rank-4-combat.png",
    "https://edassets.org/static/img/pilots-federation/combat/rank-5-combat.png",
    "https://edassets.org/static/img/pilots-federation/combat/rank-6-combat.png",
    "https://edassets.org/static/img/pilots-federation/combat/rank-7-combat.png",
    "https://edassets.org/static/img/pilots-federation/combat/rank-8-combat.png",
    "https://edassets.org/static/img/pilots-federation/combat/rank-9-combat.png"
)
val cqcRankImages = arrayOf(
    "https://edassets.org/static/img/pilots-federation/cqc/rank-1-cqc.png",
    "https://edassets.org/static/img/pilots-federation/cqc/rank-2-cqc.png",
    "https://edassets.org/static/img/pilots-federation/cqc/rank-3-cqc.png",
    "https://edassets.org/static/img/pilots-federation/cqc/rank-4-cqc.png",
    "https://edassets.org/static/img/pilots-federation/cqc/rank-5-cqc.png",
    "https://edassets.org/static/img/pilots-federation/cqc/rank-6-cqc.png",
    "https://edassets.org/static/img/pilots-federation/cqc/rank-7-cqc.png",
    "https://edassets.org/static/img/pilots-federation/cqc/rank-8-cqc.png",
    "https://edassets.org/static/img/pilots-federation/cqc/rank-9-cqc.png"
)

val stationsImages = mapOf(
    "Coriolis Starport" to "https://edassets.org/static/img/stations/Coriolis.png",
    "Ocellus Starport" to "https://edassets.org/static/img/stations/Ocellus.png",
    "Orbis Starport" to "https://edassets.org/static/img/stations/Orbis.png",
    "Outpost" to "https://edassets.org/static/img/stations/Outpost.png",
    "Mega ship" to "https://edassets.org/static/img/stations/Mega-Ship_Icon.png",
    "Asteroid base" to "https://edassets.org/static/img/stations/Asteroid_Station_Icon.png",
    "Planetary Outpost" to "https://edassets.org/static/img/settlements/settlement_sm.png",
    "Planetary Port" to "https://edassets.org/static/img/settlements/surface_port_sm.png"
)

fun stationImage(type: String): String {
    return stationsImages[type] ?: "https://edassets.org/static/img/stations/Coriolis.png"
}

fun getNewsImageUrl(id: String): String {
    return "https://hosting.zaonce.net/elite-dangerous/galnet/$id.png"
}

val shipIdToImgId = mapOf(
    "adder" to "adder",
    "typex_3" to "alliance-challenger",
    "typex" to "alliance-chieftain",
    "typex_2" to "alliance-crusader",
    "anaconda" to "anaconda",
    "asp" to "asp-explorer",
    "asp_scout" to "asp-scout",
    "belugaliner" to "beluga-liner",
    "cobramkiii" to "cobra-mk-iii",
    "cobramkiv" to "cobra-mk-iv",
    "diamondbackxl" to "diamondback-explorer",
    "diamondback" to "diamondback-scout",
    "dolphin" to "dolphin",
    "eagle" to "eagle-mk-ii",
    "federation_dropship_mkii" to "federal-assault-ship",
    "federation_corvette" to "federal-corvette",
    "federation_dropship" to "federal-dropship",
    "federation_gunship" to "federal-gunship",
    "ferdelance" to "fer-de-lance",
    "hauler" to "hauler",
    "empire_trader" to "imperial-clipper",
    "empire_courier" to "imperial-courier",
    "cutter" to "imperial-cutter",
    "empire_eagle" to "imperial-eagle",
    "independant_trader" to "keelback",
    "krait_mkii" to "krait-mk-ii",
    "krait_light" to "krait-phantom",
    "orca" to "orca",
    "mamba" to "mamba",
    "python" to "python",
    "sidewinder" to "sidewinder",
    "type6" to "type-6-transporter",
    "type7" to "type-7",
    "type9" to "type-9-heavy",
    "type9_military" to "type-10-defender",
    "viper" to "viper-mk-iii",
    "viper_mkiv" to "viper-mk-iv",
    "vulture" to "vulture"
)

fun getShipImageUrl(id: String): String {
    return "https://edassets.org/static/img/ship-schematics/qohen-leth/${shipIdToImgId[id]}.png"
}