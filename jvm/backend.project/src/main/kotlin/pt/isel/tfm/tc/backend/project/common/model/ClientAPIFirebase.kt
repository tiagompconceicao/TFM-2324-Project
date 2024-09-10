package pt.isel.tfm.tc.backend.project.common.model

data class ClientAPIFirebase(
        val apiKey : String,
        val authDomain : String,
        val projectId : String,
        val storageBucket: String,
        val messagingSenderId : String,
        val appId : String,
        val measurementId : String
)
{}