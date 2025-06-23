package chatApp.example.chatApp.app;

import chatApp.example.chatApp.domain.dto.RequestRoomDto;
import chatApp.example.chatApp.domain.model.Room;
import chatApp.example.chatApp.domain.model.User;
import chatApp.example.chatApp.domain.repository.UserRepository;
import chatApp.example.chatApp.domain.service.RoomService;
import chatApp.example.chatApp.domain.service.RoomUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomUserService roomUserService;

    @PostMapping
    public ResponseEntity<String> addRoom(@Valid @RequestBody RequestRoomDto requestDto) {
        //登録処理
        roomService.addRoom(requestDto);
        return ResponseEntity.ok("ルームを作成しました");
    }

    @PostMapping("/join")
    public ResponseEntity<String> findByRoom(@RequestBody Map<String, String> requestRoomCode, @AuthenticationPrincipal UserDetails userDetails) {
        String roomCode = requestRoomCode.get("roomCode");
        Room room = roomService.findByRoomCode(roomCode);

        //リクエストしたRoomCodeが存在しない場合
        if (room == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ルームが存在しません");
        }

        //ログインユーザーを取得
        String loginId = userDetails.getUsername();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが存在しません: " + loginId));

        //中間テーブルに値を保存
        roomUserService.joinRoom(user, room);

        return ResponseEntity.ok("roomに参加しました");
    }

    @DeleteMapping("/exit/{roomId}")
    public ResponseEntity<String> leaveRoom(@PathVariable UUID roomId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        //ログインユーザーを取得
        String loginId = userDetails.getUsername();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが存在しません: " + loginId));

        //ルームの取得
        Room room = roomService.findById(roomId);
        if (room == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ルームが存在しません");
        }
        //ルームから退出
        roomUserService.leaveRoom(user, room);

        return ResponseEntity.ok("ルームから退出しました");
    }

}
