package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;

public abstract class Controller {

    protected Service service;
    protected Stage stage;

    public void setService(Service service) {
        this.service = service;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    protected Stage initNewView(FXMLLoader fxmlLoader, String title) {
        try {
            Pane root = fxmlLoader.load();
            Scene signInScene = new Scene(root);
            Stage stage = new Stage();

            stage.setTitle(title);
            stage.setScene(signInScene);
            root.requestFocus();

            return stage;
        }
        catch(IOException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    protected Controller initController(FXMLLoader fxmlLoader, Stage stage) {
        Controller controller = fxmlLoader.getController();
        controller.setService(service);
        controller.setStage(stage);
        return controller;
    }
}
