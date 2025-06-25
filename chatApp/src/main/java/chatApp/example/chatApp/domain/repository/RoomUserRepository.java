package chatApp.example.chatApp.domain.repository;

import chatApp.example.chatApp.domain.model.Room;
import chatApp.example.chatApp.domain.model.RoomUser;
import chatApp.example.chatApp.domain.model.RoomUserId;
import chatApp.example.chatApp.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomUserRepository extends JpaRepository<RoomUser, RoomUserId> {
    //roomに参加している全ユーザーを取得
    List<RoomUser> findByRoomId(UUID roomId);
    //roomに参加しているかチェック
    boolean existsByUserAndRoom(User user, Room room);
    //roomの参加者を取得
    RoomUser findByUserAndRoom(User user, Room room);

    //最新のメッセージ順にルームを表示するためのクエリ
    @Query("""
    SELECT ru.room FROM RoomUser ru
    WHERE ru.user.id = :userId
    ORDER BY (
        SELECT MAX(m.createdAt)
        FROM Message m
        WHERE m.room = ru.room
    ) DESC
""")
    List<Room> findRoomsByUserIdOrderByLatestMessage(@Param("userId") UUID userId);
}
