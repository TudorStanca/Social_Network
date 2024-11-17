package controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MessageAlert {

    public static void showMessage(Stage owner, String title, String content) {
        Alert message = new Alert(Alert.AlertType.INFORMATION);
        message.initOwner(owner);
        message.setTitle(title);
        message.setHeaderText(null);
        message.setContentText(content);
        message.showAndWait();
    }

    public static void showError(Stage owner, String error) {
        Alert message = new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Error");
        message.setHeaderText(null);
        message.setContentText(error);
        message.showAndWait();
    }
}
