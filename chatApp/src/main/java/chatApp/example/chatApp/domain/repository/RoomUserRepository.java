package chatApp.example.chatApp.domain.repository;

import chatApp.example.chatApp.domain.model.RoomUser;
import chatApp.example.chatApp.domain.model.RoomUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomUserRepository extends JpaRepository<RoomUser, RoomUserId> {
    //roomに参加している全ユーザーを取得
    List<RoomUser> findByRoomId(UUID roomId);
}

