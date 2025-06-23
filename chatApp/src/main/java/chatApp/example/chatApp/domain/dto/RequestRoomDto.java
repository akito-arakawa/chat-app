package chatApp.example.chatApp.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestRoomDto {
    @NotBlank(message = "ルームコードは必須です。")
    private String roomCode;
    @NotBlank(message = "ルーム名は必須です。")
    private String roomName;
}
