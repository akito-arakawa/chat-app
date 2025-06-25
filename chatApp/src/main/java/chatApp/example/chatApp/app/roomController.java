package chatApp.example.chatApp.app;

import chatApp.example.chatApp.domain.dto.RequestRoomDto;
import chatApp.example.chatApp.domain.dto.RoomDetailsDto;
import chatApp.example.chatApp.domain.dto.RoomUserDto;
import chatApp.example.chatApp.domain.model.Room;
import chatApp.example.chatApp.domain.model.RoomUser;
import chatApp.example.chatApp.domain.model.User;
import chatApp.example.chatApp.domain.repository.RoomUserRepository;
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

import java.util.List;
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

    @Autowired
    private RoomUserRepository roomUserRepository;

    @GetMapping("/my/last-active")
    public ResponseEntity<UUID> getLastActiveRoomId(@AuthenticationPrincipal UserDetails userDetails) {
        //ログインユーザーを取得
        String loginId = userDetails.getUsername();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが存在しません: " + loginId));
        List<Room> rooms = roomUserRepository.findRoomsByUserIdOrderByLatestMessage(user.getId());
        //ルームが空の場合
        if (rooms.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        //メッセージが最新のルームを取得
        Room latest = rooms.get(0);
        return ResponseEntity.ok(latest.getId());   //IDのみを返す
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDetailsDto> getRoom(@PathVariable UUID roomId) {
        //Room情報を取得
        Room room = roomService.findById(roomId);
        String roomName = room.getRoomName();
        String roomCode = room.getRoomCode();
        //Roomに参加しているUserを取得
        List<RoomUser> roomUser = roomUserService.findByRoomId(roomId);
        //room詳細情報のオブジェクト生成
        RoomDetailsDto roomDetails = new RoomDetailsDto(
                roomId,
                roomName,
                roomCode,
                roomUser.stream().map(RoomUserDto::fromEntity).toList()
        );

        return ResponseEntity.ok(roomDetails);
    }

    @PostMapping
    public ResponseEntity<String> addRoom(@Valid @RequestBody RequestRoomDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {

        //ログインユーザーを取得
        String loginId = userDetails.getUsername();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが存在しません: " + loginId));

        //登録処理
        Room room = roomService.addRoom(requestDto, user);
        //中間テーブルに値を登録
        roomUserService.joinRoom(user, room);

        return ResponseEntity.ok("ルームを作成しました");
    }

    @PostMapping("/join")
    public ResponseEntity<String> findByRoom(@RequestBody Map<String, String> requestRoomCode, @AuthenticationPrincipal UserDetails userDetails) {
        //ルームコードからルーム情報を取得
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

    @DeleteMapping("/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable UUID roomId,
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
        //ルームの管理者かチェック
        if (!room.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("このルームを削除する権限がありません");
        }
        roomService.deleteRoom(room);

        return ResponseEntity.ok("ルームを削除しました。");
    }

}
