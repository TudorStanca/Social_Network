package controller.mainUiPages;

import controller.AbstractController;
import controller.Controller;
import controller.UserInterfaceController;
import domain.dto.ControllerDTO;
import domain.dto.FriendDTO;
import domain.dto.UserDTO;
import domain.exceptions.SetupControllerException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import ui.MainApplication;
import utils.FriendButtonType;
import utils.events.Event;
import utils.events.EventType;
import utils.events.FriendChangeEvent;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class HomePageController extends AbstractController implements ObserverController {

    private static final double borderPaneWidth = width * 0.80;

    private ObservableList<FriendDTO> friendRequestsList = FXCollections.observableArrayList();
    private ObservableList<FriendDTO> friendsList = FXCollections.observableArrayList();
    private Long connectedUserId;

    @FXML
    private VBox leftVBox, rightVBox;

    @FXML
    private ScrollPane leftScrollPane, rightScrollPane;

    @FXML
    private VBox innerLeftVBox, innerRightVBox;

    @FXML
    public void initialize() {
        leftVBox.setPrefWidth(borderPaneWidth * 0.50);
        rightVBox.setPrefWidth(borderPaneWidth * 0.50);
        leftScrollPane.setPrefHeight(height);
        rightScrollPane.setPrefHeight(height);

        friendRequestsList.addListener((ListChangeListener<FriendDTO>) change -> {
            loadFriends(friendRequestsList, innerRightVBox, FriendButtonType.ACCEPT);
        });

        friendsList.addListener((ListChangeListener<FriendDTO>) change -> {
            loadFriends(friendsList, innerLeftVBox, FriendButtonType.DELETE);
        });
    }

    private void loadFriends(List<FriendDTO> lst, VBox pane, FriendButtonType friendButtonType) {
        pane.getChildren().clear();
        for (FriendDTO friend : lst) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("friend.fxml"));
                Node friendUi = fxmlLoader.load();
                Controller controller = fxmlLoader.getController();
                controller.setupController(new ControllerDTO(service, stage, new UserDTO(connectedUserId), friend, friendButtonType));
                pane.getChildren().add(friendUi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setFriendRequestsList() {
        friendRequestsList.setAll((StreamSupport.stream(service.findPendingRecievingFriendRequests(connectedUserId).spliterator(), false).toList()));
    }

    private void setFriendsList() {
        friendsList.setAll((StreamSupport.stream(service.findUserFriends(connectedUserId).spliterator(), false).toList()));
    }

    @Override
    public void update(Event e) {
        FriendChangeEvent event = (FriendChangeEvent) e;
        if (event.getEventType() == EventType.ACCEPT_REQUEST || event.getEventType() == EventType.DELETE_REQUEST) {
            setFriendsList();
        }
        setFriendRequestsList();
    }

    @Override
    public void setupController(ControllerDTO controllerDTO) {
        service = controllerDTO.getService();
        stage = controllerDTO.getStage();
        Optional.ofNullable(service).orElseThrow(() -> new SetupControllerException("Service is null in home page controller"));
        Optional.ofNullable(stage).orElseThrow(() -> new SetupControllerException("Stage is null in home page controller"));

        service.addObserver(this);

        connectedUserId = controllerDTO.getConnectedUserId();
        Optional.ofNullable(connectedUserId).orElseThrow(() -> new SetupControllerException("ID is null in home page controller"));

        setFriendRequestsList();
        setFriendsList();
    }
}
