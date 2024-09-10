package pt.isel.tfm.tc.backend.project.common.data

import com.google.cloud.Timestamp
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pt.isel.tfm.tc.backend.project.common.model.Chat
import pt.isel.tfm.tc.backend.project.common.model.Prompt

@Service
class PromptData(database: Firestore): Data() {

    @Autowired
    val firestore = database

    //Method that returns all the messages of a specific chat
    //Contains authentication verifications
    fun getChatMessages(chatId: String): MutableList<Prompt> {

        val messages = mutableListOf<Prompt>()
        val chat = firestore.collection("chats").document(chatId)
        val query = chat.collection("messages").orderBy("date")

        //Messages reading and parsing
        for (message in query.get().get().documents){
            if (message.exists()){
                messages.add(Prompt(
                        date = (message.data["date"] as Timestamp).toDate(),
                        text = message.data["text"] as String,
                        author =  message.data["author"] as Boolean))
            }
        }
        return messages
    }

    fun getChats(username: String): MutableList<Chat> {
        val chats = mutableListOf<Chat>()

        val snap = firestore.collection("chats")
                .whereEqualTo("owner", username)
                .get()

        for (document in snap.get().documents){
            chats.add(Chat(
                    id = document.id,
                    date = (document.data["date"] as Timestamp).toDate(),
                    description = document.data["description"] as String,
                    owner =  document.data["owner"] as String))
        }

        // Sort the list by date
        chats.sortByDescending { it.date }

        return chats
    }

    //Checks if the user is owner of this chat
    fun checkIfUserOwnsChat(chatId: String, username: String?): Boolean{
        if (username == null) return false
        val chat = firestore.collection("chats").document(chatId)
        return chat.get().get().data!!["owner"] == username
    }

    //Create a chat and add the first prompt to the database
    fun createChat(text: String, author:Boolean,username:String): String {
        val prompt = mutableMapOf<String,Any>()
        val currentDate = Timestamp.now()
        prompt["text"] = text
        prompt["author"] = author
        prompt["date"] = currentDate

        val chatDetails = mutableMapOf<String,Any>()
        chatDetails["owner"] = username
        chatDetails["description"] = text
        chatDetails["date"] = currentDate

        val document = firestore.collection("chats").add(chatDetails).get()
        document.collection("messages").add(prompt).get()
        return document.id

    }

    //Add a prompt to the database
    fun addPrompt(text: String, author:Boolean,chatId: String,username:String): String {
        val prompt = mutableMapOf<String,Any>()
        prompt["text"] = text
        prompt["author"] = author
        prompt["date"] = Timestamp.now()

        firestore.collection("chats").document(chatId).collection("messages").add(prompt).get()
        return chatId
    }
}