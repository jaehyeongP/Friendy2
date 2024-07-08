package web.mvc.repository.meetUpBoard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import web.mvc.entity.meetUpBoard.MeetUpBoardList;

public interface MeetUpBoardListRepository extends JpaRepository<MeetUpBoardList, Long> {


}
