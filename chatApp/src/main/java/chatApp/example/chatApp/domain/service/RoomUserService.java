package chatApp.example.chatApp.domain.service;

import chatApp.example.chatApp.domain.model.Room;
import chatApp.example.chatApp.domain.model.RoomUser;
import chatApp.example.chatApp.domain.model.RoomUserId;
import chatApp.example.chatApp.domain.model.User;
import chatApp.example.chatApp.domain.repository.RoomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomUserService {
    @Autowired
    private RoomUserRepository roomUserRepository;

    @Transactional
    public void joinRoom(User user, Room room) {
        if (roomUserRepository.existsByUserAndRoom(user, room)) {
            return; // すでに参加済みなら何もしない
        }
        System.out.println(user);
        System.out.println(room);
        //オブジェクトを作成
        RoomUser roomUser = new RoomUser();
        roomUser.setId(new RoomUserId(user.getId(), room.getId()));
        roomUser.setUser(user);
        roomUser.setRoom(room);
        //登録
        roomUserRepository.save(roomUser);
    }
}
