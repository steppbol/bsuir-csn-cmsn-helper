package com.bsuir.sdtt.chat;

import com.bsuir.sdtt.dto.MessageDTO;
import com.bsuir.sdtt.mapper.MessageMapper;
import com.bsuir.sdtt.service.MessageService;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class ChatServer {
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageMapper messageMapper;

    private SocketIOServer socketIOServer;
    private Map<UUID, SocketIOClient> users;

    public ChatServer() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(10000);

        socketIOServer = new SocketIOServer(config);
        users = new HashMap<>();

        setupListeners();
        socketIOServer.start();
    }

    private void setupListeners() {
        socketIOServer.addConnectListener(socketIOClient -> {
            String userId = socketIOClient.getHandshakeData().getSingleUrlParam("userId");

            if (userId != null) {
                users.put(UUID.fromString(userId), socketIOClient);
            } else {
                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setMessage("You don't have enough access rights");
                messageDTO.setCreationDate(new Timestamp(System.currentTimeMillis()));
                socketIOClient.sendEvent("access_denied", messageDTO);
                socketIOClient.disconnect();
            }
        });

        socketIOServer.addEventListener("new_message", MessageDTO.class, (socketIOClient, messageDTO, ackRequest) -> {
            if (users.get(messageDTO.getReceiverId()) != null) {
                users.get(messageDTO.getReceiverId()).sendEvent("new_message", messageDTO);
            }
            messageService.addMessage(messageMapper.messageDTOtoMessage(messageDTO));

            if (ackRequest.isAckRequested()) {
                ackRequest.sendAckData(1);
            }
        });

        socketIOServer.addDisconnectListener(socketIOClient -> {
            for (Map.Entry<UUID, SocketIOClient> entry : users.entrySet()) {
                if (entry.getValue() == socketIOClient) {
                    users.remove(entry.getKey());
                    entry.getValue().disconnect();
                    break;
                }
            }
        });
    }
}
