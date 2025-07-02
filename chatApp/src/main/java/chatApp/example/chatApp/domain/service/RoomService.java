package chatApp.example.chatApp.domain.service;

import chatApp.example.chatApp.domain.dto.RequestRoomDto;
import chatApp.example.chatApp.domain.dto.RoomSummaryDto;
import chatApp.example.chatApp.domain.model.Room;
import chatApp.example.chatApp.domain.model.User;
import chatApp.example.chatApp.domain.repository.RoomRepository;
import chatApp.example.chatApp.domain.repository.RoomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomUserRepository roomUserRepository;

    //RoomCodeを参照し、該当するRoomを返す
    public Room findByRoomCode(String roomCode) {
        return roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("ルームコードが見つかりません: " + roomCode));
    }

    //roomIdを基にroom参照
    public Room findById(UUID roomId) {
        return roomRepository.findById(roomId).orElseThrow(()->new RuntimeException("該当するroomがありません"));
    }

    //Room作成
    @Transactional
    public Room addRoom(RequestRoomDto request, User user) {
        //同じRoomCodeがある場合
        if (roomRepository.existsByRoomCode(request.getRoomCode())) {
            throw new IllegalArgumentException("既に使用されているルームコードです。");
        }
        //オブジェクトを生成
        Room room = new Room();
        room.setRoomCode(request.getRoomCode());
        room.setRoomName(request.getRoomName());
        room.setOwner(user);
        //roomを登録
        return roomRepository.save(room);
    }

    //Room削除
    @Transactional
    public void deleteRoom(Room room) {
        roomRepository.delete(room);
    }

    //参加しているルームのIdとnameを取得
    public List<RoomSummaryDto> getRoomsByUserOrderedByCreatedAt(UUID userId) {
        List<Room> rooms = roomUserRepository.findRoomsByUserIdOrderByRoomCreatedAt(userId);
        return rooms.stream()
                .map(room -> new RoomSummaryDto(room.getId(), room.getRoomName()))
                .toList();
    }
}
