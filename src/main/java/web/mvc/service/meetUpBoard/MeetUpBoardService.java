package web.mvc.service.meetUpBoard;


import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import web.mvc.dto.meetUpBoard.MeetUpBoardDTO;
import web.mvc.dto.meetUpBoard.MeetUpDeleteDTO;
import web.mvc.dto.meetUpBoard.MeetUpUpdateDTO;
import web.mvc.entity.meetUpBoard.MeetUpBoard;


import java.util.Date;
import java.util.List;


public interface MeetUpBoardService {


    String createParty(MeetUpBoardDTO meetUpBoardDTO , List<MultipartFile> file) throws  Exception;

    String updateBoard(MeetUpUpdateDTO meetUpUpdateDTO , List<MultipartFile> file) throws  Exception;

    String deleteBoard( String meetUpSeq, String meetUpPwd);

    MeetUpBoard  findMeetUpByMeetUpName(String meetUpName);

    MeetUpBoard findMeetUpPeopleList(String roomId);
    Resource getDetailImg(String imgName);

   List<MeetUpBoard> findMeetUpByInterest(String interest);

    MeetUpBoard findMeetUpByBoardSeq(Long meetUpSeq);

    List<MeetUpBoard> findByMeetUpName(String meetUpName);
    List<MeetUpBoard> selectAll();

    List<Date> findByPartySeq();


    void checkDeadLine();

}
