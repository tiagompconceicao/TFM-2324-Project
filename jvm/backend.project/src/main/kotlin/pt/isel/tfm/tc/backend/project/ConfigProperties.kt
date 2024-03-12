package pt.isel.tfm.tc.backend.project

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("models")
data class ConfigProperties (
        val OpenAiAPIKey: String
)