package controller;

import domain.User;
import domain.exceptions.MyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ui.MainApplication;

import java.io.IOException;

public class SignUpController extends Controller {

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField emailAddress;

    @FXML
    private PasswordField password;

    @FXML
    private void handleSignUpButton(ActionEvent event) {
        String firstName = this.firstName.getText();
        String lastName = this.lastName.getText();
        String email = this.emailAddress.getText();
        String password = this.password.getText();
        try {
            User user = service.addUser(firstName, lastName, email, password);
            MessageAlert.showMessage(stage, "Account create confirmation", "The new account: " + user.toString() + " has been successfully created");

            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("sign-in.fxml"));
            changeRoot(fxmlLoader);

            SignInController signInController = (SignInController) initController(fxmlLoader, stage);
            signInController.setTextFields(email, password);
        } catch (MyException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignInHyperlink(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("sign-in.fxml"));
            changeRoot(fxmlLoader);
            initController(fxmlLoader, stage);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
