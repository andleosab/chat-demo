package org.demo.chat;

public record UserCreatedEvent(String userId, String username, String email, long timestamp) {}
