package controller.mainUiPages;

import controller.AbstractController;
import domain.dto.ControllerDTO;
import domain.exceptions.SetupControllerException;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import utils.events.Event;

import java.util.Optional;

public class MessagesPageController extends AbstractController implements ObserverController{

    private Long connectedUserId;

    @FXML
    public void initialize() {

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
    }

    @Override
    public void update(Event event) {

    }
}
