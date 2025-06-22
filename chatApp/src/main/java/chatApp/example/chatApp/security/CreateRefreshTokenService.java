package chatApp.example.chatApp.security;

import chatApp.example.chatApp.domain.model.RefreshToken;
import chatApp.example.chatApp.domain.model.User;
import chatApp.example.chatApp.domain.repository.RefreshTokenRepository;
import chatApp.example.chatApp.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class CreateRefreshTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveRefreshToken(String refreshTokenStr, UserDetails authenticatedUser) {
        Optional<User> user = userRepository.findByLoginId(authenticatedUser.getUsername());
        RefreshToken refreshToken = new RefreshToken();
        if (user.isPresent()) {
            refreshToken.setUser(user.orElseThrow());
            refreshToken.setToken(refreshTokenStr);
            refreshToken.setExpiryDate(Timestamp.from(Instant.now().plus(7, ChronoUnit.DAYS)));
        } else {
            throw new RuntimeException();
        }

        refreshTokenRepository.save(refreshToken);
    }
}
