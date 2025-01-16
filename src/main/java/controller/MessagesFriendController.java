package controller;

import domain.dto.ControllerDTO;
import domain.dto.FriendDTO;
import domain.exceptions.SetupControllerException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.Optional;

public class MessagesFriendController extends AbstractController {

    private Long connectedUserId;
    private FriendDTO friendDTO;

    @FXML
    private AnchorPane root;

    @FXML
    private ToggleButton toggleButton;

    @FXML
    private ImageView profileImage;

    @FXML
    private Label friendNameLabel;

    @FXML
    private void handleToggleButton(ActionEvent event) {
        if (toggleButton.isSelected()) {
            root.setStyle("-fx-background-color: #eb3296");
        } else {
            root.setStyle("-fx-background-color: #eb1c6f");
        }
    }

    public ToggleButton getToggleButton() {
        return toggleButton;
    }

    @Override
    public void setupController(ControllerDTO controllerDTO) {
        service = controllerDTO.getService();
        stage = controllerDTO.getStage();
        Optional.ofNullable(service).orElseThrow(() -> new SetupControllerException("Service is null in message friend controller"));
        Optional.ofNullable(stage).orElseThrow(() -> new SetupControllerException("Stage is null in message friend controller"));

        connectedUserId = controllerDTO.getConnectedUserId();
        Optional.ofNullable(connectedUserId).orElseThrow(() -> new SetupControllerException("Connected user id is null in message friend controller"));

        Long friendId = controllerDTO.getFriendDTOIdFriend();
        String firstName = controllerDTO.getFriendDTOFirstName();
        String lastName = controllerDTO.getFriendDTOLastName();
        Optional.ofNullable(friendId).orElseThrow(() -> new SetupControllerException("Friend id is null in message friend controller"));
        Optional.ofNullable(firstName).orElseThrow(() -> new SetupControllerException("Friend first name is null in message friend controller"));
        Optional.ofNullable(lastName).orElseThrow(() -> new SetupControllerException("Friend last name is null in message friend controller"));
        friendDTO = new FriendDTO(controllerDTO.getFriendDTOIdFriendship(), friendId, firstName, lastName, controllerDTO.getFriendDTOImagePath(), controllerDTO.getFriendDTODate());

        profileImage.setImage(getImageFromString(friendDTO.getImagePath()));
        profileImage.setFitHeight(50);
        profileImage.setFitWidth(50);
        profileImage.setPreserveRatio(true);

        friendNameLabel.setText(firstName + " " + lastName);
        toggleButton.setText(friendId + ", " + firstName + " " + lastName + ", " + friendDTO.getImagePath());
    }
}
