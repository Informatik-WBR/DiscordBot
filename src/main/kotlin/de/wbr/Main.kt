@file:JvmName("Main")

package de.wbr

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.wbr.model.Command
import de.wbr.model.data.ConfigModel
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ConfigurationBuilder
import java.io.File

object Main : EventListener {

    val gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    lateinit var bot: JDA
    lateinit var config: ConfigModel

    val commands: Set<Command> = Reflections(
        "de.wbr.commands", ConfigurationBuilder().addClassLoaders(
            ClassLoader.getSystemClassLoader(),
            ClassLoader.getPlatformClassLoader()
        ).setScanners(
            SubTypesScanner(false)
        )
    ).getSubTypesOf(Command::class.java)
        .map { it.getConstructor().newInstance().also { a -> println("Adding ${a.name}") } }.toSet()

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            File("natives/linux-arm/libconnector.so").let {
                if (it.exists()) {
                    System.load(it.absolutePath)
                }
            }
        } catch (e: Exception) {
            println("Failed to load libconnector.so")
        }
        val file = File("config.json").also { it.createNewFile() }
        config = gson.fromJson(File("config.json").reader(), ConfigModel::class.java) ?: ConfigModel()
        file.writeText(gson.toJson(config))
        bot = JDABuilder.createDefault(config.token).setActivity(
            Activity.listening("to your walls")
        ).setStatus(OnlineStatus.DO_NOT_DISTURB).build()
        try {
            bot.awaitReady()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        bot.addEventListener(this)
    }

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is MessageReceivedEvent -> {
                val msg = event.message.contentRaw.split(" ")
                commands.find { msg[0].removePrefix("%") == it.name }?.execute(event, msg.toTypedArray())
            }
            is SlashCommandInteractionEvent -> {
                event.commandType
            }
        }
    }
}