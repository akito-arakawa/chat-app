package chatApp.example.chatApp.domain.service;

import chatApp.example.chatApp.domain.dto.RegisterDto;
import chatApp.example.chatApp.domain.model.Role;
import chatApp.example.chatApp.domain.model.User;
import chatApp.example.chatApp.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(RegisterDto register) {
        //引数で受け取ったデータをUserテーブルに登録
        try {
            User user = new User();
            user.setLoginId(register.getLoginId());
            String password = passwordEncoder.encode(register.getPassword());
            user.setPassword(password);
            user.setUsername(register.getUsername());
            user.setRole(Role.ROLE_USER);
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("ユーザ登録に失敗しました", e);
        }
    }

    public boolean existsByLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }
}
