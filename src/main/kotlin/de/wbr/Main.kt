package de.wbr

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.wbr.datamodel.ConfigModel
import java.io.File

val gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
lateinit var config: ConfigModel
fun main(args: Array<String>) {
    config = gson.fromJson(File("config.json").reader(), ConfigModel::class.java)
}