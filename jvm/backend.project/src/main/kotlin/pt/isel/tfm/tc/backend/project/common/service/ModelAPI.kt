package pt.isel.tfm.tc.backend.project.common.service

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import pt.isel.tfm.tc.backend.project.ConfigProperties
import pt.isel.tfm.tc.backend.project.common.model.*
import pt.isel.tfm.tc.backend.project.common.model.models.*

/**
 * This enum class stands to normalize and abstract the LLMs API to the Controllers
 */
enum class ModelAPI() {
    GPT {
        override fun getUrl() = "https://api.openai.com/v1/chat/completions"
        override fun getHeaders(configProperties: ConfigProperties): List<Pair<String,String>> {
            var headerList = mutableListOf<Pair<String,String>>()
            headerList.add(Pair(AUTHORIZATION,"Bearer ${configProperties.OpenAiAPIKey}"))
            return headerList
        }
        override fun buildBody(modelName: String, messages: List<Message>) = ModelMessage(modelName,messages)
        override fun getOutputModel(json: String): ModelResponse {
            val mapper = jacksonObjectMapper().registerModule(KotlinModule())
            return mapper.readValue(json, OpenAIResponse::class.java)
        }

        override fun parseResults(results: ModelResponse): String {
            val model = results as OpenAIResponse
            return model.choices[0].message.content
        }
    },
    CLAUDE {
        override fun getUrl() = "https://api.anthropic.com/v1/messages"
        override fun getHeaders(configProperties: ConfigProperties): List<Pair<String,String>> {
            var headerList = mutableListOf<Pair<String,String>>()
            headerList.add(Pair("x-api-key",configProperties.ClaudeAPIKey))
            headerList.add(Pair("anthropic-version","2023-06-01"))
            return headerList
        }
        //TODO: Automatic update of the most recent model??
        override fun buildBody(modelName: String, messages: List<Message>) = ClaudeModelMessage("claude-3-opus-20240229",messages,1024)
        override fun getOutputModel(json: String): ModelResponse {
            val mapper = jacksonObjectMapper()
            return mapper.readValue(json, ClaudeResponse::class.java)
        }

        override fun parseResults(results: ModelResponse): String {
            val model = results as ClaudeResponse
            return model.content[0].text
        }
    };
    val AUTHORIZATION = "Authorization"
    abstract fun getUrl(): String
    abstract fun getHeaders(configProperties: ConfigProperties): List<Pair<String,String>>
    abstract fun buildBody(modelName: String, messages: List<Message>): ModelMessage
    abstract fun getOutputModel(json: String): ModelResponse
    abstract fun parseResults(results: ModelResponse): String

    /**
     * Method used to build the response of a model
     */
    @Throws(Exception::class)
    fun parseOutput(chatId: String,modelResult: String, promptType: PromptType): OutputModel {
        val modelContent = parseResults(getOutputModel(modelResult))

        val contents = mutableListOf<ModelContent>()
        contents.add(ModelContent("text",modelContent))
        if (promptType != PromptType.OTHER){
            val dockerfile = detectDockerfile(modelContent)
            contents.add(ModelContent("dockerfile",dockerfile))
        }
        return OutputModel(id = chatId,contents = contents)
    }

    /**
     * Method used to detect and extract a Dockerfile in a given string.
     */
    private fun detectDockerfile(text: String): String {

        val splits = text.split("```")
        if (splits.size < 3) throw Exception("Dockerfile not found or malformed")
        val dockerSplit = splits[1]

        val regex = Regex("[Dd]ockerfile")
        return if (regex.containsMatchIn(dockerSplit)) {
            regex.split(dockerSplit)[1]
        } else {
            dockerSplit
        }

    }
}