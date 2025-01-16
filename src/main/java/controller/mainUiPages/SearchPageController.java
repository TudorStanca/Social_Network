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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ui.MainApplication;
import utils.FriendButtonType;
import utils.events.Event;
import utils.events.EventType;
import utils.events.FriendChangeEvent;
import utils.events.UserChangeEvent;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class SearchPageController extends AbstractController implements ObserverController {

    private ObservableList<FriendDTO> candidateFriendsList = FXCollections.observableArrayList();
    private Long connectedUserId;

    @FXML
    private VBox searchVBox;

    @FXML
    private ScrollPane searchScrollPane;

    @FXML
    private TextField searchTextField;

    @FXML
    public void initialize() {
        searchScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        searchScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        searchTextField.textProperty().addListener(o -> handleFilter());
        candidateFriendsList.addListener((ListChangeListener<FriendDTO>) change -> {
            loadCandidateFriends(candidateFriendsList);
        });
    }

    private void setCandidateFriendsList() {
        candidateFriendsList.setAll((StreamSupport.stream(service.findUserCandidateFriends(connectedUserId).spliterator(), false).toList()));
    }

    private void handleFilter() {
        Predicate<FriendDTO> p1 = friend -> (friend.getFirstName() + " " + friend.getLastName()).startsWith(searchTextField.getText());
        loadCandidateFriends(candidateFriendsList.filtered(p1));
    }

    private void loadCandidateFriends(List<FriendDTO> lst) {
        searchVBox.getChildren().clear();
        for (FriendDTO friend : lst) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("friend.fxml"));
                Node friendUi = fxmlLoader.load();
                Controller controller = fxmlLoader.getController();
                controller.setupController(new ControllerDTO(service, stage, new UserDTO(connectedUserId), friend, FriendButtonType.ADD));
                searchVBox.getChildren().add(friendUi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(Event e) {
        if (e instanceof FriendChangeEvent event) {
            if (event.getEventType() == EventType.CREATE_REQUEST || event.getEventType() == EventType.DELETE_REQUEST) {
                setCandidateFriendsList();
                searchTextField.clear();
            }
        }
        if (e instanceof UserChangeEvent event){
            if(event.getEventType() == EventType.ADD_USER || event.getEventType() == EventType.DELETE_USER || event.getEventType() == EventType.UPDATE_USER){
                setCandidateFriendsList();
                searchTextField.clear();
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

        setCandidateFriendsList();
    }
}
