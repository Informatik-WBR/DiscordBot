package de.wbr.commands

import de.wbr.AudioPlayer
import de.wbr.model.Command
import de.wbr.utils.Extensions.message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Queue: Command {
    override val name: String = "queue"

    override fun execute(event: MessageReceivedEvent, msg: Array<String>) {
        AudioPlayer.scheduler.queue.takeIf { it.isNotEmpty() }?.joinToString(" ") {
            it.info.let { info -> "${AudioPlayer.scheduler.queue.indexOf(it)} -> ${info.title} (${info.author})" }
        }?.message(event.textChannel)
    }
}