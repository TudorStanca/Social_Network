package controller;

import domain.User;
import domain.exceptions.MyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ui.MainApplication;

public class SignUpController extends Controller {

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField emailAddress;

    @FXML
    private PasswordField password;

    @Override
    protected Stage initNewView(FXMLLoader fxmlLoader, String title){
        Stage stage = super.initNewView(fxmlLoader, title);
        stage.setResizable(false);
        return stage;
    }

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

            Stage signInStage = initNewView(fxmlLoader, "Sign In");

            SignInController signInController = (SignInController) initController(fxmlLoader, signInStage);
            signInController.setTextFields(email, password);

            stage.close();
            signInStage.show();
        } catch (MyException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
    }

    @FXML
    private void handleSignInHyperlink(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("sign-in.fxml"));
        Stage signInStage = initNewView(fxmlLoader, "Sign In");

        initController(fxmlLoader, signInStage);

        stage.close();
        signInStage.show();
    }
}
