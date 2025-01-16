package controller;

import domain.dto.ControllerDTO;
import domain.dto.FriendDTO;
import domain.exceptions.MyException;
import domain.exceptions.SetupControllerException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import utils.Constants;
import utils.FriendButtonType;

import java.time.LocalDateTime;
import java.util.Optional;

public class FriendController extends AbstractController {

    private FriendDTO friendDTO;
    private Long connectedUserId;

    @FXML
    private Label friendNameLabel, friendsFromLabel;

    @FXML
    private Button topButton, declineButton;

    @FXML
    private ImageView profileImage;

    @FXML
    private VBox buttonVBox, labelVBox;

    @FXML
    private void handleAddButton(ActionEvent event) {
        try {
            service.addFriend(connectedUserId, friendDTO.getIdFriend());
        } catch (MyException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
    }

    @FXML
    private void handleAcceptButton(ActionEvent event) {
        try {
            service.updateFriend(friendDTO.getIdFriendship(), true, LocalDateTime.now());
        } catch (MyException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
    }

    @FXML
    private void handleDeclineButton(ActionEvent event) {
        try {
            service.deleteFriend(friendDTO.getIdFriendship());
        } catch (MyException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        try {
            service.deleteFriend(friendDTO.getIdFriendship());
        } catch (MyException e) {
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

        Long friendId = controllerDTO.getFriendDTOIdFriend();
        String firstName = controllerDTO.getFriendDTOFirstName();
        String lastName = controllerDTO.getFriendDTOLastName();
        Optional.ofNullable(connectedUserId).orElseThrow(() -> new SetupControllerException("Friend id is null in friend controller"));
        Optional.ofNullable(friendId).orElseThrow(() -> new SetupControllerException("Friend id is null in friend controller"));
        Optional.ofNullable(firstName).orElseThrow(() -> new SetupControllerException("Friend first name is null in friend controller"));
        Optional.ofNullable(lastName).orElseThrow(() -> new SetupControllerException("Friend last name is null in friend controller"));
        friendDTO = new FriendDTO(controllerDTO.getFriendDTOIdFriendship(), friendId, firstName, lastName, controllerDTO.getFriendDTOImagePath(), controllerDTO.getFriendDTODate());
        friendNameLabel.setText(firstName + " " + lastName);

        profileImage.setImage(getImageFromString(friendDTO.getImagePath()));
        profileImage.setFitHeight(75);
        profileImage.setFitWidth(75);
        profileImage.setPreserveRatio(true);

        FriendButtonType type = controllerDTO.getFriendButtonType();
        Optional.ofNullable(type).orElseThrow(() -> new SetupControllerException("Friend button type is null in friend controller"));
        switch (type) {
            case ADD -> {
                buttonVBox.getChildren().remove(declineButton);
                labelVBox.getChildren().remove(friendsFromLabel);
                topButton.setText("Add");
                topButton.setOnAction(this::handleAddButton);
            }
            case ACCEPT -> {
                topButton.setText("Accept");
                topButton.setOnAction(this::handleAcceptButton);
                declineButton.setText("Decline");
                declineButton.setOnAction(this::handleDeclineButton);
                friendsFromLabel.setText("Sent at: " + friendDTO.getDate().format(Constants.DATE_FORMATTER));
            }
            case DELETE -> {
                buttonVBox.getChildren().remove(declineButton);
                topButton.setText("Delete");
                topButton.setOnAction(this::handleDeleteButton);
                friendsFromLabel.setText("From: " + friendDTO.getDate().format(Constants.DATE_FORMATTER));
            }
        }
    }
}
