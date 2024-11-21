package controller;

import controller.mainUiPages.ObserverController;
import domain.User;
import domain.dto.ControllerDTO;
import domain.dto.UserDTO;
import domain.exceptions.SetupControllerException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ui.MainApplication;

import java.io.IOException;
import java.util.Optional;

public class UserInterfaceController extends AbstractController {

    private ObserverController currentController = null;
    private User connectedUser;

    @FXML
    private AnchorPane root;

    @FXML
    private VBox leftVBox;

    @FXML
    private Label connectedUserLabel;

    @FXML
    private Button homeButton, searchButton, messagesButton, profileButton, signOutButton;

    private void changeBorderPane(Pane newBorderPane) {
        root.getChildren().setAll(leftVBox, newBorderPane);
        AnchorPane.setTopAnchor(newBorderPane, 0.0);
        AnchorPane.setBottomAnchor(newBorderPane, 0.0);
        AnchorPane.setRightAnchor(newBorderPane, 0.0);
        newBorderPane.setPrefSize(width * 0.80, height);
    }

    @FXML
    public void initialize() {

        root.setPrefSize(width, height);
        leftVBox.setPrefSize(width * 0.20, height);

        homeButton.setPrefSize(leftVBox.getPrefWidth(), 50);
        homeButton.setGraphicTextGap(leftVBox.getPrefWidth() / 2 - 75);
        searchButton.setPrefSize(leftVBox.getPrefWidth(), 50);
        searchButton.setGraphicTextGap(leftVBox.getPrefWidth() / 2 - 75);
        messagesButton.setPrefSize(leftVBox.getPrefWidth(), 50);
        messagesButton.setGraphicTextGap(leftVBox.getPrefWidth() / 2 - 75);
        profileButton.setPrefSize(leftVBox.getPrefWidth(), 50);
        profileButton.setGraphicTextGap(leftVBox.getPrefWidth() / 2 - 75);
        signOutButton.setPrefSize(leftVBox.getPrefWidth(), 50);
        signOutButton.setGraphicTextGap(leftVBox.getPrefWidth() / 2 - 75);
    }

    @FXML
    private void handleHomeButton() {
        service.removeObserver(currentController);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("mainUiPages/home-page.fxml"));
            Pane homeBorderPane = fxmlLoader.load();

            Controller controller = fxmlLoader.getController();
            controller.setupController(new ControllerDTO(service, stage, new UserDTO(connectedUser.getId())));
            currentController = (ObserverController) controller;

            changeBorderPane(homeBorderPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchButton() {
        service.removeObserver(currentController);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("mainUiPages/search-page.fxml"));
            Pane searchBorderPane = fxmlLoader.load();

            Controller controller = fxmlLoader.getController();
            controller.setupController(new ControllerDTO(service, stage, new UserDTO(connectedUser.getId())));
            currentController = (ObserverController) controller;

            changeBorderPane(searchBorderPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMessagesButton() {
        //TODO
    }

    @FXML
    private void handleProfileButton() {
        //TODO
    }

    @FXML
    private void handleSignOutButton() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("sign-in.fxml"));

            Stage signInStage = initNewView(fxmlLoader, "Sign In");
            Controller controller = fxmlLoader.getController();
            controller.setupController(new ControllerDTO(service, signInStage));

            stage.close();
            signInStage.show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void setupController(ControllerDTO controllerDTO) {
        service = controllerDTO.getService();
        stage = controllerDTO.getStage();
        Optional.ofNullable(service).orElseThrow(() -> new SetupControllerException("Service is null in user interface controller"));
        Optional.ofNullable(stage).orElseThrow(() -> new SetupControllerException("Stage is null in user interface controller"));

        Long id = controllerDTO.getConnectedUserId();
        String firstName = controllerDTO.getConnectedUserDTOFirstName();
        String lastName = controllerDTO.getConnectedUserDTOLastName();
        String email = controllerDTO.getConnectedUserDTOEmail();
        String password = controllerDTO.getConnectedUserDTOPassword();
        Optional.ofNullable(id).orElseThrow(() -> new SetupControllerException("User id is null in user interface controller"));
        Optional.ofNullable(firstName).orElseThrow(() -> new SetupControllerException("User first name is null in user interface controller"));
        Optional.ofNullable(lastName).orElseThrow(() -> new SetupControllerException("User last name is null in user interface controller"));
        Optional.ofNullable(email).orElseThrow(() -> new SetupControllerException("User email is null in user interface controller"));
        Optional.ofNullable(password).orElseThrow(() -> new SetupControllerException("User password is null in user interface controller"));
        connectedUser = new User(firstName, lastName, email, password);
        connectedUser.setId(id);
        connectedUserLabel.setText(connectedUser.toString());

        stage.setOnCloseRequest(event -> {
            service.removeObserver(currentController);
        });
    }
}
