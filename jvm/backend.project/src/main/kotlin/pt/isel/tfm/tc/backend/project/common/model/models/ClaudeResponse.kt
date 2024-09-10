package pt.isel.tfm.tc.backend.project.common.model.models

data class ClaudeResponse (
        val id: String,
        val type: String,
        val role: String,
        val model: String,
        val content: Array<ModelContent>,
        val stop_reason: String,
        val stop_sequence: String?,
        val usage: ClaudeUsage
) : ModelResponse(){}

data class ModelContent(
    val type: String,
    val text: String
)

data class ClaudeUsage(
    val input_tokens: Int,
    val output_tokens: Int
)