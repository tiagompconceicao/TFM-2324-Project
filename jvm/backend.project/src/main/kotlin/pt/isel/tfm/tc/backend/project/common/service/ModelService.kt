package pt.isel.tfm.tc.backend.project.common.service

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import pt.isel.tfm.tc.backend.project.common.model.models.ModelRequest

@Service
class ModelService() {
    private val client = RestClient.create()

    fun execPrompt(message: ModelRequest): String? {
        val modelAPI = client.post()
                .uri(message.modelAPI.getUrl())
                .contentType(APPLICATION_JSON)

        for (header in message.headers) {
            modelAPI.header(header.first, header.second)
        }

        return modelAPI.body(message.messageBody)
                .retrieve()
                .body(String::class.java)
    }
}