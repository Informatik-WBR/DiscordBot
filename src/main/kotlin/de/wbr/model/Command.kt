package de.wbr.model

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface Command {
    val name: String
    fun execute(event: MessageReceivedEvent, msg: Array<String>)
}