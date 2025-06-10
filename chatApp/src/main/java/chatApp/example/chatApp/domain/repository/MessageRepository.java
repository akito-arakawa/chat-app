package chatApp.example.chatApp.domain.repository;

import chatApp.example.chatApp.domain.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    // 最初の20件（最新）
    List<Message> findTop20ByRoomIdOrderByCreatedAtDesc(UUID roomId);

    // より古い20件（古いcreatedAtを基準）
    List<Message> findTop20ByRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(UUID roomId, Timestamp createdAt);

    //個々のチャットを編集
    Optional<Message> findByUserIdAndId(UUID userId, UUID Id);

}
