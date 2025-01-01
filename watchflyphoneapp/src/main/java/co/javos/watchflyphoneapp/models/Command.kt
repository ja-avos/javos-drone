package co.javos.watchflyphoneapp.models

import com.google.gson.JsonObject
import com.google.gson.JsonParser

enum class CommandType {
    ACTION,
    STATUS_UPDATE
}

class Command(val type: CommandType, val content: String) {

    fun toJsonObject(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", type.name)
        jsonObject.addProperty("content", content)
        return jsonObject
    }

    companion object {
        private fun fromJsonObject(jsonObject: JsonObject): Command {
            val type = CommandType.valueOf(jsonObject.get("type").asString)
            val content = jsonObject.get("content").asString
            return Command(type, content)
        }
        fun fromString(string: String): Command {
            val json = JsonParser.parseString(string).asJsonObject
            return fromJsonObject(json)
        }
    }

    override fun toString(): String {
        return toJsonObject().toString()
    }
}