package controller.mainUiPages;

import controller.AbstractController;
import domain.dto.ControllerDTO;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import utils.events.Event;

public class HomePageController extends AbstractController implements ObserverController {

    private static final double borderPaneWidth = width * 0.80;

    @FXML
    private VBox leftVBox, rightVBox;

    @FXML
    private ScrollPane leftScrollPane, rightScrollPane;

    @FXML
    private VBox innerLeftVBox, innerRightVBox;

    @FXML
    public void initialize() {
        leftVBox.setPrefWidth(borderPaneWidth * 0.50);
        rightVBox.setPrefWidth(borderPaneWidth * 0.50);
        leftScrollPane.setPrefHeight(height);
        rightScrollPane.setPrefHeight(height);
    }

    @Override
    public void update(Event event) {

    }

    @Override
    public void setupController(ControllerDTO controllerDTO) {

    }
}
