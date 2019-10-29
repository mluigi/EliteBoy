package m.luigi.eliteboy.elitedangerous.companionapi.data.deserializers

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import m.luigi.eliteboy.elitedangerous.companionapi.data.Module
import java.lang.reflect.Type

class ModuleDeserializer : JsonDeserializer<Module> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Module {
        val jsonObject = json!!.asJsonObject

        if (jsonObject["id"].asString.contains(Regex("[a-z]."))) {
            jsonObject.addProperty("sId",jsonObject["id"].asString)
            jsonObject.remove("id")
        }

        return Gson().fromJson(jsonObject, Module::class.java)
    }

}