package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    protected static final double width = Screen.getPrimary().getBounds().getWidth() * 0.75;
    protected static final double height = Screen.getPrimary().getBounds().getHeight() * 0.75;

    protected Service service;
    protected Stage stage;

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
