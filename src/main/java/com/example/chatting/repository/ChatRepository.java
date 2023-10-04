package com.example.chatting.repository;

import com.example.chatting.dto.ChatRoom;
import com.example.chatting.util.RoomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ChatRepository {
    private static final String CHAT_ROOM_KEY = "chat:" + RoomUtil.randomRoomId();
    private final RedisTemplate<String, Object> redisTemplate;

    // 전체 채팅방 조회
    public List<ChatRoom> findAllRoom(){
        List<Object> chatRooms = redisTemplate.opsForList().range(CHAT_ROOM_KEY, 0, -1);

        // List<Object> -> List<ChatRoom>
        List<ChatRoom> chatRoomList = chatRooms.stream()
                .map(object -> (ChatRoom) object)
                .collect(Collectors.toList());

        return chatRoomList;
    }

    // roomID 기준으로 채팅방 찾기
    public ChatRoom findRoomById(String roomId){
        return (ChatRoom) redisTemplate.opsForHash().get(CHAT_ROOM_KEY, roomId);
    }

    // roomName 로 채팅방 만들기
    public ChatRoom createChatRoom(String roomName){
        ChatRoom chatRoom = new ChatRoom().create(roomName); // 채팅룸 이름으로 채팅 룸 생성 후

        // Redis에 채팅방 정보를 저장하는 코드 추가
        redisTemplate.opsForHash().put(CHAT_ROOM_KEY, chatRoom.getRoomId(), chatRoom);

        return chatRoom;
    }

    // 채팅방 인원+1
    public void plusUserCnt(String roomId){
        ChatRoom room = findRoomById(roomId);
        room.setUserCount(room.getUserCount()+1);
    }

    // 채팅방 인원-1
    public void minusUserCnt(String roomId){
        ChatRoom room = findRoomById(roomId);
        room.setUserCount(room.getUserCount()-1);
    }

    // 채팅방 유저 리스트에 유저 추가
    public String addUser(String roomId, String userName){
        ChatRoom room = findRoomById(roomId);
        String userUUID = UUID.randomUUID().toString();

        // 아이디 중복 확인 후 userList 에 추가
        room.getUserlist().put(userUUID, userName);

        // Redis에 채팅방 정보를 업데이트
        redisTemplate.opsForHash().put(CHAT_ROOM_KEY, roomId, room);

        return userUUID;
    }

    // 채팅방 유저 이름 중복 확인
    public String isDuplicateName(String roomId, String username){
        ChatRoom room = findRoomById(roomId);
        String tmp = username;

        // 만약 userName 이 중복이라면 랜덤한 숫자를 붙임
        // 이때 랜덤한 숫자를 붙였을 때 getUserlist 안에 있는 닉네임이라면 다시 랜덤한 숫자 붙이기!
        while(room.getUserlist().containsValue(tmp)){
            int ranNum = (int) (Math.random()*100)+1;

            tmp = username+ranNum;
        }

        return tmp;
    }

    // 채팅방 유저 리스트 삭제
    public void delUser(String roomId, String userUUID){
        ChatRoom room = findRoomById(roomId);
        room.getUserlist().remove(userUUID);
    }

    // 채팅방 userName 조회
    public String getUserName(String roomId, String userUUID){
        ChatRoom room = findRoomById(roomId);
        return room.getUserlist().get(userUUID);
    }

    // 채팅방 전체 userlist 조회
    public ArrayList<String> getUserList(String roomId){
        ArrayList<String> list = new ArrayList<>();

        ChatRoom room = findRoomById(roomId);

        // hashmap 을 for 문을 돌린 후
        // value 값만 뽑아내서 list 에 저장 후 reutrn
        room.getUserlist().forEach((key, value) -> list.add(value));
        return list;
    }
}
