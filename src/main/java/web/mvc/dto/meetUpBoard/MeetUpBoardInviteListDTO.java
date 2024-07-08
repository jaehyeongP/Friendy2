package web.mvc.dto.meetUpBoard;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MeetUpBoardInviteListDTO {

    private Long userSeq;
    private Long meetUpBoardSeq;

}
