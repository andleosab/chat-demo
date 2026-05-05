package org.demo.chat;

public record UserCreatedMessage(MessageType type, String userId, String username, String email, long timestamp) {}
