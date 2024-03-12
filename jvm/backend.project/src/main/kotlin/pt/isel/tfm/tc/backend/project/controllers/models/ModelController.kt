package pt.isel.tfm.tc.backend.project.controllers.models;

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.tfm.tc.backend.project.common.MODELS_PATH
import pt.isel.tfm.tc.backend.project.common.service.ModelService
import pt.isel.tfm.tc.backend.project.common.data.PromptData


@RestController
@RequestMapping(MODELS_PATH)
public class ModelController(private val modelService: ModelService, private val promptData: PromptData ) {
    /* TODO
     * Develop IModelData and ModelData (import code to communicate with Firestore and perform http requests)
     * Ask model to execute a prompt: POST
     * Save prompt in Database: PromptsData -> POST -> Firestore
     * Get all conversations info: GET
     * Get all prompts from a conversation: GET
     */

    //Call modelData to access the model API and execute the prompt
    //Save the response
    //Save prompt in Firestore
    //Save the response int Firestore
    //return the response to the client

    /*
    @ExceptionHandler()
    fun handleException(): ResponseEntity<String> {
        return ResponseEntity
                .badRequest()
                .body("Algo deu errado")
    }
    */


    /*
     * Test for the communication with Firebase
     */
    @GetMapping
    fun getHome(): ResponseEntity<String> {
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/json"))
                .body("Isto é um teste")
                //promptData.getTests()
    }


    /*
     * Method that handle the request of the user to execute a prompt of a specific model
     */
    @PostMapping("/{modelName}/prompts")
    fun executePrompt(
            @RequestBody prompt: PromptInputModel,
            @PathVariable modelName: String
    ): ResponseEntity<String> {
        val response = modelService.execPrompt(modelName,prompt.text,prompt.type)
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/json"))
                .body(response)
    }
}


