package pt.isel.tfm.tc.backend.project.controllers.models;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isel.tfm.tc.backend.project.common.MODELS_PATH
import pt.isel.tfm.tc.backend.project.common.data.ModelData;

@RestController
@RequestMapping(MODELS_PATH)
public class ModelController(private val data:ModelData) {
    /* TODO
     * Develop IModelData and ModelData (import code to communicate with Firestore and perform http requests)
     * Ask model to execute a prompt: POST
     * Save prompt in Database: POST
     * Get all conversations info: GET
     * Get all prompts from a conversation: GET
     */
}


