package web.mvc.entity.user;

import jakarta.persistence.*;
import lombok.*;
import web.mvc.entity.chatting.MessageLog;
import web.mvc.entity.generalBoard.PhotoBoard;
import web.mvc.entity.generalBoard.PhotoBoardLike;
import web.mvc.entity.meetUpBoard.MeetUpBoard;
import web.mvc.entity.meetUpBoard.MeetUpBoardList;
import web.mvc.entity.meetUpBoard.MeetUpRequest;
import web.mvc.entity.place.PlaceRecommendation;
import web.mvc.enums.users.Classification;
import web.mvc.enums.users.Gender;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name ="user_seq", allocationSize = 1, sequenceName = "user_seq")
    private Long userSeq;

    @Column(length = 100)
    private String userId;

    @Column(length = 100)
    private String userPwd;

    @Column(length = 100)
    private String userName;

    @Column(length = 100)
    private String nickName;

    @Temporal(TemporalType.DATE)
    private Date birth;

    @Column(length = 300)
    private String address;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String phone;

    @Column(length = 100)
    private String Role;

    private Classification country;

    private Gender gender;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserDetail userDetail;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<JellyTransaction> jellyTransactionList;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private EmailVerification emailVerification;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SmsVerification smsVerification;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<MeetUpBoard> meetUpBoardList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<MessageLog> messageLogList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<MeetUpRequest> meetUpRequest;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PhotoBoard> photoBoardList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PhotoBoardLike> photoBoardLikeList;

    @OneToMany(mappedBy = "user")
    private List<MeetUpBoardList> meetUpBoardLists;
}