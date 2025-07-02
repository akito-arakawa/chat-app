package chatApp.example.chatApp.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDetailsDto {
    private UUID roomId;
    private String roomName;
    private String roomCode;
    private List<RoomUserDto> participants;
}
