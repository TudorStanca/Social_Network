package controller;

import controller.mainUiPages.ObserverController;
import domain.dto.ControllerDTO;
import domain.dto.FriendDTO;
import domain.dto.UserDTO;
import domain.exceptions.MyException;
import domain.exceptions.NoFriendSelectedException;
import domain.exceptions.SetupControllerException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ui.MainApplication;
import utils.events.Event;
import utils.events.EventType;
import utils.events.FriendChangeEvent;
import utils.events.UserChangeEvent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class BroadcastMessageController extends AbstractController implements ObserverController {

    private ObservableList<FriendDTO> friendsList = FXCollections.observableArrayList();
    private List<Long> selectedFriendIds = new ArrayList<>();
    private Long connectedUserId;

    @FXML
    private VBox root, friendsVBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField textField;

    @FXML
    private Button sendButton;

    @FXML
    public void initialize() {
        root.setPrefSize(width * 0.30, height * 0.50);
        scrollPane.setPrefHeight(height * 0.50 - 50);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        friendsVBox.prefWidthProperty().bind(scrollPane.widthProperty());
        textField.setPrefWidth(width * 0.30 - 100);

        friendsList.addListener((ListChangeListener<FriendDTO>) change -> {
            loadFriends(friendsList);
        });
    }

    @FXML
    private void handleSendButton(ActionEvent event) {
        try {
            if (selectedFriendIds.isEmpty()) {
                throw new NoFriendSelectedException("No friends selected");
            }

            String message = textField.getText();
            service.addMessage(connectedUserId, selectedFriendIds, message, LocalDateTime.now(), null);
            textField.clear();

        } catch (MyException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
    }

    private void setFriendsList() {
        friendsList.setAll((StreamSupport.stream(service.findUserFriends(connectedUserId).spliterator(), false).toList()));
    }

    private void loadFriends(List<FriendDTO> lst) {
        friendsVBox.getChildren().clear();
        for (FriendDTO friend : lst) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("messages-friend.fxml"));
                Node friendUi = fxmlLoader.load();
                ((AnchorPane) friendUi).prefWidthProperty().bind(friendsVBox.widthProperty());

                Controller controller = fxmlLoader.getController();
                controller.setupController(new ControllerDTO(service, stage, new UserDTO(connectedUserId), friend));
                ToggleButton toggleButton = ((MessagesFriendController) controller).getToggleButton();
                toggleButton.selectedProperty().addListener((_, _, newValue) -> {
                    if (newValue) {
                        selectedFriendIds.add(Long.parseLong(toggleButton.getText().split(", ")[0]));
                    } else {
                        selectedFriendIds.remove(Long.parseLong(toggleButton.getText().split(", ")[0]));
                    }
                });

                friendsVBox.getChildren().add(friendUi);
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

        stage.setOnCloseRequest(event -> {
            service.removeObserver(this);
        });

        setFriendsList();
    }

    @Override
    public void update(Event e) {
        if (e instanceof FriendChangeEvent event) {
            if (event.getEventType() == EventType.ACCEPT_REQUEST || event.getEventType() == EventType.DELETE_REQUEST) {
                setFriendsList();
            }
        }
        if (e instanceof UserChangeEvent event){
            if (event.getEventType() == EventType.UPDATE_USER){
                setFriendsList();
            }
        }
    }
}
