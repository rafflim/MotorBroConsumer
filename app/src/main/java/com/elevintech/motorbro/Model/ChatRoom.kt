package com.elevintech.motorbro.Model

class ChatRoom (
        var lastMessage: ChatMessage = ChatMessage(),
        var participants: Map<String, String> = mapOf()
)