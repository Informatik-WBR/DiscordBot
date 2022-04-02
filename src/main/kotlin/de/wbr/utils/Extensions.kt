package de.wbr.utils

import net.dv8tion.jda.api.entities.TextChannel

object Extensions {
    fun String.message(channel: TextChannel) = channel.sendMessage(this).queue()

}