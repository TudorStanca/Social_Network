package controller.mainUiPages;

import controller.AbstractController;
import controller.Controller;
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
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import ui.MainApplication;
import utils.FriendButtonType;
import utils.events.Event;
import utils.events.EventType;
import utils.events.FriendChangeEvent;
import utils.paging.Page;
import utils.paging.Pageable;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class HomePageController extends AbstractController implements ObserverController {

    private static final double borderPaneWidth = width * 0.80;

    private ObservableList<FriendDTO> friendRequestsList = FXCollections.observableArrayList();
    private ObservableList<FriendDTO> friendsList = FXCollections.observableArrayList();
    private Long connectedUserId;

    private int friendsPageSize = (int) Math.ceil(height / 100d) - 1;
    private int friendsCurrentPage = 0;
    private int friendsMaxPage = 0;

    @FXML
    private VBox leftVBox, rightVBox;

    @FXML
    private ScrollPane leftScrollPane, rightScrollPane;

    @FXML
    private VBox innerLeftVBox, innerRightVBox;

    @FXML
    private Button previousFriendsButton, nextFriendsButton;

    @FXML
    public void initialize() {
        leftVBox.setPrefWidth(borderPaneWidth * 0.50);
        rightVBox.setPrefWidth(borderPaneWidth * 0.50);
        leftScrollPane.setPrefHeight(height);
        leftScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rightScrollPane.setPrefHeight(height);
        rightScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rightScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        friendRequestsList.addListener((ListChangeListener<FriendDTO>) change -> {
            loadFriends(friendRequestsList, innerRightVBox, FriendButtonType.ACCEPT);
        });

        friendsList.addListener((ListChangeListener<FriendDTO>) change -> {
            loadFriends(friendsList, innerLeftVBox, FriendButtonType.DELETE);
        });

        leftScrollPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.N && friendsCurrentPage + 1 != friendsMaxPage) {
                friendsCurrentPage++;
                setFriendsList();
            }
            else if(event.getCode() == KeyCode.B && friendsCurrentPage != 0) {
                friendsCurrentPage--;
                setFriendsList();
            }
        });
    }

    @FXML
    private void handlePreviousFriendsButton(ActionEvent event){
        friendsCurrentPage--;
        setFriendsList();
    }

    @FXML
    private void handleNextFriendsButton(ActionEvent event){
        friendsCurrentPage++;
        setFriendsList();
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
        Page<FriendDTO> page = service.findUserFriends(new Pageable(friendsCurrentPage, friendsPageSize), connectedUserId);
        previousFriendsButton.setDisable(false);
        nextFriendsButton.setDisable(false);
        if(StreamSupport.stream(page.getElementsOnPage().spliterator(), false).findAny().isEmpty() && friendsCurrentPage + 1 == friendsMaxPage) {
            friendsCurrentPage--;
            page = service.findUserFriends(new Pageable(friendsCurrentPage, friendsPageSize), connectedUserId);
        }
        friendsMaxPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / (double) friendsPageSize);
        if(friendsCurrentPage == 0){
            previousFriendsButton.setDisable(true);
        }
        if(friendsCurrentPage + 1 == friendsMaxPage){
            nextFriendsButton.setDisable(true);
        }
        friendsList.setAll((StreamSupport.stream(page.getElementsOnPage().spliterator(), false).toList()));
    }

    @Override
    public void update(Event e) {
        if (e instanceof FriendChangeEvent) {
            FriendChangeEvent event = (FriendChangeEvent) e;
            if (event.getEventType() == EventType.ACCEPT_REQUEST || event.getEventType() == EventType.DELETE_REQUEST) {
                setFriendsList();
            }
            setFriendRequestsList();
        }
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
