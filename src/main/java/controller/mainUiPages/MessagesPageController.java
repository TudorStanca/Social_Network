package controller.mainUiPages;

import controller.AbstractController;
import controller.Controller;
import controller.MessageAlert;
import controller.MessageController;
import domain.dto.ControllerDTO;
import domain.dto.FriendDTO;
import domain.dto.MessageDTO;
import domain.dto.UserDTO;
import domain.exceptions.MyException;
import domain.exceptions.SetupControllerException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.MainApplication;
import utils.FriendButtonType;
import utils.MessageType;
import utils.events.Event;
import utils.events.EventType;
import utils.events.FriendChangeEvent;
import utils.events.MessageChangeEvent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class MessagesPageController extends AbstractController implements ObserverController{

    private ObservableList<FriendDTO> friendsList = FXCollections.observableArrayList();
    private ObservableList<MessageDTO> messageList = FXCollections.observableArrayList();
    private Long connectedUserId;
    private Long selectedFriendId;

    @FXML
    private BorderPane root;

    @FXML
    private VBox leftVBox, friendsListVBox, rightVBox, messagesVBox;

    @FXML
    private HBox bottomHBox;

    @FXML
    private ScrollPane leftScrollPane, rightScrollPane;

    @FXML
    private Label userNameLabel;

    @FXML
    private TextField searchTextField, messageTextField;

    @FXML
    private ImageView friendIcon;

    private ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    public void initialize() {
        leftVBox.setPrefSize(width * 0.80 * 0.30, height);
        leftScrollPane.setPrefHeight(height);
        rightVBox.setPrefSize(width * 0.80 * 0.70, height);
        rightScrollPane.setPrefHeight(height - 177);
        messagesVBox.setPrefWidth(width * 0.80 * 0.70 - 23);

        messageTextField.setPrefWidth(width * 0.80 * 0.70 - 100);

        searchTextField.textProperty().addListener(o -> handleFilter());
        friendsList.addListener((ListChangeListener<FriendDTO>) change -> {
            loadFriends(friendsList);
        });

        messageList.addListener((ListChangeListener<MessageDTO>) change -> {
            loadMessages(messageList);
        });
    }

    @FXML
    private void handleSendButton(ActionEvent event) {
        try {
            String message = messageTextField.getText();
            service.addMessage(connectedUserId, Arrays.asList(selectedFriendId), message, LocalDateTime.now(), null);
            messageTextField.clear();
        }
        catch (MyException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
    }

    private void setFriendsList() {
        friendsList.setAll((StreamSupport.stream(service.findUserFriends(connectedUserId).spliterator(), false).toList()));
    }

    private void setMessageList() {
        messageList.setAll(StreamSupport.stream(service.findMessages(connectedUserId, selectedFriendId).spliterator(), false).toList());
        Platform.runLater(() -> rightScrollPane.setVvalue(1.0));
    }

    private void handleFilter() {
        Predicate<FriendDTO> p1 = friend -> (friend.getFirstName() + " " + friend.getLastName()).startsWith(searchTextField.getText());
        loadFriends(friendsList.filtered(p1));
    }

    private void loadMessages(List<MessageDTO> lst){
        messagesVBox.getChildren().clear();
        for (MessageDTO msg : lst) {
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("message.fxml"));
                Node messageUi = fxmlLoader.load();

                Controller controller = fxmlLoader.getController();
                controller.setupController(new ControllerDTO(service, stage, msg, MessageType.MESSAGE));

                VBox vBoxContainingMessageAndReply = new VBox();
                HBox hBoxContainingVBoxMessageAndReply = new HBox(vBoxContainingMessageAndReply);

                if(msg.getIdFrom().equals(connectedUserId)){
                    ((MessageController) controller).setAligment(Pos.CENTER_RIGHT);
                    ((MessageController) controller).setBackgroundColor("#005c4b");
                }
                else{
                    ((MessageController) controller).setAligment(Pos.CENTER_LEFT);
                    ((MessageController) controller).setBackgroundColor("#363636");
                }

                if (msg.getIdMessageReply() != null){
                    FXMLLoader fxmlLoaderReply = new FXMLLoader(MainApplication.class.getResource("message.fxml"));
                    Node messageReplyUi = fxmlLoaderReply.load();

                    Controller controllerReply = fxmlLoaderReply.getController();
                    controllerReply.setupController(new ControllerDTO(service, stage, msg, MessageType.REPLY));

                    vBoxContainingMessageAndReply.getChildren().add(messageReplyUi);
                    vBoxContainingMessageAndReply.getChildren().add(messageUi);

                    if(msg.getIdFrom().equals(connectedUserId)){
                        ((MessageController) controllerReply).setAligment(Pos.CENTER_RIGHT);
                        VBox.setMargin(messageReplyUi, new Insets(0, 10, 0, 0));
                        ((MessageController) controllerReply).setBackgroundColor("#ff0000");
                    }
                    else{
                        ((MessageController) controller).setAligment(Pos.CENTER_LEFT);
                        VBox.setMargin(messageReplyUi, new Insets(0, 0, 0, 10));
                        ((MessageController) controllerReply).setBackgroundColor("#ff0000");
                    }

                    messagesVBox.getChildren().add(hBoxContainingVBoxMessageAndReply);
                }
                else {
                    messagesVBox.getChildren().add(messageUi);
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void loadFriends(List<FriendDTO> lst) {
        friendsListVBox.getChildren().clear();
        for (FriendDTO friend : lst) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("messages-friend.fxml"));
                Node friendUi = fxmlLoader.load();
                ((AnchorPane) friendUi).setPrefWidth(width * 0.80 * 0.30 - 2);

                ((ToggleButton) ((AnchorPane) friendUi).getChildren().get(2)).setToggleGroup(toggleGroup);
                toggleGroup.selectedToggleProperty().addListener((_, oldValue, newValue) -> {
                    if (newValue != null) { // if a new button has been selected
                        String[] splitString = ((ToggleButton) newValue).getText().split(", ");
                        selectedFriendId = Long.parseLong(splitString[0]);
                        userNameLabel.setText(splitString[1]);
                        friendIcon.setVisible(true);
                        userNameLabel.setVisible(true);
                        setMessageList();
                    }
                    if(oldValue != null && newValue != null) { // if a new button has been selected and it's different from previous selected one
                        ((ToggleButton) oldValue).getParent().setStyle("-fx-background-color: #eb1c6f");
                    }
                    if (oldValue != null && newValue == null) { // if the same button is pressed
                        oldValue.setSelected(true);
                    }
                });

                Controller controller = fxmlLoader.getController();
                controller.setupController(new ControllerDTO(service, stage, new UserDTO(connectedUserId), friend));
                friendsListVBox.getChildren().add(friendUi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setupController(ControllerDTO controllerDTO) {
        service = controllerDTO.getService();
        stage = controllerDTO.getStage();
        Optional.ofNullable(service).orElseThrow(() -> new SetupControllerException("Service is null in search page controller"));
        Optional.ofNullable(stage).orElseThrow(() -> new SetupControllerException("Stage is null in search page controller"));

        service.addObserver(this);

        connectedUserId = controllerDTO.getConnectedUserId();
        Optional.ofNullable(connectedUserId).orElseThrow(() -> new SetupControllerException("ID is null in search page controller"));

        friendIcon.setVisible(false);
        userNameLabel.setVisible(false);

        setFriendsList();
    }

    @Override
    public void update(Event e) {
        if(e instanceof FriendChangeEvent) {
            FriendChangeEvent event = (FriendChangeEvent) e;
            if (event.getEventType() == EventType.ACCEPT_REQUEST || event.getEventType() == EventType.DELETE_REQUEST) {
                setFriendsList();
            }
        }
        else if(e instanceof MessageChangeEvent){
            MessageChangeEvent event = (MessageChangeEvent) e;
            if (event.getType() == EventType.CREATE_MESSAGE) {
                setMessageList();
            }
        }
    }
}
