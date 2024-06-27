package web.mvc.service.generalBoard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import web.mvc.dto.generalBoard.CommunityBoardDTO;
import web.mvc.entity.generalBoard.CommunityBoard;
import web.mvc.entity.user.Users;
import web.mvc.repository.generalBoard.CommunityBoardRepository;
import web.mvc.repository.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityBoardServiceImpl implements CommunityBoardService {

    private final CommunityBoardRepository communityBoardRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CommunityBoardDTO> getAllCommunityBoards() {
        log.info("Fetching all community boards");
        List<CommunityBoard> communityBoards = communityBoardRepository.findAll();

        if (communityBoards == null || communityBoards.isEmpty()) {
            log.warn("게시물이 없습니다.");
            throw new RuntimeException("게시물이 없습니다.");
        }

        List<CommunityBoardDTO> communityBoardDTOs = communityBoards.stream()
                .map(CommunityBoard::toDTO)
                .collect(Collectors.toList());

        log.info("Fetched {} community boards", communityBoardDTOs.size());
        return communityBoardDTOs;
    }


    @Transactional
    @Override
    public CommunityBoardDTO createCommunityBoard(CommunityBoardDTO communityBoardDTO) {
        log.info("Creating CommunityBoard with title: {}", communityBoardDTO.getBoardTitle());

        // 사용자 엔티티 조회
        Users user = userRepository.findById(communityBoardDTO.getUserSeq())
                .orElseThrow(() -> new RuntimeException("User not found with userid: " + communityBoardDTO.getUserSeq()));

        // 조회된 사용자 객체를 변환 메서드에 전달하여 CommunityBoard 엔티티 생성
        CommunityBoard communityBoard = communityBoardDTO.toEntity(user);

        // CommunityBoard 엔티티 저장
        CommunityBoard savedCommunityBoard = communityBoardRepository.save(communityBoard);
        log.info("CommunityBoard created with SEQ: {}", savedCommunityBoard.getCommBoardSeq());

        // 저장된 CommunityBoard 엔티티를 DTO로 변환하여 반환
        CommunityBoardDTO savedCommunityBoardDTO = savedCommunityBoard.toDTO();
        log.info("CommunityBoardDTO created with SEQ: {}", savedCommunityBoardDTO.getCommBoardSeq());
        return savedCommunityBoardDTO;
    }

    // 실명 게시판 조회
    @Transactional(readOnly = true)
    @Override
    public List<CommunityBoardDTO> getAllRealNameCommunityBoards() {
        log.info("Fetching all real name community boards");
        List<CommunityBoard> communityBoards = communityBoardRepository.findByBoardTypeOrderByBoardRegDateDesc(0);

        if (communityBoards == null || communityBoards.isEmpty()) {
            log.warn("실명 게시물이 없습니다.");
            return null;
        }

        List<CommunityBoardDTO> communityBoardDTOs = communityBoards.stream()
                .map(CommunityBoard::toDTO)
                .collect(Collectors.toList());

        log.info("Fetched {} real name community boards", communityBoardDTOs.size());
        return communityBoardDTOs;
    }

    // 익명 게시판 조회
    @Transactional(readOnly = true)
    @Override
    public List<CommunityBoardDTO> getAllAnonymousCommunityBoards() {
        log.info("Fetching all anonymous community boards");
        List<CommunityBoard> communityBoards = communityBoardRepository.findByBoardTypeOrderByBoardRegDateDesc(1);

        if (communityBoards == null || communityBoards.isEmpty()) {
            log.warn("익명 게시물이 없습니다.");
            return null;
        }

        List<CommunityBoardDTO> communityBoardDTOs = communityBoards.stream()
                .map(CommunityBoard::toDTO)
                .collect(Collectors.toList());

        log.info("Fetched {} anonymous community boards", communityBoardDTOs.size());
        return communityBoardDTOs;
    }

    @Transactional(readOnly = true)
    @Override
    public CommunityBoardDTO getCommunityBoardById(Long commBoardSeq) {
        log.info("Fetching community board with SEQ: {}", commBoardSeq);

        // CommunityBoard 엔티티 조회
        CommunityBoard fetchedBoard = communityBoardRepository.findById(commBoardSeq)
                .orElseThrow(() -> new RuntimeException("CommunityBoard not found with seq: " + commBoardSeq));

        communityBoardRepository.updateCommBoardCount(commBoardSeq);

        // 엔티티를 DTO로 변환하여 반환
        CommunityBoardDTO fetchedBoardDTO = fetchedBoard.toDTO();
        log.info("Fetched community board with SEQ: {}", fetchedBoardDTO.getCommBoardSeq());

        return fetchedBoardDTO;
    }

    @Transactional
    @Override
    public CommunityBoardDTO updateCommunityBoard(Long commBoardSeq, CommunityBoardDTO communityBoardDTO) {

        log.info("Updating community board with SEQ: {}", commBoardSeq);

        // 기존 CommunityBoard 엔티티 조회
        CommunityBoard existingBoard = communityBoardRepository.findById(commBoardSeq)
                .orElseThrow(() -> new RuntimeException("CommunityBoard not found with seq: " + commBoardSeq));

        // 업데이트할 사용자 엔티티 조회
        Users user = userRepository.findById(communityBoardDTO.getUserSeq())
                .orElseThrow(() -> new RuntimeException("User not found with userid: " + communityBoardDTO.getUserSeq()));

        // 엔티티 필드 업데이트
        existingBoard.setUser(user);
        existingBoard.setBoardTitle(communityBoardDTO.getBoardTitle());
        existingBoard.setBoardContent(communityBoardDTO.getBoardContent());
        existingBoard.setBoardType(communityBoardDTO.getBoardType());
        existingBoard.setBoardLike(communityBoardDTO.getBoardLike());
        existingBoard.setBoardPwd(communityBoardDTO.getBoardPwd());
        existingBoard.setCommBoardCount(communityBoardDTO.getCommBoardCount());


        // 엔티티 저장
        CommunityBoard updatedCommunityBoard = communityBoardRepository.save(existingBoard);
        log.info("CommunityBoard updated with SEQ: {}", updatedCommunityBoard.getCommBoardSeq());

        // 저장된 엔티티를 DTO로 변환하여 반환
        CommunityBoardDTO updatedCommunityBoardDTO = updatedCommunityBoard.toDTO();

        return updatedCommunityBoardDTO;
    }

    @Transactional
    @Override
    public String deleteCommunityBoard(Long commBoardSeq) {
        log.info("Deleting community board with SEQ: {}", commBoardSeq);

        // 삭제하려는 CommunityBoard 엔티티 조회
        CommunityBoard deletingBoard = communityBoardRepository.findById(commBoardSeq)
                .orElseThrow(() -> new RuntimeException("CommunityBoard not found with seq: " + commBoardSeq));

        // CommunityBoard 엔티티 삭제
        communityBoardRepository.delete(deletingBoard);
        log.info("CommunityBoard deleted with SEQ: {}", commBoardSeq);

        String message = "CommunityBoard deleted successfully";
        log.info(message);
        return message;
    }

    // 키워드로 게시물 검색
    @Transactional(readOnly = true)
    @Override
    public List<CommunityBoardDTO> searchCommunityBoards(int boardType, String keyword) {
        List<CommunityBoard> communityBoards = communityBoardRepository.findByBoardTypeAndBoardTitleContainingOrBoardContentContainingOrderByBoardRegDateDesc(
                boardType, keyword, keyword);

        return communityBoards.stream()
                .map(CommunityBoard::toDTO)
                .collect(Collectors.toList());
    }

}


