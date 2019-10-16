package m.luigi.eliteboy.elitedangerous.companionapi.data.deserializers

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import m.luigi.eliteboy.elitedangerous.companionapi.data.Starport
import java.lang.reflect.Type

class StarportDeserializer : JsonDeserializer<Starport> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Starport {
        val jsonObject = json!!.asJsonObject
        if (jsonObject["services"].isJsonArray){
            jsonObject.remove("services")
        }
        return Gson().fromJson(jsonObject, Starport::class.java)
    }
}