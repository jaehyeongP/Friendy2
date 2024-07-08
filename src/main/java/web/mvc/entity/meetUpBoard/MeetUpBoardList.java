package web.mvc.entity.meetUpBoard;

import jakarta.persistence.*;
import lombok.*;
import web.mvc.entity.user.Users;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetUpBoardList {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meetupboardlist_seq")
    @SequenceGenerator(allocationSize = 1, sequenceName = "meetupboardlist_seq", name = "meetupboardlist_seq")
    private Long meetUpBoardListSeq;  // 오타 수정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="meetup_seq")
    private MeetUpBoard meetUpBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq")
    private Users user;
}