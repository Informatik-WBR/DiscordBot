package de.wbr.commands

import de.wbr.AudioPlayer
import de.wbr.model.Command
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Clear: Command {
    override val name: String = "clear"
    override fun execute(event: MessageReceivedEvent, msg: Array<String>) {
        AudioPlayer.scheduler.queue.clear()
        AudioPlayer.player.stopTrack()
    }
}