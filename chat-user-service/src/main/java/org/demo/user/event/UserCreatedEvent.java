package org.demo.user.event;

public record UserCreatedEvent(String userId, String username, String email, long timestamp) {}
