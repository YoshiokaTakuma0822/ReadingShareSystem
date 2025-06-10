package com.readingshare.chat.service;

import org.springframework.stereotype.Service;

import com.readingshare.chat.domain.model.Message;
import com.readingshare.chat.domain.repository.MessageRepository;
import com.readingshare.room.domain.model.Member;
import com.readingshare.room.domain.model.Room;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message createJoinMessage(Member member, Room room) {
        String content = member.getName() + " joined";
        Message message = Message.create(content, member, room, Message.MessageType.JOIN);
        return messageRepository.save(message);
    }

    public Message createLeaveMessage(Member member, Room room) {
        String content = member.getName() + " left";
        Message message = Message.create(content, member, room, Message.MessageType.LEAVE);
        return messageRepository.save(message);
    }

    public Message createChatMessage(String content, Member member, Room room) {
        Message message = Message.create(content, member, room, Message.MessageType.CHAT);
        return messageRepository.save(message);
    }
}
