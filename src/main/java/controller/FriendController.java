package controller;

import domain.User;
import domain.exceptions.MyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FriendController extends Controller {

    private User friend;
    private Long connectedUserId;

    @FXML
    private Label friendName;

    public void setFriendName(User friend) {
        this.friend = friend;
        friendName.setText(friend.getFirstName() + " " + friend.getLastName());
    }

    public void setConnectedUserId(Long connectedUserId) {
        this.connectedUserId = connectedUserId;
    }

    @FXML
    private void handleAddButton(ActionEvent event) {
        try {
            service.addFriend(connectedUserId, friend.getId());
        }
        catch (MyException e){
            MessageAlert.showError(stage, e.getMessage());
        }
    }
}
