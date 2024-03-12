package pt.isel.tfm.tc.backend.project.common.data

import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import org.springframework.beans.factory.annotation.Autowired

abstract class Data() {


    val db: Firestore = FirestoreClient.getFirestore()
}