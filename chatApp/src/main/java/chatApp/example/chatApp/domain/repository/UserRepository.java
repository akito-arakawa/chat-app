package chatApp.example.chatApp.domain.repository;

import chatApp.example.chatApp.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    //loginIdを基にUserを取得
    Optional<User> findByLoginId(String loginId);
    //loginIdが正しいかチェック
    boolean existsByLoginId(String loginId);
}
