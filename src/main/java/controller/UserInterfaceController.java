package controller;

import controller.mainUiPages.HomePageController;
import controller.mainUiPages.ObserverController;
import controller.mainUiPages.SearchPageController;
import domain.User;
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

public class UserInterfaceController extends Controller {

    private static final double width = Screen.getPrimary().getBounds().getWidth() * 0.75;
    private static final double height = Screen.getPrimary().getBounds().getHeight() * 0.75;
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

    public void setConnectedUser(User connectedUser) {
        this.connectedUser = connectedUser;
        connectedUserLabel.setText(connectedUser.toString());
    }

    public void setupStageOnCloseRequestEventHandler(){
        stage.setOnCloseRequest(event -> {
            service.removeObserver(currentController);
        });
    }

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
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("mainUiPages/home-page.fxml"));
            Pane homeBorderPane = fxmlLoader.load();
            initController(fxmlLoader, stage);
            changeBorderPane(homeBorderPane);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            Controller controller = initController(fxmlLoader, stage);
            service.addObserver((HomePageController) controller);
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
            Controller controller = initController(fxmlLoader, stage);
            ((SearchPageController) controller).setConnectedUserId(connectedUser.getId());
            service.addObserver((SearchPageController) controller);
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
            initController(fxmlLoader, signInStage);

            stage.close();
            signInStage.show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
