package chatApp.example.chatApp.domain.dto;

import chatApp.example.chatApp.domain.model.RoomUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomUserDto {
    private UUID userId;
    private String username;
    private String iconUrl;

    //ユーザ一覧をまとめた静的メソッド
    public static RoomUserDto fromEntity(RoomUser entity) {
        return new RoomUserDto(
                entity.getUser().getId(),
                entity.getUser().getUsername(),
                entity.getUser().getIconUrl()
        );
    }

}
