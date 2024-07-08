package web.mvc.entity.meetUpBoard;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import web.mvc.entity.chatting.ChattingRoom;
import web.mvc.entity.user.Interest;
import web.mvc.entity.user.MeetupRecord;
import web.mvc.entity.user.Users;

import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetUpBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meetup_seq")
    @SequenceGenerator(allocationSize = 1, sequenceName = "meetup_seq", name = "meetup_seq")
    private Long meetUpSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq")
    private Users user;

    @Column(length = 50)
    private String meetUpName;

    @Column(length = 100)
    private String meetUpDesc;

    @OneToMany(mappedBy = "meetUpBoard", cascade = CascadeType.ALL)
    private List<MeetUpBoardDetailImg> meetUpBoardDetailImgList;

    @CreationTimestamp
    private Date meetUpRegDate;

    @OneToMany(mappedBy = "meetUpBoard", cascade = CascadeType.ALL)
    private List<MeetUpRequest> meetUpRequestsList;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "interest_seq")
    private Interest interest;

    @OneToMany(mappedBy = "meetUpBoard", cascade = CascadeType.ALL)
    private List<MeetUpBoardList> meetUpBoardList;

    @OneToOne(mappedBy = "meetUpBoard", cascade = CascadeType.ALL)
    private MeetupRecord meetupRecord;

    @UpdateTimestamp
    private Date meetUpUpdateTime;

    @Column(nullable = true)
    private String meetUpPeopleList;

    private int meetUpPwd;

    private int meetUpMaxEntry;

    @ColumnDefault("0")
    private int nowEntry;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chattingroom_seq")
    private ChattingRoom chattingroom;

    private Date meetUpDeadLine;

    private int meetUpStatus;
}