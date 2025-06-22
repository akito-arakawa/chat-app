package chatApp.example.chatApp.app;

import chatApp.example.chatApp.domain.dto.LoginDto;
import chatApp.example.chatApp.domain.dto.RegisterDto;
import chatApp.example.chatApp.domain.model.RefreshToken;
import chatApp.example.chatApp.domain.model.User;
import chatApp.example.chatApp.domain.repository.RefreshTokenRepository;
import chatApp.example.chatApp.domain.service.UserService;
import chatApp.example.chatApp.security.CreateRefreshTokenService;
import chatApp.example.chatApp.security.CustomUserDetailsService;
import chatApp.example.chatApp.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class authController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CreateRefreshTokenService createRefreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

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

    //ログイン認証
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getLoginId(),
                            loginDto.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //認証済みのUSER情報を取得
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            //アクセストークンを生成
            String accessToken = jwtService.generateAccessToken(userDetails);
            //リフレッシュトークンを生成
            String refreshToken = jwtService.generateRefreshToken(userDetails);
            //リフレッシュトークン保存処理
            createRefreshTokenService.saveRefreshToken(refreshToken, userDetails);

            return ResponseEntity.ok(Map.of("accessToken", accessToken,
                    "refreshToken", refreshToken));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ログイン失敗：ユーザーが見つかりません");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ログイン失敗：IDまたはパスワードが間違っています");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshTokenStr = request.get("refreshToken");

        // トークン文字列から該当するリフレッシュトークンエンティティを取得
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByToken(refreshTokenStr);
        //値が空の場合
        if (optionalRefreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("無効なリフレッシュトークン");
        }
        //値を取得
        RefreshToken refreshToken = optionalRefreshToken.get();

        //有効期限をチェック
        if (refreshToken.getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) {
            //期限切れトークンを削除
            refreshTokenRepository.delete(refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("リフレッシュトークンが期限切れ");
        }

        //アクセストークンを再発行
        User user = refreshToken.getUser();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getLoginId());

        //新しいトークンを作成
        String newAccessToken = jwtService.generateAccessToken(userDetails);

        //新しいアクセストークンを返す
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
}
