package pt.isel.tfm.tc.backend.project.common.service

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import pt.isel.tfm.tc.backend.project.ConfigProperties
import pt.isel.tfm.tc.backend.project.common.model.Message
import pt.isel.tfm.tc.backend.project.common.model.ModelMessage

@Service
@ConfigurationPropertiesScan
class ModelService(private val configProperties: ConfigProperties) {
    val client = RestClient.create()
    val USER = "user"
    val AUTHORIZATION = "Authorization"
    fun execPrompt(modelName: String, promptText: String, promptType: String): String? {
        val messages = PromptType.valueOf(promptType).buildMessages()
        messages.add(Message(USER, promptText))
        val modelAPI = ModelAPI.valueOf(modelName)
        return client.post()
                .uri(modelAPI.getUrl())
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, getAPIAuth(modelAPI))
                .body(ModelMessage(model = modelName, messages = messages))
                .retrieve()
                .body(String::class.java)
    }

    fun getAPIAuth(modelAPI: ModelAPI): String{
        return when(modelAPI){
            //Eventually add here other models auth
            else -> "Bearer ${configProperties.OpenAiAPIKey}"
        }
    }
}