package m.luigi.eliteboy.elitedangerous.edsm.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Log {
    var shipId = 0
    var system = ""
    var firstDiscover = false
    private var date = ""

    fun date() = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))!!
}