package pt.isel.tfm.tc.backend.project.common.data

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import pt.isel.tfm.tc.backend.project.common.model.Message
import pt.isel.tfm.tc.backend.project.common.model.ModelMessage

@Service
class ModelData {
    final val OPENAIURL = "https://api.openai.com/v1/chat/completions"
    val client = RestClient.create()

    fun execPrompt(modelName: String, promptText: String): String? {
        var messages = mutableListOf<Message>()
        messages.add(Message("system", "You are a helpful assistant. You have to generate honeypot dockerfiles in order to fight against malicious agents."))
        messages.add(Message("user", promptText))
        return client.post()
                .uri(OPENAIURL)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer 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")
                .body(ModelMessage(model = modelName, messages = messages))
                .retrieve()
                .body(String::class.java)
    }
}