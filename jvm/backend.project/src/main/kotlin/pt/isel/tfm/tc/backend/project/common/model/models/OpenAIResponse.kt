package pt.isel.tfm.tc.backend.project.common.model.models

data class OpenAIResponse(
        val id : String = "",
        val `object` : String = "",
        val created : Int = 0,
        val model : String = "",
        val choices: Array<ChoicesModel> = arrayOf(),
        val usage: UsageModel = UsageModel(0,0,0),
        val system_fingerprint : String? = "null",
): ModelResponse() {}

data class ChoicesModel(
        val index: Int,
        val message: OpenAiMessage,
        val logprobs: String?,
        val finish_reason: String
){}

data class UsageModel(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int,
){}
