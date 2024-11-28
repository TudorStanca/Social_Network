package controller;

import domain.dto.ControllerDTO;
import domain.dto.MessageDTO;
import domain.exceptions.SetupControllerException;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import utils.Constants;
import utils.MessageType;

import java.util.Optional;

public class MessageController extends AbstractController {

    MessageDTO messageDTO;

    @FXML
    private HBox hBox, messageHBox;

    @FXML
    private StackPane stackPane;

    @FXML
    private TextFlow textFlow;

    @FXML
    private Label dateLabel;

    @FXML
    private ToggleButton toggleButton;

    @FXML
    public void initialize() {
        textFlow.setPadding(new Insets(5,10,5,10));
        toggleButton.prefWidthProperty().bind(messageHBox.widthProperty());
        hBox.setPrefWidth(width * 0.80 * 0.70);
    }

    public void setAligment(Pos value){
        hBox.setAlignment(value);
    }

    public void setBackgroundColor(String color){
        String style = "-fx-background-radius: 10px; -fx-background-color: " + color;
        messageHBox.setStyle(style);
    }

    @Override
    public void setupController(ControllerDTO controllerDTO) {
        service = controllerDTO.getService();
        stage = controllerDTO.getStage();
        Optional.ofNullable(service).orElseThrow(() -> new SetupControllerException("Service is null in message friend controller"));
        Optional.ofNullable(stage).orElseThrow(() -> new SetupControllerException("Stage is null in message friend controller"));

        messageDTO = controllerDTO.getMessageDTO();
        Optional.ofNullable(messageDTO).orElseThrow(() -> new SetupControllerException("MessageDTO is null in message friend controller"));

        Text text;
        if(controllerDTO.getMessageType() == MessageType.MESSAGE) {
            text = new Text(messageDTO.getText());
            dateLabel.setText(messageDTO.getDate().format(Constants.DATE_FORMATTER));
        }
        else {
            text = new Text(messageDTO.getTextReply());
            dateLabel.setText(messageDTO.getDateReply().format(Constants.DATE_FORMATTER));
        }
        text.setFill(Color.WHITE);
        textFlow.setMaxWidth(width * 0.80 * 0.70 * 0.60);
        textFlow.getChildren().add(text);
    }
}
