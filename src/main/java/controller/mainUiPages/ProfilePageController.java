package controller.mainUiPages;

import controller.AbstractController;
import domain.dto.ControllerDTO;
import domain.exceptions.SetupControllerException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import utils.events.Event;

import java.io.*;
import java.util.Optional;

public class ProfilePageController extends AbstractController implements ObserverController {

    private long connectedUserId;

    @FXML
    private void handleChangePassword (ActionEvent event){

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

    @Override
    public void setupController(ControllerDTO controllerDTO) {
        service = controllerDTO.getService(); Optional.ofNullable(service).orElseThrow(() -> new SetupControllerException("Service is null in profile page controller"));
        stage = controllerDTO.getStage(); Optional.ofNullable(stage).orElseThrow(() -> new SetupControllerException("Stage is null in profile page controller"));
        connectedUserId = controllerDTO.getConnectedUserId(); Optional.ofNullable(stage).orElseThrow(() -> new SetupControllerException("Connected user id is null in profile page controller"));
    }

    @Override
    public void update(Event event) {

    }
}
