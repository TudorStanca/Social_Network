package controller;

import domain.dto.ControllerDTO;
import domain.dto.FriendDTO;
import domain.dto.MessageDTO;
import domain.exceptions.SetupControllerException;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Optional;

public class MessageController extends AbstractController {

    MessageDTO messageDTO;

    @FXML
    private HBox hBox;

    @FXML
    private StackPane stackPane;

    @FXML
    private TextFlow textFlow;

    @FXML
    private ToggleButton toggleButton;

    @Override
    public void setupController(ControllerDTO controllerDTO) {
        service = controllerDTO.getService();
        stage = controllerDTO.getStage();
        Optional.ofNullable(service).orElseThrow(() -> new SetupControllerException("Service is null in message friend controller"));
        Optional.ofNullable(stage).orElseThrow(() -> new SetupControllerException("Stage is null in message friend controller"));

        messageDTO = controllerDTO.getMessageDTO();
        Optional.ofNullable(messageDTO).orElseThrow(() -> new SetupControllerException("MessageDTO is null in message friend controller"));

        Text text = new Text(messageDTO.getText());
        textFlow.getChildren().add(text);
    }
}
