package pt.isel.tfm.tc.backend.project.controllers.services

import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.tfm.tc.backend.project.common.SERVICES_PATH
import pt.isel.tfm.tc.backend.project.common.authorization.USER_ATTRIBUTE_KEY
import pt.isel.tfm.tc.backend.project.common.data.PromptData
import pt.isel.tfm.tc.backend.project.common.model.Container
import pt.isel.tfm.tc.backend.project.common.model.DockerBuildResponse
import pt.isel.tfm.tc.backend.project.common.model.DockerContainer
import pt.isel.tfm.tc.backend.project.common.service.DockerService
import java.io.*
import java.time.Instant
import java.util.logging.Logger
import java.util.zip.*


@ConfigurationPropertiesScan
@RestController
@RequestMapping(SERVICES_PATH)
public class ServiceController(private val promptData: PromptData, private val dockerService: DockerService) {

    @ExceptionHandler(Exception::class)
    fun handleException(ex:Exception): ResponseEntity<String> {
        return ResponseEntity
                .status(500)
                .body(ex.message)
    }

    /**
     * Obtain all containers created by the user
     */

    @GetMapping
    fun getContainers(
            request: HttpServletRequest
    ): ResponseEntity<
            List<DockerContainer>> {

        val uid = request.getAttribute(USER_ATTRIBUTE_KEY) as String
        val chats = promptData.getChats(uid)

        val containers = dockerService.getUserContainers(chats)
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/json"))
                .body(containers)
    }


    /**
     * Obtain a specific container
     */
    @GetMapping("/{containerId}")
    fun getSpecificContainer(
            @PathVariable containerId: String
    ): ResponseEntity<Container> {

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/json"))
                .body(dockerService.getContainer(containerId))
    }

    /**
     * Starts a specific container
     */
    @PostMapping("/{containerId}/start")
    fun startContainer(
            @PathVariable containerId: String,
            request: HttpServletRequest
    ): ResponseEntity<String> {


        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/json"))
                .body(dockerService.startContainer(containerId))
    }

    /**
     * Stop a specific container
     */
    @PostMapping("/{containerId}/stop")
    fun stopContainer(
            @PathVariable containerId: String,
            request: HttpServletRequest
    ): ResponseEntity<String> {


        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/json"))
                .body(dockerService.stopContainer(containerId))
    }

    /**
     * Method that handle the request of the user to build and create a container from a given dockerfile
     * It is used to attend the creation of a service
     * TODO: Check already created containers/images
     */

    @PostMapping("/{chatId}")
    fun createImageAndContainer(
            @RequestBody body: ByteArray,
            @PathVariable chatId: String,
            request: HttpServletRequest
    ): ResponseEntity<DockerBuildResponse> {
        val dockerfileTar = createTarFile(body)

        val imageName = chatId.lowercase()+dockerfileTar.hashCode()

        val imageReturn = dockerService.buildImage(dockerfileTar,imageName)

        val returnVal = dockerService.createContainer(imageName,dockerService.getNextPortAvailable())

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/json"))
                .body(returnVal)
    }

    /**
     * Auxiliary method to convert dockerfile into a Tar file in order to send it to the Docker Daemon
     */
    fun createTarFile(dockerfileContent: ByteArray): File {
        // Create a temporary directory
        val tempDir = createTempDir()

        // Write Dockerfile content to a file
        val dockerfile = File(tempDir, "Dockerfile")
        dockerfile.writeBytes(dockerfileContent)

        // Create a tar file
        val tarFile = File("dockerfile.tar.gz")
        FileOutputStream(tarFile).use { fos ->
            GZIPOutputStream(fos).use { gos ->
                TarArchiveOutputStream(gos).use { tarOs ->
                    addFileToTar(tarOs, dockerfile, "")
                }
            }
        }

        // Delete temporary directory
        tempDir.deleteRecursively()

        return tarFile
    }

    /**
     * Auxiliary method to add files in a Tar file.
     */
    private fun addFileToTar(tarOs: TarArchiveOutputStream, file: File, parentDir: String) {
        val entryName = parentDir + file.name
        val tarEntry = TarArchiveEntry(file, entryName)
        tarOs.putArchiveEntry(tarEntry)
        if (file.isFile) {
            FileInputStream(file).use { fis ->
                BufferedInputStream(fis).use { bis ->
                    bis.copyTo(tarOs)
                }
            }
            tarOs.closeArchiveEntry()
        } else if (file.isDirectory) {
            tarOs.closeArchiveEntry()
            file.listFiles()?.forEach {
                addFileToTar(tarOs, it, entryName + "/")
            }
        }
    }

}
