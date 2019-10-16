package m.luigi.eliteboy.elitedangerous.companionapi

import com.google.gson.annotations.SerializedName

enum class CombatRank(rank: Int) {
    @SerializedName("0")
    Harmless(0),
    @SerializedName("1")
    MostlyHarmless(1),
    @SerializedName("2")
    Novice(2),
    @SerializedName("3")
    Competent(3),
    @SerializedName("4")
    Expert(4),
    @SerializedName("5")
    Master(5),
    @SerializedName("6")
    Dangerous(6),
    @SerializedName("7")
    Deadly(7),
    @SerializedName("8")
    Elite(8)
}

enum class TradeRank(rank: Int) {
    @SerializedName("0")
    Penniless(0),
    @SerializedName("1")
    MostlyPenniless(1),
    @SerializedName("2")
    Peddler(2),
    @SerializedName("3")
    Dealer(3),
    @SerializedName("4")
    Merchant(4),
    @SerializedName("5")
    Broker(5),
    @SerializedName("6")
    Entrepreneur(6),
    @SerializedName("7")
    Tycoon(7),
    @SerializedName("8")
    Elite(8)
}

enum class ExplorationRank(rank: Int) {
    @SerializedName("0")
    Aimless(0),
    @SerializedName("1")
    MostlyAimless(1),
    @SerializedName("2")
    Scout(2),
    @SerializedName("3")
    Surveyor(3),
    @SerializedName("4")
    Explorer(4),
    @SerializedName("5")
    Pathfinder(5),
    @SerializedName("6")
    Ranger(6),
    @SerializedName("7")
    Pioneer(7),
    @SerializedName("8")
    Elite(8)
}

enum class FederationRank(rank: Int) {
    @SerializedName("0")
    None(0),
    @SerializedName("1")
    Recruit(1),
    @SerializedName("2")
    Cadet(2),
    @SerializedName("3")
    Midshipman(3),
    @SerializedName("4")
    PettyOfficer(4),
    @SerializedName("5")
    ChiefPettyOfficer(5),
    @SerializedName("6")
    WarrantOfficer(6),
    @SerializedName("7")
    Ensign(7),
    @SerializedName("8")
    Lieutenant(8),
    @SerializedName("9")
    LtCommander(9),
    @SerializedName("10")
    PostCommander(10),
    @SerializedName("11")
    PostCaptain(11),
    @SerializedName("12")
    RearAdmiral(12),
    @SerializedName("13")
    ViceAdmiral(13),
    @SerializedName("14")
    Admiral(14)
}

enum class EmpireRank(rank: Int) {
    @SerializedName("0")
    None(0),
    @SerializedName("1")
    Outsider(1),
    @SerializedName("2")
    Serf(2),
    @SerializedName("3")
    Master(3),
    @SerializedName("4")
    Squire(4),
    @SerializedName("5")
    Knight(5),
    @SerializedName("6")
    Lord(6),
    @SerializedName("7")
    Baron(7),
    @SerializedName("8")
    Viscount(8),
    @SerializedName("9")
    Count(9),
    @SerializedName("10")
    Earl(10),
    @SerializedName("11")
    Marquis(11),
    @SerializedName("12")
    Duke(12),
    @SerializedName("13")
    Prince(13),
    @SerializedName("14")
    King(14)
}

enum class CQCRank(rank: Int) {
    @SerializedName("0")
    Helpless(0),
    @SerializedName("1")
    MostlyHelpless(1),
    @SerializedName("2")
    Amateur(2),
    @SerializedName("3")
    SemiProfessional(3),
    @SerializedName("4")
    Professional(4),
    @SerializedName("5")
    Champion(5),
    @SerializedName("6")
    Hero(6),
    @SerializedName("7")
    Legend(7),
    @SerializedName("8")
    Elite(8)
}