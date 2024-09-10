package pt.isel.tfm.tc.backend.project.common.model

data class Prompt(
        var date: java.util.Date,
        var text: String,
        var author: Boolean
) { }