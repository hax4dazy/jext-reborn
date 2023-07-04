package me.spartacus04.jext.config

import me.spartacus04.jext.ServerVersion

class ConfigData {
    companion object {
        lateinit var CONFIG: Config
        lateinit var DISCS: List<Disc>
        lateinit var LANG: LanguageManager
        lateinit var VERSION: ServerVersion
    }
}