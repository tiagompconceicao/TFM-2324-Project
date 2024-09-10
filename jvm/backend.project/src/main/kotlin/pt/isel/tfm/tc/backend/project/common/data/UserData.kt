package pt.isel.tfm.tc.backend.project.common.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserData(firebase: FirebaseAuth) {

    @Autowired
    val auth = firebase

    fun isLoggedIn(authHeader: String): FirebaseToken?{
        val idToken = authHeader.substringAfter("Bearer ")
        return try {
            auth.verifyIdToken(idToken)
        } catch (exception: Exception){
            null
        }
    }
}