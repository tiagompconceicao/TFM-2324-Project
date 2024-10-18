package pt.isel.tfm.tc.backend.project.common.authorization

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.tfm.tc.backend.project.common.data.PromptData


/**
 * This is an interceptor that verifies if a resource access can be performed by the current user.
 * Checks if the client owns the chat that is trying to access
 * TODO: Add similar flow for the Services
 */
class AccessControlInterceptor(private val promptData: PromptData) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val uid = request.getAttribute(USER_ATTRIBUTE_KEY) as String?
        val id = getResourceFromUri(request.requestURI, 3)
        val path = getResourceFromUri(request.requestURI, 2)

        return when {
            id == "" -> true
            path == "services" -> true
            promptData.checkIfUserOwnsChat(id,uid) -> true
            else -> {
                response.status = HttpServletResponse.SC_FORBIDDEN
                false
            }
        }
    }



    /*
     * Used to get a specific identifier from the request Uri
     */
    private fun getResourceFromUri(uri: String, index: Int): String {
        val splitedUri = uri.split("/")
        if (splitedUri.size < index + 1) return ""
        return splitedUri[index]
    }
}