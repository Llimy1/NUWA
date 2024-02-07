package org.project.nuwabackend.dto.message.request;

public record DirectMessageRequestDto(String roomId, String sender, String receiver, String content) {
}
