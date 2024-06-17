package web.mvc.dto.meetUpBoard;


import lombok.*;
import web.mvc.entity.meetUpBoard.MeetUpBoardDetailImg;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MeetUpBoardDTO {
    private Long meetUpSeq;
    private Long userSeq;
    private Long interestSeq;
    private String meetUpName;
    private String meetUpDesc;
    private MeetUpBoardDetailImg meetUpBoardDetailImg;
    private List<String> meetUpPeopleList;
    private int meetUpPwd;
    private int checkPwd;
    private int meetUpMaxEntry;
    private  String  meetUpDeadLine;
    private int  meetUpStatus;
}
