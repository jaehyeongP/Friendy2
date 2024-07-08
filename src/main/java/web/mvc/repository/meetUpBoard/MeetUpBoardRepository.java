package web.mvc.repository.meetUpBoard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.dto.meetUpBoard.MeetUpBoardDTO;
import web.mvc.dto.meetUpBoard.MeetUpUpdateDTO;
import web.mvc.entity.meetUpBoard.MeetUpBoard;

import java.util.Date;
import java.util.List;

public interface MeetUpBoardRepository extends JpaRepository<MeetUpBoard, Long> {

    @Query("select pb.meetUpDeadLine FROM MeetUpBoard pb order by pb.meetUpRegDate desc ")
    List<Date> findAllPartDeadLine();

    @Modifying
    @Transactional
    @Query("UPDATE MeetUpBoard m " +
            "SET m.meetUpName = :#{#dto.meetUpName}, m.meetUpDesc = :#{#dto.meetUpDesc}, m.meetUpDeadLine = :#{#dto.meetUpDeadLine} , m.meetUpMaxEntry = :#{#dto.meetUpMaxEntry}" +
            " WHERE m.meetUpSeq = :#{#dto.meetUpSeq}")
    int updateMeetUp(@Param("dto") MeetUpBoard dto);



    @Query("select p from MeetUpBoard p where p.meetUpName LIKE %?1%")
      List<MeetUpBoard> findMeetUPBoardByMeetUpName(String meetUpName);

    @Query("select m from MeetUpBoard  m where m.interest =?1")
     List<MeetUpBoard> selectMeetUpBoardByInterest(String interest);

    @Query("select p from MeetUpBoard p where p.meetUpSeq =?1")
        MeetUpBoard findMeetUpBoardByMeetUpSeq(Long meetupSeq);


    @Query("select p from MeetUpBoard p where p.meetUpName= ?1")
    MeetUpBoard selectMeetUpByMeetUpName (String meetUpName);



    @Modifying
    @Transactional
    @Query("update MeetUpBoard p set p.meetUpStatus = 1 where p.meetUpSeq= ?1 ")
    int updatePartyStatus(Long partySeq);


    @Query("select  p.meetUpSeq  from MeetUpBoard p where p.meetUpDeadLine = ?1 ")
    List<Long> findByPartySeqByDeadLine(Date partDeadLine);

    @Query("select  p  from MeetUpBoard p where p.meetUpSeq=?1")
    MeetUpBoard findPwdBySeq(Long meetUpSeq);
//

    @Modifying
    @Transactional
    @Query("update MeetUpBoard  p set  p.nowEntry=?1 where p.meetUpSeq=?2")
    int updateNowCount(  int count ,  Long meetUpSeq);


    @Query("select m from MeetUpBoard  m where m.interest.interestCategory =?1")
    List<MeetUpBoard> findMeetUpBoardByInterest(String interestCategory);

    @Query("select m from MeetUpBoard  m where m.chattingroom.roomId=?1")
    MeetUpBoard findMeetUpBoardByRoomId(String roomId);

}
