package domain.dto;

import controller.Controller;
import javafx.stage.Stage;
import service.Service;
import utils.FriendButtonType;

import java.time.LocalDateTime;

public class ControllerDTO {

    private Service service = null;
    private Stage stage = null;
    private UserDTO connectedUserDTO = null;
    private FriendDTO friendDTO = null;
    private MessageDTO messageDTO = null;
    private FriendButtonType friendButtonType = null;
    private Controller parentController = null;

    public ControllerDTO(Service service, Stage stage, UserDTO connectedUserDTO, FriendDTO friendDTO, FriendButtonType friendButtonType) {
        this.service = service;
        this.stage = stage;
        this.connectedUserDTO = connectedUserDTO;
        this.friendDTO = friendDTO;
        this.friendButtonType = friendButtonType;
    }

    public ControllerDTO(Service service, Stage stage, MessageDTO messageDTO) {
        this.service = service;
        this.stage = stage;
        this.messageDTO = messageDTO;
    }

    public ControllerDTO(Service service, Stage stage, UserDTO connectedUserDTO, FriendDTO friendDTO) {
        this.service = service;
        this.stage = stage;
        this.connectedUserDTO = connectedUserDTO;
        this.friendDTO = friendDTO;
    }

    public ControllerDTO(Service service, Stage stage, UserDTO connectedUserDTO, Controller parentController) {
        this.service = service;
        this.stage = stage;
        this.connectedUserDTO = connectedUserDTO;
        this.parentController = parentController;
    }

    public ControllerDTO(Service service, Stage stage, UserDTO connectedUserDTO) {
        this.service = service;
        this.stage = stage;
        this.connectedUserDTO = connectedUserDTO;
    }

    public ControllerDTO(Service service, Stage stage) {
        this.service = service;
        this.stage = stage;
    }

    public Service getService() {
        return service;
    }

    public Stage getStage() {
        return stage;
    }

    public boolean isConnectedUser() {
        return connectedUserDTO != null;
    }

    public Long getConnectedUserId() {
        return connectedUserDTO.getId();
    }

    public String getConnectedUserDTOFirstName() {
        return connectedUserDTO.getFirstName();
    }

    public String getConnectedUserDTOLastName() {
        return connectedUserDTO.getLastName();
    }

    public String getConnectedUserDTOEmail() {
        return connectedUserDTO.getEmail();
    }

    public String getConnectedUserDTOPassword() {
        return connectedUserDTO.getPassword();
    }

    public Long getFriendDTOIdFriendship() {
        return friendDTO.getIdFriendship();
    }

    public Long getFriendDTOIdFriend() {
        return friendDTO.getIdFriend();
    }

    public String getFriendDTOFirstName() {
        return friendDTO.getFirstName();
    }

    public String getFriendDTOLastName() {
        return friendDTO.getLastName();
    }

    public LocalDateTime getFriendDTODate() {
        return friendDTO.getDate();
    }

    public FriendButtonType getFriendButtonType() {
        return friendButtonType;
    }

    public MessageDTO getMessageDTO() {
        return messageDTO;
    }

    public Controller getParentController() {
        return parentController;
    }
}
