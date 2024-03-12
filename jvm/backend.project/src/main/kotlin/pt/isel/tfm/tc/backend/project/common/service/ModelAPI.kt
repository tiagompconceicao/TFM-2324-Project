package pt.isel.tfm.tc.backend.project.common.service

enum class ModelAPI {
    GPT {
        override fun getUrl() = "https://api.openai.com/v1/chat/completions"
    };

    abstract fun getUrl(): String
}