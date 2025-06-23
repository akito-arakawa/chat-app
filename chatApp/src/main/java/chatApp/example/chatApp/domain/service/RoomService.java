package chatApp.example.chatApp.domain.service;

import chatApp.example.chatApp.domain.dto.RequestRoomDto;
import chatApp.example.chatApp.domain.model.Room;
import chatApp.example.chatApp.domain.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    //RoomCodeを参照し、該当するRoomを返す
    public Room findByRoomCode(String roomCode) {
        return roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("ルームコードが見つかりません: " + roomCode));
    }

    //Room作成
    @Transactional
    public void addRoom(RequestRoomDto request) {
        //同じRoomCodeがある場合
        if (roomRepository.existsByRoomCode(request.getRoomCode())) {
            throw new IllegalArgumentException("既に使用されているルームコードです。");
        }
        //オブジェクトを作成
        Room room = new Room();
        room.setRoomCode(request.getRoomCode());
        room.setRoomName(request.getRoomName());
        //roomを登録
        roomRepository.save(room);
    }
}
