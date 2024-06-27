package web.mvc.controller.meetUpBoard;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.mvc.dto.meetUpBoard.MeetUpBoardDTO;
import web.mvc.dto.meetUpBoard.MeetUpDeleteDTO;
import web.mvc.dto.meetUpBoard.MeetUpSendDTO;
import web.mvc.dto.meetUpBoard.MeetUpUpdateDTO;
import web.mvc.dto.user.InterestDTO;
import web.mvc.entity.meetUpBoard.MeetUpBoard;
import web.mvc.entity.meetUpBoard.MeetUpBoardDetailImg;
import web.mvc.entity.user.Interest;
import web.mvc.repository.user.InterestRepository;
import web.mvc.service.meetUpBoard.MeetUpBoardService;
import web.mvc.service.meetUpBoard.MeetUpDetailImgService;
import web.mvc.service.place.PlaceService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/partyBoard")
@Slf4j
public class MeetUpBoardController {

    private final MeetUpBoardService meetUpBoardService;
    private final InterestRepository interestRepository;
    private final MeetUpDetailImgService meetUpDetailImgService;

    @PostMapping(value = "/create")
    public ResponseEntity<?> createPartyBoard(@ModelAttribute MeetUpBoardDTO meetUpBoardDTO, @RequestPart(value = "file", required = false) List<MultipartFile> file) throws Exception {
        //입력된 타입이 안맞으면 @RequestBody에서 애초에 걸러져버림. .
        System.out.println("여기도 못가는거 맞지 ");
        System.out.println("받아온시퀀스"+meetUpBoardDTO.getUserSeq());
        System.out.println(meetUpBoardDTO.getMeetUpName());
        System.out.println(meetUpBoardDTO.getMeetUpDesc());
        System.out.println(meetUpBoardDTO.getMeetUpDeadLine());
        System.out.println("file : "+ file);
        System.out.println(meetUpBoardDTO.getMeetUpMaxEntry());
        System.out.println(meetUpBoardDTO.getMeetUpPwd());
        System.out.println(meetUpBoardDTO.getInterestCate());
    String result =meetUpBoardService.createParty(meetUpBoardDTO, file);
//        System.out.println(result+"결과");
        return ResponseEntity.status(HttpStatus.OK).body("성공");
    }

    @PutMapping("/update")
    //게시글 수정
    public ResponseEntity<?> updateMeetUpBoard( @ModelAttribute MeetUpUpdateDTO meetUpUpdateDTO, @RequestPart(value = "file" , required = false) List<MultipartFile> file) throws Exception {
        System.out.println("밋업" + meetUpUpdateDTO);
        System.out.println("설명:"+meetUpUpdateDTO.getMeetUpDesc());
        System.out.println("넘버 : "+ meetUpUpdateDTO.getMeetUpMaxEntry());
        System.out.println("날짜 :" +meetUpUpdateDTO.getMeetUpDeadLine());

            meetUpBoardService.updateBoard(meetUpUpdateDTO, file);
        return  ResponseEntity.status(HttpStatus.OK).body("test");
    }

