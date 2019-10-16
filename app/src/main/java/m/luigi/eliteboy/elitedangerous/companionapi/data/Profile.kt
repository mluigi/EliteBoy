package m.luigi.eliteboy.elitedangerous.companionapi.data

class Profile {
    var commander: Commander? = null
    var lastStarport: Starport? = null
    var lastSystem: StarSystem? = null
    var ship: Ship? = null
    var ships: Map<String, Ship>? = null

}