package com.example.chatting.controller;

import com.example.chatting.dto.ChatDTO;
import com.example.chatting.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RequiredArgsConstructor
@Controller
public class TestController {

    private final SimpMessageSendingOperations template;

    @Autowired ChatRepository repository;
    /**
     * index
     */
    @GetMapping("/testChating")
    public ModelAndView home(ModelAndView mav) {
        mav.setViewName("index");
        return mav;
    }

    // 채팅방 입장
    @MessageMapping("/chat/enterUserTest")
    public void enterUser(@Payload ChatDTO chat, SimpMessageHeaderAccessor headerAccessor) {
        log.info(chat.toString());

        // 채팅방에 유저 추가 및 UserUUID 반환
        String userUUID = repository.addUser(chat.getRoomId(), chat.getSender());

        // 반환 결과를 socket session 에 userUUID 로 저장
        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
        headerAccessor.getSessionAttributes().put("roomId", chat.getRoomId());

        chat.setMessage(chat.getSender() + " 님 입장!!");

        //redis 저장
        repository.saveMsg(chat);

        //구독한 채널에 반환
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }

    // 메세지 전송
    @MessageMapping("/chat/sendMessageTest")
    public void sendMessage(@Payload ChatDTO chat) {
        log.info("CHAT {}", chat);
        String msg = chat.getSender() + "님 : " + chat.getMessage();
        chat.setMessage(msg);

        //redis 저장
        repository.saveMsg(chat);

        //구독한 채널에 반환
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }
}
