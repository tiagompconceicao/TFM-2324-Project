package pt.isel.tfm.tc.backend.project.common.model

import java.util.*

data class ModelMessage(
        var model: String,
        var messages: List<Message>
) {
    fun init(model:String,content:String): ModelMessage {
        var messages = mutableListOf<Message>()
        messages.add(Message("system","You are a helpful assistant. You have to generate honeypot dockerfiles in order to fight against malicious agents."))
        messages.add(Message("user",content))
        return ModelMessage(model, messages)

    }
}

data class Message(
        var role: String,
        var content: String
)