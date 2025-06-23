package chatApp.example.chatApp.domain.service;

import chatApp.example.chatApp.domain.model.Room;
import chatApp.example.chatApp.domain.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    //RoomCodeを参照し、該当するRoomを返す
    public Room findByRoomCode(String roomCode) {
        return roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("ルームコードが見つかりません: " + roomCode));
    }

}
