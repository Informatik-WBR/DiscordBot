package de.wbr.commands

import de.wbr.AudioPlayer
import de.wbr.model.Command
import de.wbr.utils.Extensions.message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.math.max
import kotlin.math.min

class Skip: Command {
    override val name: String = "skip"

    override fun execute(event: MessageReceivedEvent, msg: Array<String>) {
        AudioPlayer.player.volume = min(max(0, msg[1].toIntOrNull() ?: let { "${msg[1]} ist keine Ganzzahl!".message(event.textChannel); return }), 100).also {
            "Set Volume to $it".message(event.textChannel)
            println("${event.author.name} (${event.author.id}) set volume to $it")
        }
    }
}