package controller;

import domain.User;
import domain.dto.ControllerDTO;
import domain.exceptions.MyException;
import domain.exceptions.SetupControllerException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Optional;

public class FriendController extends AbstractController {

    private Long friendId;
    private Long connectedUserId;

    @FXML
    private Label friendName;

    @FXML
    private void handleAddButton(ActionEvent event) {
        try {
            service.addFriend(connectedUserId, friendId);
        }
        catch (MyException e){
            MessageAlert.showError(stage, e.getMessage());
        }
    }

    @Override
    public void setupController(ControllerDTO controllerDTO) {
        service = controllerDTO.getService();
        stage = controllerDTO.getStage();
        Optional.ofNullable(service).orElseThrow(() -> new SetupControllerException("Service is null in friend controller"));
        Optional.ofNullable(stage).orElseThrow(() -> new SetupControllerException("Stage is null in friend controller"));

        connectedUserId = controllerDTO.getConnectedUserId();
        Optional.ofNullable(connectedUserId).orElseThrow(() -> new SetupControllerException("Connected user id is null in friend controller"));

        friendId = controllerDTO.getFriendDTOId();
        String firstName = controllerDTO.getFriendDTOFirstName();
        String lastName = controllerDTO.getFriendDTOLastName();
        Optional.ofNullable(connectedUserId).orElseThrow(() -> new SetupControllerException("Friend id is null in friend controller"));
        Optional.ofNullable(friendId).orElseThrow(() -> new SetupControllerException("Friend id is null in friend controller"));
        Optional.ofNullable(firstName).orElseThrow(() -> new SetupControllerException("Friend first name is null in friend controller"));
        Optional.ofNullable(lastName).orElseThrow(() -> new SetupControllerException("Friend last name is null in friend controller"));
        friendName.setText(firstName + " " + lastName);
    }
}
