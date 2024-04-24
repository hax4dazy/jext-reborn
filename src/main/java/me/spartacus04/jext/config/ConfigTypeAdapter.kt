package me.spartacus04.jext.config

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import me.spartacus04.jext.JextState.GSON
import me.spartacus04.jext.config.fields.FieldGuiStyle
import me.spartacus04.jext.config.fields.FieldJukeboxBehaviour
import me.spartacus04.jext.config.fields.FieldLanguageMode

internal class ConfigTypeAdapter : TypeAdapter<Config>() {
    override fun read(reader: JsonReader): Config {
        val config = Config()
        reader.beginObject()

        while(reader.hasNext()) {
            when(reader.nextName()) {
                "lang" -> config.LANGUAGE_MODE = GSON.fromJson(reader.nextString(), FieldLanguageMode::class.java)
                "jukebox-behaviour" -> config.JUKEBOX_BEHAVIOUR = GSON.fromJson(reader.nextString(), FieldJukeboxBehaviour::class.java)
                "jukebox-gui-size" -> config.GUI_SIZE = reader.nextInt()
                "jukebox-gui-style" -> config.GUI_STYLE = GSON.fromJson(reader.nextString(), FieldGuiStyle::class.java)
                "disable-music-overlap" -> config.DISABLE_MUSIC_OVERLAP = reader.nextBoolean()
                "jukebox-range" -> config.JUKEBOX_RANGE = reader.nextInt()
                "disc-loottables-limit" -> {
                    reader.beginObject()
                    while(reader.hasNext()) {
                        config.DISC_LIMIT[reader.nextName()] = reader.nextInt()
                    }
                    reader.endObject()
                }
                "fragment-loottables-limit" -> {
                    reader.beginObject()
                    while(reader.hasNext()) {
                        config.FRAGMENT_LIMIT[reader.nextName()] = reader.nextInt()
                    }
                    reader.endObject()
                }
                "check-for-updates" -> config.CHECK_FOR_UPDATES = reader.nextBoolean()
                "allow-metrics" -> config.ALLOW_METRICS = reader.nextBoolean()
                "force-resource-pack" -> config.FORCE_RESOURCE_PACK = reader.nextBoolean()
                "enable-resource-pack-host" -> config.RESOURCE_PACK_HOST = reader.nextBoolean()
                "web-interface-port" -> config.WEB_INTERFACE_PORT = reader.nextInt()
                "override-web-interface-base-url" -> config.WEB_INTERFACE_BASE_URL = reader.nextString()
                "web-interface-api-enabled" -> config.WEB_INTERFACE_API_ENABLED = reader.nextBoolean()
                "web-interface-password" -> config.WEB_INTERFACE_PASSWORD = reader.nextString()
                else -> reader.skipValue()
            }
        }

        reader.endObject()
        return config
    }

    override fun write(writer: JsonWriter, value: Config) {
        writer.beginObject()

        writer.name("\$schema").value("https://raw.githubusercontent.com/spartacus04/jext-reborn/master/schemas/config.json")

        writer.name("lang").value(GSON.toJson(value.LANGUAGE_MODE))
        writer.name("jukebox-behaviour").value(GSON.toJson(value.JUKEBOX_BEHAVIOUR))
        writer.name("jukebox-gui-style").value(GSON.toJson(value.GUI_STYLE))
        writer.name("jukebox-gui-size").value(value.GUI_SIZE)
        writer.name("disable-music-overlap").value(value.DISABLE_MUSIC_OVERLAP)
        writer.name("jukebox-range").value(value.JUKEBOX_RANGE)

        writer.name("disc-loottables-limit").beginObject()
        value.DISC_LIMIT.forEach { (key, value) ->
            writer.name(key).value(value)
        }
        writer.endObject()

        writer.name("fragment-loottables-limit").beginObject()
        value.FRAGMENT_LIMIT.forEach { (key, value) ->
            writer.name(key).value(value)
        }

        writer.endObject()

        writer.name("check-for-updates").value(value.CHECK_FOR_UPDATES)
        writer.name("allow-metrics").value(value.ALLOW_METRICS)
        writer.name("force-resource-pack").value(value.FORCE_RESOURCE_PACK)
        writer.name("enable-resource-pack-host").value(value.RESOURCE_PACK_HOST)
        writer.name("web-interface-port").value(value.WEB_INTERFACE_PORT)
        writer.name("override-web-interface-base-url").value(value.WEB_INTERFACE_BASE_URL)
        writer.name("web-interface-api-enabled").value(value.WEB_INTERFACE_API_ENABLED)
        writer.name("web-interface-password").value(value.WEB_INTERFACE_PASSWORD)

        writer.endObject()
    }
}