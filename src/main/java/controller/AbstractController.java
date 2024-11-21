package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    protected Service service;
    protected Stage stage;

    public void setService(Service service) {
        this.service = service;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    protected Stage initNewView(FXMLLoader fxmlLoader, String title) throws IOException {
        Pane root = fxmlLoader.load();
        Scene signInScene = new Scene(root);
        Stage stage = new Stage();

        stage.setTitle(title);
        stage.setScene(signInScene);
        root.requestFocus();

        return stage;
    }

    protected void changeRoot(FXMLLoader fxmlLoader) throws IOException {
        Pane root = fxmlLoader.load();
        stage.getScene().setRoot(root);
        root.requestFocus();
    }
}
