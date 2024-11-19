package controller;

import domain.User;
import domain.exceptions.MyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ui.MainApplication;

import java.io.IOException;

public class SignInController extends Controller {

    @FXML
    private TextField emailAddress;

    @FXML
    private PasswordField password;

    public void setTextFields(String email, String password) {
        this.emailAddress.setText(email);
        this.password.setText(password);
    }

    @FXML
    private void handleSignInButton(ActionEvent event) {
        try {
            String email = emailAddress.getText();
            String password = this.password.getText();
            User user = service.findUserByEmailPassword(email, password);

            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("user-interface.fxml"));

            Stage userInterfaceStage = initNewView(fxmlLoader, "Social Network");
            initController(fxmlLoader, userInterfaceStage);

            stage.close();
            userInterfaceStage.show();
        }
        catch (MyException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignUpHyperlink(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("sign-up.fxml"));
            changeRoot(fxmlLoader);
            initController(fxmlLoader, stage);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
