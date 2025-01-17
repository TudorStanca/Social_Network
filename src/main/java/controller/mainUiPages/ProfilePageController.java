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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ui.MainApplication;
import utils.FriendButtonType;
import utils.events.Event;
import utils.events.EventType;
import utils.events.FriendChangeEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class ProfilePageController extends AbstractController implements ObserverController {

    private ObservableList<FriendDTO> friendsList = FXCollections.observableArrayList();
    private long connectedUserId;

    @FXML
    private Label numberOfFriendsLabel, numberOfFriendRequestsLabel, friendsStatusLabel;

    @FXML
    private VBox friendsVBox, leftVBox;

    @FXML
    private ScrollPane friendsScrollPane;

    @FXML
    private StackPane stackPane;


    @FXML
    public void initialize(){
        stackPane.setPrefHeight(height);
        friendsStatusLabel.setMouseTransparent(true);

        friendsList.addListener((ListChangeListener<FriendDTO>) change -> {
            loadFriends(friendsList, friendsVBox);
        });
    }

    private void loadFriends(List<FriendDTO> lst, VBox pane) {
        pane.getChildren().clear();
        for (FriendDTO friend : lst) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("friend.fxml"));
                Node friendUi = fxmlLoader.load();
                Controller controller = fxmlLoader.getController();
                controller.setupController(new ControllerDTO(service, stage, new UserDTO(connectedUserId), friend, FriendButtonType.NOTHING));
                pane.getChildren().add(friendUi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setFriendsList() {
        friendsList.setAll((StreamSupport.stream(service.findUserFriends(connectedUserId).spliterator(), false).toList()));
        friendsStatusLabel.setVisible(friendsList.isEmpty());
    }

    @FXML
    private void handleChangeImage (ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a profile image");
        fileChooser.setInitialDirectory(new File("C:\\"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            service.updateUserProfileImage(connectedUserId, file);
        }
    }

    @FXML
    private void handleDeleteUser(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            service.deleteUser(connectedUserId);
            stage.close();
        }
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("sign-in.fxml"));
//
//            Stage signInStage = initNewView(fxmlLoader, "Sign In");
//            Controller controller = fxmlLoader.getController();
//            controller.setupController(new ControllerDTO(service, signInStage));
//
//            stage.close();
//            signInStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void setNumberOfFriendsLabel(){
        numberOfFriendsLabel.setText("Number of friends: " + service.countFriends(connectedUserId));
    }

    private void setNumberOfFriendRequestsLabel(){
        numberOfFriendRequestsLabel.setText("Number of friend requests: " + service.countFriendRequests(connectedUserId));
    }

    @Override
    public void setupController(ControllerDTO controllerDTO) {
        service = controllerDTO.getService(); Optional.ofNullable(service).orElseThrow(() -> new SetupControllerException("Service is null in profile page controller"));
        stage = controllerDTO.getStage(); Optional.ofNullable(stage).orElseThrow(() -> new SetupControllerException("Stage is null in profile page controller"));
        connectedUserId = controllerDTO.getConnectedUserId(); Optional.ofNullable(stage).orElseThrow(() -> new SetupControllerException("Connected user id is null in profile page controller"));

        setNumberOfFriendsLabel();
        setNumberOfFriendRequestsLabel();

        setFriendsList();
    }

    @Override
    public void update(Event e) {
        if(e instanceof FriendChangeEvent event){
            if(event.getEventType().equals(EventType.CREATE_REQUEST)){
                setNumberOfFriendRequestsLabel();
            }
            if(event.getEventType().equals(EventType.ACCEPT_REQUEST)){
                setNumberOfFriendsLabel();
                setNumberOfFriendRequestsLabel();
                setFriendsList();
            }
            if(event.getEventType().equals(EventType.DELETE_REQUEST)){
                setNumberOfFriendsLabel();
                setFriendsList();
            }
        }
    }
}
