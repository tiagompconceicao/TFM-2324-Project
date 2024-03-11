package pt.isel.tfm.tc.backend.project.common.data

import com.google.firebase.cloud.FirestoreClient
import org.springframework.stereotype.Service
import pt.isel.tfm.tc.backend.project.common.model.Prompt
import java.util.*

@Service
class PromptData {
    fun getTests(): MutableList<Prompt> {
        val db = FirestoreClient.getFirestore()
        var prompts = mutableListOf<Prompt>()
        for (document in db.collection("test").listDocuments()){
            var future = document.get()
            var prompt = future.get()
            if (prompt.exists()){


                prompts.add(Prompt(date = prompt.data!!.get("date") as Date, text = prompt.data!!.get("text") as String))
            }
        }
        return prompts
    }
}