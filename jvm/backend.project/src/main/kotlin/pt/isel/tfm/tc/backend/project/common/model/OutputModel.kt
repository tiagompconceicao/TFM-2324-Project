package pt.isel.tfm.tc.backend.project.common.model

import pt.isel.tfm.tc.backend.project.common.model.models.ModelContent
import java.time.LocalDateTime

//Class that represents the model of the output messages to a Prompt request from the frontend
data class OutputModel(
        var id: String? = "",
        var date:LocalDateTime? = LocalDateTime.now(),
        var contents: MutableList<ModelContent>
) {}