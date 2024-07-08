package web.mvc.repository.meetUpBoard;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import web.mvc.entity.meetUpBoard.MeetUpRequest;

import java.util.List;

public interface MeetUpRequestRepository extends JpaRepository<MeetUpRequest, Long> {

    @Query("select p from MeetUpRequest p where p.meetUpBoard.meetUpSeq = ?1")
    List<MeetUpRequest> findAllByMeetUpSeq(Long meetUpBoardSeq);

    @Modifying
    @Query("update MeetUpRequest p set p.meetUpRequestStatus =  ?1 where p.meetUpBoard.meetUpSeq= ?2 and  p.user.userSeq=?3")
    int changeStatusBySeq( int meetUpRequestStatus, Long meetUpSeq, Long userSeq);

    @Query("select  p.user.userSeq from MeetUpRequest p where  p.meetUpBoard.meetUpSeq=?1")
    List<Long> findUserSeqByMeetUpReqSeq(Long meetUpReqSeq);


    @Modifying
    @Transactional
    @Query("delete from MeetUpBoardList where meetUpBoard.meetUpSeq=?1 and user.userSeq=?2")
    void deleteAllByMeetUpBoardListSeq(Long userSeq, Long meetUpBoardSeq);




}
