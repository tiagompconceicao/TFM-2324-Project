package pt.isel.tfm.tc.backend.project.controllers.models;

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.tfm.tc.backend.project.ConfigProperties
import pt.isel.tfm.tc.backend.project.common.CHATS_PATH
import pt.isel.tfm.tc.backend.project.common.authorization.USER_ATTRIBUTE_KEY
import pt.isel.tfm.tc.backend.project.common.data.PromptData
import pt.isel.tfm.tc.backend.project.common.model.Chat
import pt.isel.tfm.tc.backend.project.common.model.models.Message
import pt.isel.tfm.tc.backend.project.common.model.models.ModelRequest
import pt.isel.tfm.tc.backend.project.common.model.OutputModel
import pt.isel.tfm.tc.backend.project.common.model.Prompt
import pt.isel.tfm.tc.backend.project.common.service.ModelAPI
import pt.isel.tfm.tc.backend.project.common.service.ModelService
import pt.isel.tfm.tc.backend.project.common.service.PromptType

@ConfigurationPropertiesScan
@RestController
@RequestMapping(CHATS_PATH)
public class ModelController(private val modelService: ModelService, private val promptData: PromptData, private val configProperties: ConfigProperties) {

    val USER = "user"

    @ExceptionHandler(Exception::class)
    fun handleException(ex:Exception): ResponseEntity<String> {
        return ResponseEntity
                .status(500)
                .body(ex.message)
    }

    /**
     * Obtain all chats own by the user
     */

    @GetMapping()
    fun getChats(
            request: HttpServletRequest
    ): ResponseEntity<MutableList<Chat>> {
        val uid = request.getAttribute(USER_ATTRIBUTE_KEY) as String
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/json"))
                .body(promptData.getChats(uid))

    }

    /**
     * Obtain a specific chat created by the user
     */
    @GetMapping("/{chatId}")
    fun getSpecificChat(
            @PathVariable chatId: String
    ): ResponseEntity<MutableList<Prompt>> {
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/json"))
                .body(promptData.getChatMessages(chatId))

    }

    /**
     * Method that handle the request of the user to execute a prompt of a specific model
     */

    @PostMapping
    fun executePrompt(
            @RequestBody prompt: PromptInputModel,
            request: HttpServletRequest
    ): ResponseEntity<Any> {

        val uid = request.getAttribute(USER_ATTRIBUTE_KEY) as String

        val modelAPI = getAPI(prompt.model)
        val promptType = getPromptType(prompt.type)

        val messages = mutableListOf<Message>()
        messages.add(Message(USER, prompt.text))

        val chatId = if (prompt.chatId == null){
            promptData.createChat(
                    text = prompt.text,
                    author = true,
                    username = uid
            )
        } else {
            promptData.addPrompt(
                    text = prompt.text,
                    author = true,
                    chatId = prompt.chatId,
                    username = uid
            )
        }

        val outputModel: OutputModel
        try {
            outputModel = buildOutput(chatId,modelAPI,prompt.model,messages,3,promptType)
        } catch (exception: Exception){
            throw Exception(exception)
        }

        promptData.addPrompt(
                text = outputModel.contents[0].text,
                author = false,
                chatId = chatId,
                username = uid
        )

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/json"))
                .body(outputModel)
    }

    /**
     * Method that sends the prompt to the LLM DAO, it also implements a retry mechanism in cases of timeouts or server failures
     */
    fun buildOutput(chatId: String, modelAPI: ModelAPI, modelName: String, messages: MutableList<Message>, retriesLeft: Int, promptType: PromptType): OutputModel{
        if (retriesLeft == 0) throw Exception("Max retries surpassed")
        val outputModel: OutputModel
        try {
            val response = modelService.execPrompt(ModelRequest(
                    headers = modelAPI.getHeaders(configProperties),
                    modelAPI = modelAPI,
                    messageBody = modelAPI.buildBody(modelName,messages))) ?: throw Exception("LLM didn't responded")
            outputModel = modelAPI.parseOutput(chatId,response,promptType)
        } catch(exception: Exception) {
            return buildOutput(chatId, modelAPI, modelName, messages, retriesLeft-1,promptType)
        }
        return outputModel
    }

    /**
     * GPT-4 used for default clause
     */
    fun getAPI(modelName: String): ModelAPI {
        return when(modelName){
            //Eventually add here other models auth
            "gpt-4" -> ModelAPI.GPT
            "claude" -> ModelAPI.CLAUDE
            else -> {ModelAPI.GPT}
        }
    }

    /**
     * Obtain the prompt type, the 'OTHER' type disables the retry mechanism
     */
    fun getPromptType(promptType: String): PromptType {
        return when(promptType){
            //Eventually add here other models auth
            "CTF" -> PromptType.CTF
            "Honeypot" -> PromptType.Honeypot
            else -> PromptType.OTHER
        }
    }
}


