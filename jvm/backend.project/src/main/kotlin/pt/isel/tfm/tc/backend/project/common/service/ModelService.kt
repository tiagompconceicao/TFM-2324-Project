package pt.isel.tfm.tc.backend.project.common.service

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import pt.isel.tfm.tc.backend.project.ConfigProperties
import pt.isel.tfm.tc.backend.project.common.model.Message
import pt.isel.tfm.tc.backend.project.common.ModelAPI
import pt.isel.tfm.tc.backend.project.common.model.ModelMessage

@Service
@ConfigurationPropertiesScan
class ModelService(private val configProperties: ConfigProperties): IModelService() {

    override fun execPrompt(modelName: String, promptText: String, promptType: String): String? {
        val messages = PromptType.valueOf(promptType).buildMessages()
        messages.add(Message(USER, promptText))

        return client.post()
                .uri(ModelAPI.valueOf(modelName).getUrl())
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer ${configProperties.OpenAiAPIKey}")
                .body(ModelMessage(model = modelName, messages = messages))
                .retrieve()
                .body(String::class.java)
    }
}