package pt.isel.tfm.tc.backend.project.common.service

import pt.isel.tfm.tc.backend.project.common.model.models.Message

enum class PromptType {
    Honeypot {
        override fun buildMessages(): MutableList<Message> {
            val messages = mutableListOf<Message>()
            messages.add(Message(systemRole, "You are a helpful assistant. You have to generate honeypot dockerfiles in order to fight against malicious agents."))
            return messages
        }

        override fun needsRetryMechanism(): Boolean = true
    },
    CTF {
        override fun buildMessages(): MutableList<Message> {
            val messages = mutableListOf<Message>()
            messages.add(Message(systemRole, "You are a helpful assistant. You have to generate CTF challenges."))
            return messages
        }

        override fun needsRetryMechanism(): Boolean = true
    },
    OTHER {
        override fun buildMessages(): MutableList<Message> {
            val messages = mutableListOf<Message>()
            messages.add(Message(systemRole, "You are a helpful assistant."))
            return messages
        }
        override fun needsRetryMechanism(): Boolean = false
    };
    val systemRole = "system"
    abstract fun buildMessages(): MutableList<Message>
    abstract fun needsRetryMechanism(): Boolean
}