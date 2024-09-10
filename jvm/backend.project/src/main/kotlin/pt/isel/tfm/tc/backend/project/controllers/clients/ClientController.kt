package pt.isel.tfm.tc.backend.project.controllers.clients

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.tfm.tc.backend.project.FirebaseClientConfigProperties
import pt.isel.tfm.tc.backend.project.common.HOME_PATH
import pt.isel.tfm.tc.backend.project.common.authorization.USER_ATTRIBUTE_KEY
import pt.isel.tfm.tc.backend.project.common.model.ClientAPIFirebase


@RestController
@CrossOrigin(origins = ["http://localhost:3000"], methods = [RequestMethod.GET], maxAge = 3600)
@RequestMapping(HOME_PATH)
public class ClientController() {

    /*
       * Endpoint to give to the client the Firebase API credentials
       */
    @GetMapping("/ping")
    fun gethome(): ResponseEntity<String> {
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/json"))
                .body(":)")
    }

}

