package de.wbr.commands

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import de.wbr.AudioPlayer
import de.wbr.model.Command
import de.wbr.utils.Extensions.message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Play: Command {
    override val name: String = "play"

    override fun execute(event: MessageReceivedEvent, msg: Array<String>) {
        val ch = event.textChannel
        if (event.author.isBot) return
        val channel = event.member!!.voiceState?.channel ?: return
        val manager = event.guild.audioManager
        manager.sendingHandler = AudioPlayer
        manager.openAudioConnection(channel)
        AudioPlayer.playerManager.loadItemOrdered(manager, msg[1], object: AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack?) {
                "Playing ${track?.info?.title}".message(ch)
                AudioPlayer.scheduler.queue(track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist?) {
                AudioPlayer.player.playTrack(playlist?.tracks?.get(0))
            }

            override fun noMatches() {
                "No matches found!".message(ch)
            }

            override fun loadFailed(exception: FriendlyException?) {
                "Error while loading! Look at the Console.".message(ch)
                exception?.printStackTrace()
            }
        })
    }
}
