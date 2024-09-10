package pt.isel.tfm.tc.backend.project.common.authorization


import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import pt.isel.tfm.tc.backend.project.common.data.PromptData
import pt.isel.tfm.tc.backend.project.common.data.UserData


const val USER_ATTRIBUTE_KEY = "user-attribute"
const val BEARER_SCHEME = "Bearer"


/**
 * This is a sample filter that performs request logging.
 */
@Component
class AuthenticationFilter(val userData: UserData): Filter {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {

        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        val authorizationHeader: String = httpRequest.getHeader("Authorization") ?: ""
        val vars = getResourceFromUri(request.requestURI, 2)

        //Whitelist of addresses that can request the client Firebase API Keys
        if ((httpRequest.serverName == "tfm-2324-project.web.app" ||
                httpRequest.serverName == "tfm-2324-project.firebaseapp.com"||
                httpRequest.serverName == "localhost") &&  vars == "ping") {
            chain?.doFilter(request, httpResponse)
            return
        }

        val idToken = userData.isLoggedIn(authorizationHeader)
        if (idToken != null) {
            httpRequest.setAttribute(USER_ATTRIBUTE_KEY, idToken.uid)
            chain?.doFilter(request, httpResponse)
        }
        else {
            val httpResponse = response as HttpServletResponse
            httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
            httpResponse.addHeader(HttpHeaders.WWW_AUTHENTICATE, "$BEARER_SCHEME idToken")
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

