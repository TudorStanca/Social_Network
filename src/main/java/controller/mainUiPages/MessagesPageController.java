package controller.mainUiPages;

import controller.AbstractController;
import controller.Controller;
import domain.dto.ControllerDTO;
import domain.dto.FriendDTO;
import domain.dto.MessageDTO;
import domain.dto.UserDTO;
import domain.exceptions.SetupControllerException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.MainApplication;
import utils.FriendButtonType;
import utils.events.Event;
import utils.events.EventType;
import utils.events.FriendChangeEvent;

import java.io.IOException;
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

        messageTextField.setPrefWidth(width * 0.80 * 0.70 - 100);

        searchTextField.textProperty().addListener(o -> handleFilter());
        friendsList.addListener((ListChangeListener<FriendDTO>) change -> {
            loadFriends(friendsList);
        });

        messageList.addListener((ListChangeListener<MessageDTO>) change -> {
            loadMessages(messageList);
        });
    }

    private void setFriendsList() {
        friendsList.setAll((StreamSupport.stream(service.findUserFriends(connectedUserId).spliterator(), false).toList()));
    }

    private void setMessageList() {
        messageList.setAll(StreamSupport.stream(service.findMessages(connectedUserId, selectedFriendId).spliterator(), false).toList());
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
                controller.setupController(new ControllerDTO(service, stage, msg));
                messagesVBox.getChildren().add(messageUi);
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
        FriendChangeEvent event = (FriendChangeEvent) e;
        if (event.getEventType() == EventType.ACCEPT_REQUEST || event.getEventType() == EventType.DELETE_REQUEST) {
            setFriendsList();
        }
    }
}
