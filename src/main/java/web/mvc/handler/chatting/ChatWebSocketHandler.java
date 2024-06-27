package web.mvc.handler.chatting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import web.mvc.dto.chat.MessageDTO;
import web.mvc.entity.meetUpBoard.MeetUpBoard;
import web.mvc.entity.user.Users;
import web.mvc.repository.user.UserRepository;
import web.mvc.service.chatting.MessageLogService;
import web.mvc.service.meetUpBoard.MeetUpBoardService;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, Map<String, WebSocketSession>> chatRooms = new ConcurrentHashMap<>();
    private final Map<Long, Map<Long, WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final MessageLogService messageLogService;
    private final MeetUpBoardService meetUpBoardService;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        log.info("Attempting connection: session {}", session);
        Map<String, String> queryParams = parseQueryParams(session.getUri().getQuery());
        String roomId = queryParams.get("roomId");
        System.out.println("roomId : "+ roomId);
        Long userSeq = Long.valueOf(queryParams.get("userSeq"));
        System.out.println("userSeq"+userSeq);
        Long chattingRoomSeq = Long.valueOf(queryParams.get("chattingRoomSeq"));
        System.out.println("chattingroomSeq"+chattingRoomSeq);
        Long roomMasterSeq = Long.valueOf(queryParams.get("roomMasterSeq"));
        System.out.println(roomMasterSeq+"roomMasterSeq");
        MeetUpBoard meetUpBoard =meetUpBoardService.findMeetUpPeopleList(roomId);
        System.out.println("++++++meetupboard+++++++" + meetUpBoard.getMeetUpPeopleList());
//        String meetUpList = meetUpBoard.getMeetUpPeopleList();
//        if ( meetUpList == "[]" || meetUpList==null ) {
//            System.out.println("정상");
//        } else {
//            JSONArray jsonArray = new JSONArray(meetUpList);
//
//            List<Object> list = jsonArray.toList();
//            boolean unauthorizedAccess = true;
//
//            for(Object peopleList : list){
//                    String userId = (String) peopleList;
//                System.out.println("userId"+ userId);
//                System.out.println("peopleList: "+peopleList);
//
//                    Users users =userRepository.findUserByUserId(userId);
//                System.out.println("users"+users);
//                    Long userSeqPeopleList = users.getUserSeq();
//                System.out.println("userSeqPeopleList"+userSeqPeopleList);
//                    if(userSeqPeopleList != userSeq){
//                        unauthorizedAccess = false;
//                        break;
////                        System.out.println("여기는 오냐");
//                    }
//            }
//            if (unauthorizedAccess) {
//                // 비정상적인 접근, 프론트엔드로 오류 메시지 전송
//                JsonNode errorJson = objectMapper.createObjectNode()
//                        .put("error", "Unauthorized access");
//                session.sendMessage(new TextMessage(errorJson.toString()));
//                session.close();
//                return;
//            }
//        }

        printCurrentUsers();
        log.info("Session connected: sessionId {}, roomId {}, roomSeq {} , userSeq {} ", session.getId(), roomId, chattingRoomSeq, userSeq);

        if (roomId == null || roomId.isEmpty()) {
            session.close();
            return;
        }

        Map<Long, WebSocketSession> userChattingRooms = userSessions.get(userSeq);
        if (userChattingRooms != null && userChattingRooms.containsKey(chattingRoomSeq)) {
            WebSocketSession existingSession = userChattingRooms.get(chattingRoomSeq);
            if (existingSession != null) {
                session.close();
                return;
            }
        }

        chatRooms.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
        userSessions.computeIfAbsent(userSeq, k -> new ConcurrentHashMap<>()).put(chattingRoomSeq, session);
    }

    private Map<String, String> parseQueryParams(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(pair -> pair[0], pair -> pair[1]));
    }

    private void printCurrentUsers() {
        log.info("Current connected users:");
        chatRooms.forEach((roomId, sessions) -> {
            log.info("Room ID: {}", roomId);
            sessions.forEach((sessionId, session) -> log.info("Session ID: {}", session.getId()));

        });
    }

    @Override
    @Transactional
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("WebSocket session received a message: {}", message.getPayload());
        String messageJson = message.getPayload();
        JSONObject jsonObject = new JSONObject(messageJson);
        String userSeq = jsonObject.getString("userSeq");
        String jsonMessage = jsonObject.getString("message");
        Map<String, String> queryParams = parseQueryParams(session.getUri().getQuery());
        String roomId = queryParams.get("roomId");
        Long chattingRoomSeq = Long.valueOf(queryParams.get("chattingRoomSeq"));

        //룸 시퀀스로 초대 받은 사람 명단 불러오기 .
        // 생성된 세션의 유저 시퀀스랑 명단에 있는 이름의 시퀀스랑 일치하는게 없다면
        // 입장 실패

        if (roomId == null || roomId.isEmpty()) {
            log.warn("Room ID is null or empty.");
            return;
        }

        Map<String, WebSocketSession> sessions = chatRooms.get(roomId);
        if (sessions == null) {
            log.warn("No sessions found for roomId: {}", roomId);
            return;
        }

        sessions.values().forEach(ws -> {
            try {
                Optional<Users> optionalUsers = userRepository.findById(Long.valueOf(userSeq));
                if (optionalUsers.isPresent()) {
                    Users users = optionalUsers.get();
                    Long seq = users.getUserSeq();
                    String nickname = users.getNickName();
                    String content = message.getPayload();
                    LocalTime currentTime = LocalTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    String formattedTime = currentTime.format(formatter);
                    JsonNode responseJson = objectMapper.createObjectNode()
                            .put("message", jsonMessage)
                            .put("senderNickname", nickname)
                            .put("nowTime", formattedTime)
                            .put("senderSeq", userSeq);
                    ws.sendMessage(new TextMessage(responseJson.toString()));
                    System.out.println("ws" + ws);
                    System.out.println("session" + session);

                    // 각 방과 사용자에 대해 메시지를 한 번만 로그함
                    if (ws == session) {  // 메시지를 수신한 세션에 대해서만 메시지 로그
                        MessageDTO messageDTO = MessageDTO.builder()
                                .chattingRoomSeq(chattingRoomSeq)
                                .chatRoomId(roomId)
                                .userSeq(seq)
                                .chattingContent(content)
                                .build();
                        messageLogService.insertMessage(messageDTO);
                    }
                }
            } catch (IOException e) {
                log.error("Error while sending message", e);
            }
        });
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // chatRooms에서 세션 제거
        chatRooms.forEach((roomId, sessions) -> {
            sessions.remove(session.getId());
            if (sessions.isEmpty()) {
                chatRooms.remove(roomId);
                log.info("Room ID removed: {}", roomId);
            }
        });

        // userSessions에서 세션 제거
        userSessions.forEach((userSeq, userChattingRooms) -> {
            userChattingRooms.values().remove(session);
            userChattingRooms.entrySet().removeIf(entry -> entry.getValue().equals(session));
        });

        log.info("Session closed: {}", session.getId());
    }

}
