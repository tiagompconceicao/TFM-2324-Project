package pt.isel.tfm.tc.backend.project.common.model

data class Chat(
        var id: String,
        var date: java.util.Date,
        var description: String,
        var owner: String
) {
}