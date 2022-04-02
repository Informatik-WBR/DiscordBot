package de.wbr.commands

import de.wbr.AudioPlayer
import de.wbr.model.Command
import de.wbr.utils.Extensions.message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class NowPlaying: Command {
    override val name: String = "np"

    override fun execute(event: MessageReceivedEvent, msg: Array<String>) {
        "Ich spiele gerade **${AudioPlayer.player.playingTrack.info.title}**".message(event.textChannel)
    }
}