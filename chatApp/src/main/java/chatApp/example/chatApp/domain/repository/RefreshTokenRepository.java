package chatApp.example.chatApp.domain.repository;

import chatApp.example.chatApp.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    //トークンを取得
    Optional<RefreshToken> findByToken(String token);
    //トークン削除
    void deleteByToken(String token);
}
