package chatApp.example.chatApp.domain.repository;

import chatApp.example.chatApp.domain.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    //roomCodeからroom情報を取得
    Optional<Room> findByRoomCode(String roomCode);
    //roomCodeが存在するかチェック
    boolean existsByRoomCode(String roomCode);
}
