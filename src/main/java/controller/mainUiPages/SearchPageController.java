package controller.mainUiPages;

import controller.Controller;
import controller.FriendController;
import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ui.MainApplication;
import utils.events.Event;
import utils.events.FriendRequestEvent;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SearchPageController extends Controller implements ObserverController {

    private ObservableList<User> candidateFriendsOnSearchButtonPage = FXCollections.observableArrayList();
    private Long connectedUserId;

    @FXML
    private VBox searchVBox;

    @FXML
    private ScrollPane searchScrollPane;

    @FXML
    private TextField searchTextField;

    @FXML
    public void initialize() {
        searchTextField.textProperty().addListener(o -> handleFilter());
    }

    public void setConnectedUserId(Long connectedUserId) {
        this.connectedUserId = connectedUserId;
        candidateFriendsOnSearchButtonPage.setAll(StreamSupport.stream(service.findUserCandidateFriends(connectedUserId).spliterator(), false).toList());
        loadCandidateFriendsOnSearchButtonPage(candidateFriendsOnSearchButtonPage);
    }

    private void handleFilter(){
        Predicate<User> p1 = user -> (user.getFirstName() + " " + user.getLastName()).startsWith(searchTextField.getText());
        loadCandidateFriendsOnSearchButtonPage(candidateFriendsOnSearchButtonPage.stream().filter(p1).collect(Collectors.toList()));
    }

    private void loadCandidateFriendsOnSearchButtonPage(List<User> lst) {
        searchVBox.getChildren().clear();
        for (User user : lst) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("friend.fxml"));
                Node friendUi = fxmlLoader.load();
                Controller controller = initController(fxmlLoader, stage);
                ((FriendController) controller).setConnectedUserId(connectedUserId);
                ((FriendController) controller).setFriendName(user);
                searchVBox.getChildren().add(friendUi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(Event e) {
        FriendRequestEvent friendRequestEvent = (FriendRequestEvent) e;
        candidateFriendsOnSearchButtonPage.removeIf(user -> user.getId().equals(friendRequestEvent.getId()));
        loadCandidateFriendsOnSearchButtonPage(candidateFriendsOnSearchButtonPage);
    }
}
