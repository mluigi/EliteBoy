package m.luigi.eliteboy.util

import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi

val governmentTypes = arrayListOf(
    "Anarchy",
    "Communism",
    "Confederacy",
    "Corporate",
    "Cooperative",
    "Democracy",
    "Dictatorship",
    "Feudal",
    "Imperial",
    "Patronage",
    "Prison Colony",
    "Theocracy",
    "Workshop",
    "None",
    "Engineer",
    "Prison"
).toTypedArray()

val allegianceTypes = arrayListOf(
    "Alliance",
    "Empire",
    "Federation",
    "Guardian",
    "Independent",
    "None",
    "Pilots Federation",
    "Pirate",
    "Thargoid"
).toTypedArray()

val economyTypes = arrayListOf(
    "Agriculture",
    "Extraction",
    "High Tech",
    "Industrial",
    "Military",
    "Prison",
    "Refinery",
    "Service",
    "Terraforming",
    "Colony",
    "Damage",
    "Repair",
    "None",
    "Rescue"
).toTypedArray()

val securityTypes = arrayListOf(
    "Low",
    "Medium",
    "High",
    "Anarchy",
    "Lawless"
).toTypedArray()

val factionStates = EDSMApi.SearchType.values().sliceArray(21..38).map { it.type }.toTypedArray()