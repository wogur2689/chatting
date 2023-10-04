package com.example.chatting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

//해당 부분은 필요시 사용
@Service
@RequiredArgsConstructor
public class ChatService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String chatRoomKey = "chat:room";

    /**
     * redis에 메세지 보내기
     */
    public void sendMessage(String message) {
        redisTemplate.opsForList().leftPush(chatRoomKey, message);
    }

    /**
     * redis에서 해당 채팅방의 모든 메세지 가져오기
     */
    public List<Object> getMessages() {
        return redisTemplate.opsForList().range(chatRoomKey, 0, -1);
    }
}
