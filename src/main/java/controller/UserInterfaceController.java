package controller;

import com.sun.tools.javac.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

public class UserInterfaceController extends Controller {

    private static final double width = Screen.getPrimary().getBounds().getWidth() * 0.75;
    private static final double height = Screen.getPrimary().getBounds().getHeight() * 0.75;

    @FXML
    private AnchorPane root;

    @FXML
    private VBox leftVBox;

    @FXML
    private BorderPane homeBorderPane, searchBorderPane;

    @FXML
    private Button homeButton, searchButton, messagesButton, profileButton, signOutButton;

    private void changeBorderPane(BorderPane newBorderPane) {
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
        homeBorderPane.setPrefSize(width * 0.80, height);

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
        changeBorderPane(homeBorderPane);
    }

    @FXML
    private void handleSearchButton() {
        changeBorderPane(searchBorderPane);
    }

}