    @GetMapping("/search/{meetUpName}")
    //게시글 이름으로 검색
    public ResponseEntity<?> selectMeetUpBoard(@PathVariable String meetUpName) {
        System.out.println("들어온 제목"+meetUpName);
        List<MeetUpBoard> list = meetUpBoardService.findByMeetUpName(meetUpName);
        System.out.println("제목 리스트" + list);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

//    @PutMapping("/invite")
//    public ResponseEntity<?> inviteMeetUpBoard(@)
    @GetMapping("/interestList")
    public ResponseEntity<?> findAllInterestList(){
        List<Interest> interestList=interestRepository.findAll();

       // InterestDTO interestDTO = new InterestDTO();
        List<InterestDTO> interestDTOList= new ArrayList<>();

        for(Interest interest : interestList){

            InterestDTO interestDTO = InterestDTO.builder()
                    .interestCategory(interest.getInterestCategory())
                    .interestSeq(interest.getInterestSeq())
                    .build();

            interestDTOList.add(interestDTO);


            System.out.println("intertest"+interest.getInterestCategory().toString());
        }




        return ResponseEntity.status(HttpStatus.OK).body(interestDTOList);
    }

@GetMapping("/selectAll")
public ResponseEntity<?> findAllMeetUp() {
    System.out.println("++++++++++++++++++++++++");
    List<MeetUpBoard> meetUpBoardList = meetUpBoardService.selectAll();
    List<MeetUpSendDTO> meetUpSendDTOList = new ArrayList<>();

    for (MeetUpBoard board : meetUpBoardList) {
        // 각 MeetUpBoard 객체의 정보를 출력합니다.
        System.out.println(board.getMeetUpName());


        Date date = board.getMeetUpDeadLine();
        String meetUpImgName= null;
        List<MeetUpBoardDetailImg> list=meetUpDetailImgService.findImgList(board.getMeetUpSeq());
        System.out.println("이미지 list"+list);
        List<String> imgNameList= new ArrayList<>();
             for(MeetUpBoardDetailImg meetUpBoardDetailImg : list){
                 meetUpImgName=meetUpBoardDetailImg.getMeetUpDetailImgName();
                    imgNameList.add(meetUpImgName);
             }

        MeetUpSendDTO meetUpSendDTO = MeetUpSendDTO.builder()
                .meetUpSeq(board.getMeetUpSeq())
                .meetUpDesc(board.getMeetUpDesc())
                .userSeq(board.getUser().getUserSeq())
                .interestCate(board.getInterest().getInterestCategory())
                .meetUpName(board.getMeetUpName())
                .meetUpBoardDetailImgNameList(imgNameList)
                .meetUpDeadLine(String.valueOf(date))
                .meetUpPeopleList(board.getMeetUpPeopleList())
                .meetUpMaxEntry(board.getMeetUpMaxEntry())
                .meetUpPwd(board.getMeetUpPwd())
                .meetUpStatus(board.getMeetUpStatus())
                .build();

        meetUpSendDTOList.add(meetUpSendDTO);
    }

    System.out.println("meetUpSendDTOList: " + meetUpSendDTOList);
    System.out.println("meetUpBoardList: " + meetUpBoardList);
    return ResponseEntity.status(HttpStatus.OK).body(meetUpSendDTOList);
}


@GetMapping("/seqimg")
public ResponseEntity<?> selectImgBySeq(@RequestParam String meetUpDetailImg) {

    System.out.println("meetUpDetailImg  :"+meetUpDetailImg);

        Resource resource=meetUpBoardService.getDetailImg(meetUpDetailImg);

    System.out.println("resource :"+ resource);

        return ResponseEntity.status(HttpStatus.OK).body(resource);

}


    @GetMapping("/select/{meetUpName}")
    //게시글 상세보기
    public ResponseEntity<?> selectMeetUpSeq(@PathVariable String meetUpName){

        System.out.println("입력된 meetUpname"+ meetUpName);
        MeetUpBoard meet =meetUpBoardService.findMeetUpByMeetUpName(meetUpName);
        System.out.println("결과물"+meet);
       return ResponseEntity.status(HttpStatus.OK).body(meet);
    }

    @GetMapping("/select/{interest}")
    public ResponseEntity<?>  selectAllByInterest(@PathVariable Long interest){

        Optional<Interest> interestOptional = interestRepository.findById(interest);
        if(interestOptional.isPresent()){

            Interest forInterest= interestOptional.get();
            String interestCate= forInterest.getInterestCategory();
            List<MeetUpBoard> meet=meetUpBoardService.findMeetUpByInterest(interestCate);
            return ResponseEntity.status(HttpStatus.OK).body(meet);

        }




        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }


    @GetMapping("/meetUpSeq")
    public ResponseEntity<?> selectMeetUpBoard (@RequestParam  Long meetUpSeq){
      MeetUpBoard meetUpBoard=   meetUpBoardService.findMeetUpByBoardSeq( meetUpSeq);
        Optional<Interest> interestOptional=   interestRepository.findById(meetUpBoard.getInterest().getInterestSeq());
        MeetUpSendDTO meetUpSendDTO= new MeetUpSendDTO();
        List<MeetUpBoardDetailImg> list=meetUpDetailImgService.findImgList(meetUpSeq);
        List<String> imgNameList= new ArrayList<>();
        String meetUpImgName= null;
        for(MeetUpBoardDetailImg meetUpBoardDetailImg : list){
            meetUpImgName=meetUpBoardDetailImg.getMeetUpDetailImgName();
            imgNameList.add(meetUpImgName);
        }



        if (interestOptional.isPresent()) {
            System.out.println("등록일"+meetUpBoard.getMeetUpRegDate());


            Interest interest = interestOptional.get();
             meetUpSendDTO = meetUpSendDTO.builder()
                    .meetUpName(meetUpBoard.getMeetUpName())
                    .meetUpDesc(meetUpBoard.getMeetUpDesc())
                    .interestCate(interest.getInterestCategory())
                     .interestSeq(interest.getInterestSeq())
                    .meetUpDeadLine(String.valueOf(meetUpBoard.getMeetUpDeadLine()))
                     .meetUpBoardDetailImgNameList(imgNameList)
                     .meetUpStatus(meetUpBoard.getMeetUpStatus())
                     .meetUpPeopleList(meetUpBoard.getMeetUpPeopleList())
                    .meetUpPwd(meetUpBoard.getMeetUpPwd())
                    .meetUpMaxEntry(meetUpBoard.getMeetUpMaxEntry())
                     .nowEntry(meetUpBoard.getNowEntry())
                     .meetUpRegDate(String.valueOf(meetUpBoard.getMeetUpRegDate()))
                     .userSeq(meetUpBoard.getUser().getUserSeq())
                     .roomId(meetUpBoard.getChattingroom().getRoomId())
                     .chattingRoomSeq(meetUpBoard.getChattingroom().getChattingroomSeq())
                     .userName(meetUpBoard.getUser().getUserName())
                     .build();
        }
        System.out.println("meetUpSeq :"+ meetUpSeq);
        return ResponseEntity.status(HttpStatus.OK).body(meetUpSendDTO);
    }





    @DeleteMapping("/delete")
    //게시글 삭제
    public ResponseEntity<?> deleteMeetUPBoard(@RequestParam("meetUpSeq") String meetUpSeq, @RequestParam("meetUpPwd") String meetUpPwd) {
        System.out.println(meetUpPwd);

        System.out.println(meetUpSeq);
        String result = meetUpBoardService.deleteBoard(meetUpSeq,meetUpPwd);
        System.out.println();
        if (result == null) {
//             삭제 성공 시
            return ResponseEntity.status(HttpStatus.OK).body("삭제가 성공적으로 완료되었습니다.");
        } else {
//             삭제 실패 시
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }


}
}
