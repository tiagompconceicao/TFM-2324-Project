package pt.isel.tfm.tc.backend.project.common.service

import pt.isel.tfm.tc.backend.project.common.model.Message

enum class PromptType {
    HONEYPOT {
        override fun buildMessages(): MutableList<Message> {
            val messages = mutableListOf<Message>()
            messages.add(Message(systemRole, "You are a helpful assistant. You have to generate honeypot dockerfiles in order to fight against malicious agents."))
            return messages
        }
    },
    CTF {
        override fun buildMessages(): MutableList<Message> {
            val messages = mutableListOf<Message>()
            messages.add(Message(systemRole, "You are a helpful assistant. You have to generate CTF challenges."))
            return messages
        }
    },
    OTHER {
        override fun buildMessages(): MutableList<Message> {
            val messages = mutableListOf<Message>()
            messages.add(Message(systemRole, "You are a helpful assistant."))
            return messages
        }
    };
    val systemRole = "system"
    abstract fun buildMessages(): MutableList<Message>
}