package m.luigi.eliteboy.elitedangerous.companionapi.data.deserializers

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import m.luigi.eliteboy.elitedangerous.companionapi.data.Commodity
import java.lang.reflect.Type

class CommodityDeserializer : JsonDeserializer<Commodity> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Commodity {
        val jsonObject = json!!.asJsonObject
        if (jsonObject.has("demandBracket")) {
            if (jsonObject["demandBracket"].asString == "") {
                jsonObject.addProperty("demandBracket", 0)
            }
        }
        if (jsonObject["id"].asString.contains(Regex("[a-z]."))) {
            jsonObject.addProperty("sId", jsonObject["id"].asString)
            jsonObject.remove("id")
        }
        return Gson().fromJson(jsonObject, Commodity::class.java)
    }
}