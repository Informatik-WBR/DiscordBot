package de.wbr.commands

import de.wbr.AudioPlayer
import de.wbr.model.Command
import de.wbr.utils.Extensions.message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.math.max
import kotlin.math.min

class Volume: Command {
    override val name: String = "volume"

    override fun execute(event: MessageReceivedEvent, msg: Array<String>) {
        if (System.getProperty("os.arch").also { println("ARCH: $it") } == "armv7l") {
            event.channel.sendMessage("Sorry! Volume commands are not supported on the current Runtime.").queue()
            return
        }
        val ch = event.message.textChannel
        AudioPlayer.player.volume = min(100, max(0, msg[1].toIntOrNull() ?: let { "${msg[1]} ist keine Ganzzahl!".message(ch); return })).also {
            "Set Volume to $it".message(ch)
            println("${event.author.name} (${event.author.id}) set volume to $it")
        }
    }
}