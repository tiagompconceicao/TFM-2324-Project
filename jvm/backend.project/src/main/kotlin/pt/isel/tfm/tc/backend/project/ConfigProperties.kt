package pt.isel.tfm.tc.backend.project

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("models")
data class ConfigProperties (
        val OpenAiAPIKey: String,
        val ClaudeAPIKey : String
)

@ConfigurationProperties("docker")
data class DockerConfigProperties (
        val url: String
)

@ConfigurationProperties("firebase")
data class FirebaseConfigProperties (
        val credentials : String,
        val url : String,
)

@ConfigurationProperties("client")
data class FirebaseClientConfigProperties (
        val apiKey : String,
        val authDomain : String,
        val projectId : String,
        val storageBucket: String,
        val messagingSenderId : String,
        val appId : String,
        val measurementId : String
)