package chatApp.example.chatApp.app;

import chatApp.example.chatApp.domain.dto.RegisterDto;
import chatApp.example.chatApp.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class authController {

    @Autowired
    private UserService userService;

    //新規登録
    @PostMapping
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        //同じloginIdがないかチェック
        if (userService.existsByLoginId(registerDto.getLoginId())) {
            return ResponseEntity.badRequest().body("既に存在するloginIdです");
        }
        //ユーザ登録
        userService.registerUser(registerDto);

        return ResponseEntity.ok("登録が完了しました");
    }

}
