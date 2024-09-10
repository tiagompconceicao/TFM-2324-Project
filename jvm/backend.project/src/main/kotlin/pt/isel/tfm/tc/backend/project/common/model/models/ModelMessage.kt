package pt.isel.tfm.tc.backend.project.common.model.models

import pt.isel.tfm.tc.backend.project.common.service.ModelAPI

data class ModelRequest(
    var headers: List<Pair<String,String>>,
    var modelAPI: ModelAPI,
    var messageBody: ModelMessage
)

open class ModelMessage(
        open val model: String,
        open val messages: List<Message>,
)

data class ClaudeModelMessage(
        override val model: String,
        override val messages: List<Message>,
        val max_tokens: Int
) : ModelMessage(model,messages)



open class Message(
        open val role: String,
        open val content: String,
)

data class OpenAiMessage(
        override val role: String,
        override val content: String,
        val refusal: String? = null
) : Message(role,content)