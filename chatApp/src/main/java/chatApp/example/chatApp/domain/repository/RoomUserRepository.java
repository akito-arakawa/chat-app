package chatApp.example.chatApp.domain.repository;

import chatApp.example.chatApp.domain.model.Room;
import chatApp.example.chatApp.domain.model.RoomUser;
import chatApp.example.chatApp.domain.model.RoomUserId;
import chatApp.example.chatApp.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomUserRepository extends JpaRepository<RoomUser, RoomUserId> {
    //roomに参加している全ユーザーを取得
    List<RoomUser> findByRoomId(UUID roomId);
    //roomに参加しているかチェック
    boolean existsByUserAndRoom(User user, Room room);
}
