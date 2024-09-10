package pt.isel.tfm.tc.backend.project.common.service

import com.google.cloud.Timestamp
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import pt.isel.tfm.tc.backend.project.DockerConfigProperties
import pt.isel.tfm.tc.backend.project.common.model.*
import java.io.*
import java.time.Instant

@Service
class DockerService(configProperties: DockerConfigProperties,database: Firestore) {
    private val client = RestClient.create()
    private val baseUrl = configProperties.url

    @Autowired
    val firestore = database

    /*
     * Build an image with a given tar file
     */

    fun buildImage(dockerfile: File, imageName:String): String? {
        try {
            val request = client.post()
                    .uri("$baseUrl/build?t=${imageName}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Content-type", "application/x-tar")

            return request.body(FileInputStream(dockerfile).readAllBytes())
                    .retrieve()
                    .body(String::class.java)
        }catch (e: Exception){
            throw e
        }
    }

    /*
     * Create a container with a given image name
     */
    fun createContainer(imageName:String,containerPort: Long): DockerBuildResponse? {
        val request = client.post()
                .uri("$baseUrl/containers/create?name=${imageName}")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Content-type", "application/json")


        // Define the container configuration
        val containerConfig = ContainerConfig(
                Image = imageName,
                ExposedPorts = mapOf("80/tcp" to emptyMap()),
                HostConfig = HostConfiguration(
                        PortBindings = mapOf(
                                "80/tcp" to listOf(PortBinding(HostPort = containerPort.toString()))
                        )
                )
        )

        return request.body(containerConfig)
                .retrieve()
                .body(DockerBuildResponse::class.java)
    }

    fun getNextPortAvailable(): Long {
        val snap = firestore.collection("containers")
                .orderBy("port",Query.Direction.DESCENDING)
                .limit(1)
                .get()

        val port = snap.get().documents[0].data["port"] as Long + 1

        val containerDetails = mutableMapOf<String,Any>()
        containerDetails["port"] = port

        firestore.collection("containers").add(containerDetails).get()


        return port

    }

    /*
     * Starts a specific container
     */
    fun startContainer(containerID: String): String? {
        val request = client.post()
                .uri("$baseUrl/containers/$containerID/start")
                .contentLength(0)

        return request
                .retrieve()
                .body(String::class.java)
    }

    /*
     * Stops a specific container
     */
    fun stopContainer(containerID: String): String? {
        val request = client.post()
                .uri("$baseUrl/containers/$containerID/stop")
                .contentLength(0)

        return request
                .retrieve()
                .body(String::class.java)
    }


    fun getUserContainers(chats: MutableList<Chat>): List<DockerContainer> {
        val client = HttpClients.createDefault()
        val builder = URIBuilder("$baseUrl/containers/json")
        val filters = mapOf("name" to chats.map { it.id.lowercase() })
        val jsonFilters = Gson().toJson(filters)

        builder.addParameter("all", "true")
        builder.addParameter("filters", jsonFilters)

        val httpGet = HttpGet(builder.build())

        var returnValue: List<DockerContainer> = emptyList()

        try {
            val response = client.execute(httpGet)
            val jsonResponse = response.entity.content.reader().readText()
            returnValue = Gson().fromJson(jsonResponse, object : TypeToken<List<DockerContainer>>() {}.type)
            response.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            client.close()
        }

        return returnValue

    }

    fun getContainer(containerID: String): Container? {
        val client = HttpClients.createDefault()
        val builder = URIBuilder("$baseUrl/containers/$containerID/json")

        val httpGet = HttpGet(builder.build())

        var returnValue: Container? = null

        try {
            val response = client.execute(httpGet)
            val jsonResponse = response.entity.content.reader().readText()
            returnValue = Gson().fromJson(jsonResponse, object : TypeToken<Container>() {}.type)
            response.close()
        } catch (e: IOException) {
            throw e
        } finally {
            client.close()
        }

        return returnValue
    }
}