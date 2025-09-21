package krashi.server.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResultDto {
    private boolean hasPromotions;
    private int totalPromotedUsers;
    private int totalSeatsPromoted;
    private List<PromotedUserDto> promotedUsers;
    private String message;
}