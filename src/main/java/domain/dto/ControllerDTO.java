package domain.dto;

import domain.User;
import javafx.stage.Stage;
import service.Service;
import utils.FriendButtonType;

import java.time.LocalDateTime;

public class ControllerDTO {

    private Service service = null;
    private Stage stage = null;
    private User connectedUser = null;
    private Long connectedUserId = null;
    private UserDTO friendDTO = null;
    private FriendButtonType friendButtonType = null;

    public ControllerDTO(Service service, Stage stage, User connectedUser, UserDTO friendDTO, FriendButtonType friendButtonType) {
        this.service = service;
        this.stage = stage;
        this.connectedUser = connectedUser;
        connectedUserId = connectedUser.getId();
        this.friendButtonType = friendButtonType;
    }

    public ControllerDTO(Service service, Stage stage, User connectedUser, UserDTO friendDTO) {
        this.service = service;
        this.stage = stage;
        this.connectedUser = connectedUser;
        connectedUserId = connectedUser.getId();
        this.friendDTO = friendDTO;
    }

    public ControllerDTO(Service service, Stage stage, Long connectedUserId, UserDTO friendDTO) {
        this.service = service;
        this.stage = stage;
        this.connectedUserId = connectedUserId;
        this.friendDTO = friendDTO;
    }

    public ControllerDTO(Service service, Stage stage, User connectedUser) {
        this.service = service;
        this.stage = stage;
        this.connectedUser = connectedUser;
        connectedUserId = connectedUser.getId();
    }

    public ControllerDTO(Service service, Stage stage, Long connectedUserId) {
        this.service = service;
        this.stage = stage;
        this.connectedUserId = connectedUserId;
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
        return connectedUser != null;
    }

    public Long getConnectedUserId() {
        return connectedUserId;
    }

    public String getConnectedUserFirstName() {
        return connectedUser.getFirstName();
    }

    public String getConnectedUserLastName() {
        return connectedUser.getLastName();
    }

    public String getConnectedUserEmail() {
        return connectedUser.getEmail();
    }

    public String getConnectedUserPassword() {
        return connectedUser.getPassword();
    }

    public Long getFriendDTOId() {
        return friendDTO.getId();
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
}
