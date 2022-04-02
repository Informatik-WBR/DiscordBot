package de.wbr

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer
import java.util.concurrent.LinkedBlockingQueue


object AudioPlayer: AudioSendHandler {
    val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    init {
        AudioSourceManagers.registerRemoteSources(playerManager)
    }
    val player = playerManager.createPlayer()
    val scheduler = Scheduler(player)
    init {
        player.addListener(scheduler)
    }
    private var lastFrame: AudioFrame? = null
    override fun canProvide(): Boolean {
        lastFrame = player.provide()
        return lastFrame != null
    }

    override fun provide20MsAudio(): ByteBuffer? =
        ByteBuffer.wrap(lastFrame!!.data)

    override fun isOpus(): Boolean {
        return true
    }
    class Scheduler(val player: AudioPlayer): AudioEventAdapter() {
        val queue = LinkedBlockingQueue<AudioTrack>()
        fun queue(track: AudioTrack?) {
            // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
            // something is playing, it returns false and does nothing. In that case the player was already playing so this
            // track goes to the queue instead.
            if (!player.startTrack(track, true)) {
                queue.offer(track)
            }
        }

        override fun onTrackException(player: AudioPlayer?, track: AudioTrack?, exception: FriendlyException?) {
            println("Something went wrong. Exception: $exception")
            print("Stacktrace: ")
            exception?.printStackTrace()
        }

        /**
         * Start the next track, stopping the current one if it is playing.
         */
        fun nextTrack() {
            // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
            // giving null to startTrack, which is a valid argument and will simply stop the player.
            player.startTrack(queue.poll(), false)
        }

        override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason) {
            // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
            if (endReason.mayStartNext) {
                nextTrack()
            }
        }
    }
}