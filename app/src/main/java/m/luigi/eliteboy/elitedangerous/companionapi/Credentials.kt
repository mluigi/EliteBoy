package m.luigi.eliteboy.elitedangerous.companionapi

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime

class Credentials {
    var accessToken: String? = null
    var refreshToken: String? = null
    var expiryDate: LocalDateTime? = null

    fun toJson(): String =
        GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime::class.java,
            object : JsonSerializer<LocalDateTime> {
                override fun serialize(
                    src: LocalDateTime?,
                    typeOfSrc: Type?,
                    context: JsonSerializationContext?
                ): JsonElement {
                    return JsonPrimitive(src!!.toString())
                }
            }).create().toJson(this)

    companion object {
        fun fromJson(json: String): Credentials {
            return GsonBuilder().setPrettyPrinting().registerTypeAdapter(
                Credentials::class.java,
                object : JsonDeserializer<Credentials> {
                    override fun deserialize(
                        json: JsonElement?,
                        typeOfT: Type?,
                        context: JsonDeserializationContext?
                    ): Credentials {
                        val creds = Credentials()
                        val jsonObject = json!!.asJsonObject
                        creds.refreshToken = jsonObject["refreshToken"].asString
                        creds.accessToken = jsonObject["accessToken"].asString
                        creds.expiryDate= LocalDateTime.now()
                        return creds
                    }

                }).create()
                .fromJson(json, Credentials::class.java) as Credentials
        }
    }
}