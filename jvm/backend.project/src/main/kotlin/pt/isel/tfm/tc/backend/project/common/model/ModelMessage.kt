package pt.isel.tfm.tc.backend.project.common.model

import java.util.*

data class ModelMessage(
        var model: String,
        var messages: List<Message>
)
data class Message(
        var role: String,
        var content: String
)