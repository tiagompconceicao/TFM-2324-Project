package pt.isel.tfm.tc.backend.project.common.authorization

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class AACORSFilter: Filter {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        httpResponse.addHeader("Access-Control-Allow-Origin","http://localhost:3000")
        httpResponse.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS")
        httpResponse.addHeader("Access-Control-Allow-Headers","Content-Type, Authorization, Access-Control-Allow-Headers, X-Requested-With,observe")
        httpResponse.addHeader("Access-Control-Allow-Max-Age","3600")
        httpResponse.addHeader("Access-Control-Allow-Allow-Credentials","true")

        if(httpRequest.method == "OPTIONS"){
            httpResponse.status = HttpServletResponse.SC_OK
        } else {
            chain?.doFilter(httpRequest,httpResponse)
        }
    }
}