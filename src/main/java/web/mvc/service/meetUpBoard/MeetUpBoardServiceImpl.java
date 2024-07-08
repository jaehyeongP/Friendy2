package web.mvc.service.meetUpBoard;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import web.mvc.dto.chat.ChattingRoomDTO;
import web.mvc.dto.meetUpBoard.MeetUpBoardDTO;
import web.mvc.dto.meetUpBoard.MeetUpUpdateDTO;
import web.mvc.entity.chatting.ChattingRoom;
import web.mvc.entity.meetUpBoard.MeetUpBoard;
import web.mvc.entity.meetUpBoard.MeetUpBoardDetailImg;
import web.mvc.entity.meetUpBoard.MeetUpBoardList;
import web.mvc.entity.user.Interest;
import web.mvc.entity.user.Users;
import web.mvc.exception.common.ErrorCode;
import web.mvc.exception.common.GlobalException;
import web.mvc.repository.meetUpBoard.MeetUpBoardDetailImgRepository;
import web.mvc.repository.meetUpBoard.MeetUpBoardListRepository;
import web.mvc.repository.meetUpBoard.MeetUpBoardRepository;
import web.mvc.repository.user.InterestRepository;
import web.mvc.repository.user.UserRepository;
import web.mvc.service.chatting.ChattingRoomService;
import web.mvc.service.common.CommonService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class MeetUpBoardServiceImpl implements MeetUpBoardService {

    private final MeetUpBoardRepository meetUpBoardRepository;
    private final MeetUpBoardDetailImgRepository meetUpBoardDetailImgRepository;
    private final ChattingRoomService chattingRoomService;
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final CommonService commonService;
    private final MeetUpBoardListRepository meetUpBoardListRepository;
    @Value("${meetUp.save.dir}")
    private String uploadDir;
    @Override
    public String createParty(MeetUpBoardDTO meetUpBoardDTO, List<MultipartFile> files) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateTime = meetUpBoardDTO.getMeetUpDeadLine();
        String formattedDateTime = dateTime.replace("T", " ");
        List<MeetUpBoardDetailImg> meetUpBoardDetailImgs = new ArrayList<>();

        if ((files !=null)) {
            for (MultipartFile file : files) {
                Map<String, String> map = commonService.uploadFile(true, file, uploadDir);
                MeetUpBoardDetailImg meetUpBoardDetailImg = new MeetUpBoardDetailImg();
                meetUpBoardDetailImg.setMeetUpDetailImgSrc(map.get("imgSrc"));
                meetUpBoardDetailImg.setMeetUpDetailImgType(map.get("imgType"));
                meetUpBoardDetailImg.setMeetUpDetailImgSize(map.get("imgSize"));
                meetUpBoardDetailImg.setMeetUpDetailImgName(map.get("imgName"));
                meetUpBoardDetailImgs.add(meetUpBoardDetailImg);
                meetUpBoardDetailImgRepository.save(meetUpBoardDetailImg);
            }
        }
        Date date;
        try {
            date = formatter.parse(formattedDateTime);
        } catch (ParseException e) {
            throw new GlobalException(ErrorCode.WRONG_DATE);
        }

        Long interestSeq = meetUpBoardDTO.getInterestSeq();
        Optional<Interest> interestOptional = interestRepository.findById(interestSeq);
        ChattingRoom chattingRoom= new ChattingRoom();
        if (interestOptional.isPresent()) {
            Interest interest = interestOptional.get();
            String interestCategory = interest.getInterestCategory();
            Users users = Users.builder().userSeq(meetUpBoardDTO.getUserSeq()).build();
            // MeetUpBoard 객체 생성
            try {
                Optional<Users> optionalUsers = userRepository.findById(meetUpBoardDTO.getUserSeq());
                if (optionalUsers.isPresent()) {
                    Users users2 = optionalUsers.get();
                    String userId = users2.getUserId();
                    ChattingRoomDTO chattingRoomDTO = ChattingRoomDTO.builder()
                            .userId(userId)
                            .build();
                   chattingRoom= chattingRoomService.createChattingRoom(chattingRoomDTO);

                }

            } catch (NumberFormatException e) {
                throw new GlobalException(ErrorCode.WRONG_TYPE);
            }






            MeetUpBoard meetUpBoard = MeetUpBoard.builder()
                    .user(users)
                    .meetUpName(meetUpBoardDTO.getMeetUpName())
                    .meetUpDesc(meetUpBoardDTO.getMeetUpDesc())
                    .meetUpBoardDetailImgList(meetUpBoardDetailImgs) // 이미지 리스트 설정
                    .interest(interest)
                    .meetUpPwd(meetUpBoardDTO.getMeetUpPwd())
                    .meetUpDeadLine(date)
                    .meetUpMaxEntry(meetUpBoardDTO.getMeetUpMaxEntry())
                    .meetUpStatus(meetUpBoardDTO.getMeetUpStatus())
                    .chattingroom(chattingRoom)
                    .build();
            meetUpBoardRepository.save(meetUpBoard);



            MeetUpBoardList meetUpBoardList =  MeetUpBoardList.builder()
                    .meetUpBoard(meetUpBoard)
                    .user(users)
                    .build();

           meetUpBoardListRepository.save(meetUpBoardList);



            // 각 이미지 객체에 관계 설정
            for (MeetUpBoardDetailImg img : meetUpBoardDetailImgs) {
                img.setMeetUpBoard(meetUpBoard);
            }


        }

        return "성공";
    }




    @Override
    public String updateBoard(MeetUpUpdateDTO meetUpUpdateDTO,List<MultipartFile> files) throws Exception {

        Long updateTargetSeq = meetUpUpdateDTO.getMeetUpSeq();
        List<MeetUpBoardDetailImg> meetUpBoardDetailImgs = new ArrayList<>();
        MeetUpBoard meetUpBoardForImg = new MeetUpBoard();
        meetUpBoardForImg.setMeetUpSeq(meetUpUpdateDTO.getMeetUpSeq());

        if (files !=null) {
            meetUpBoardDetailImgRepository.deleteAllByMeetUpBoardSeq(meetUpUpdateDTO.getMeetUpSeq());
            for (MultipartFile file : files) {
                Map<String, String> map = commonService.uploadFile(true, file, uploadDir);
                MeetUpBoardDetailImg meetUpBoardDetailImg = new MeetUpBoardDetailImg();
                meetUpBoardDetailImg.setMeetUpBoard(meetUpBoardForImg);
                meetUpBoardDetailImg.setMeetUpDetailImgSrc(map.get("imgSrc"));
                meetUpBoardDetailImg.setMeetUpDetailImgType(map.get("imgType"));
                meetUpBoardDetailImg.setMeetUpDetailImgSize(map.get("imgSize"));
                meetUpBoardDetailImg.setMeetUpDetailImgName(map.get("imgName"));
                meetUpBoardDetailImgs.add(meetUpBoardDetailImg);
                meetUpBoardDetailImgRepository.save(meetUpBoardDetailImg);
            }
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateTime = meetUpUpdateDTO.getMeetUpDeadLine();
        String formattedDateTime = dateTime.replace("T", " ");
        Date date;
        try {
            date = formatter.parse(formattedDateTime);
        } catch (ParseException e) {
            // meetUpDeadLine 날짜 형식이 잘못된 경우 처리
            throw new GlobalException(ErrorCode.WRONG_DATE);
        }
            MeetUpBoard meetUpBoard2 = MeetUpBoard.builder()
                    .meetUpSeq(meetUpUpdateDTO.getMeetUpSeq())
                    .meetUpName(meetUpUpdateDTO.getMeetUpName())
                    .meetUpDesc(meetUpUpdateDTO.getMeetUpDesc())
                    .meetUpMaxEntry(meetUpUpdateDTO.getMeetUpMaxEntry())
                    .meetUpDeadLine(date)
                    .build();

            meetUpBoardRepository.updateMeetUp(meetUpBoard2);


        return null;
    }

    @Override
    public String deleteBoard( String meetUpSeq, String meetUpPwd) {
        int insertPwd = Integer.parseInt(meetUpPwd);

        meetUpBoardDetailImgRepository.deleteAllByMeetUpBoardSeq(Long.valueOf(meetUpSeq));
        //사제하려는 게시글의 Seq
        Long targetSeq = Long.valueOf(meetUpSeq);
        //Seq로  삭제 시도하려는 게시글의 정보 뽑기.
        MeetUpBoard meetUpBoard2 = meetUpBoardRepository.findPwdBySeq(targetSeq);
        //삭제시도하는 게시글의 비밀번호
        int boardPwd = meetUpBoard2.getMeetUpPwd();
        //입력받은 비밀번호랑 삭제 시도하는 게시글 seq로 받아온 비밀번호가 일치하면
        if (boardPwd == insertPwd) {
            //해당 디티오의 seq로 삭제.
            MeetUpBoard meetUpBoard = MeetUpBoard.builder()
                    .meetUpSeq(targetSeq).build();
            meetUpBoardRepository.delete(meetUpBoard);
            return null;
        } else {
            String msg = " 비밀번호가 일치하지 않습니다";
            return msg;
        }
    }

    @Override
    public MeetUpBoard findMeetUpByMeetUpName(String meetUpName) {

        MeetUpBoard meetUpBoard = MeetUpBoard.builder()
                .meetUpName(meetUpName).build();
        meetUpBoardRepository.selectMeetUpByMeetUpName(meetUpName);
        return meetUpBoard;
    }

    @Override
    public MeetUpBoard findMeetUpPeopleList(String roomId) {

MeetUpBoard meetUpBoardForSeq=meetUpBoardRepository.findMeetUpBoardByRoomId(roomId);
        return  meetUpBoardForSeq;
    }

    @Override
    public Resource getDetailImg(String imgName) {

        Resource resource= new FileSystemResource(uploadDir+"\\"+imgName);
        return resource;
    }

    @Override
    public List<MeetUpBoard> findMeetUpByInterest(String interest) {

            List<MeetUpBoard> list =meetUpBoardRepository.selectMeetUpBoardByInterest(interest);
        return list;
    }

    @Override
    public MeetUpBoard findMeetUpByBoardSeq(Long meetUpSeq) {

        MeetUpBoard meetUpBoardInfo = meetUpBoardRepository.findMeetUpBoardByMeetUpSeq(meetUpSeq);
        return meetUpBoardInfo;
    }

    @Override
    public List<MeetUpBoard> findByMeetUpName(String meetUpName) {

        List<MeetUpBoard> resultList = meetUpBoardRepository.findMeetUPBoardByMeetUpName(meetUpName);
        return null;
    }


    @Override
    public List<MeetUpBoard> selectAll() {


        List<MeetUpBoard> meetUpBoardList= meetUpBoardRepository.findAll();
        return meetUpBoardList;
    }

    @Override
    public List<Date> findByPartySeq() {
        List<Date> list = meetUpBoardRepository.findAllPartDeadLine();
        return list;
    }


    //스캐줄러
    //매일 정각에 체크하는기능 .
    @Override
    //    @Scheduled(cron = "0 0 * * * ?") //매시간마다 배포시에는 주석 해제해야함.
//    @Scheduled(cron = "0 * * * * ?") // 매 분마다 작동
    public void checkDeadLine() {  //받아오는 타입 문제 ..
        List<Date> list = findByPartySeq();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime truncatedNow = now.withMinute(0).withSecond(0).withNano(0);
//        System.out.println("now:" + now);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        String formattedNow = now.format(formatter);
        for (Date date : list) {
            Date deadList = date;
            String deadListString = date.toString();
            if (deadListString.equals(formattedNow)) {
                //데드라인과 현재시간이 일치하게 되면 해당 데드라인의 시퀀스 정보 가져옴.
                List<Long> meetUpSeq = meetUpBoardRepository.findByPartySeqByDeadLine(deadList);
                for (Long partSeq : meetUpSeq) {
                    //가져온 시퀀스에 해당되는 상태 1로 변경
                    meetUpBoardRepository.updatePartyStatus(partSeq);

                }
            }
        }
    }

}
