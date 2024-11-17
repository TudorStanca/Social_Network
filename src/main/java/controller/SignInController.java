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

public class SignInController extends Controller {

    @FXML
    private TextField emailAddress;

    @FXML
    private PasswordField password;

    public void setTextFields(String email, String password) {
        this.emailAddress.setText(email);
        this.password.setText(password);
    }

    @Override
    protected Stage initNewView(FXMLLoader fxmlLoader, String title){
        Stage stage = super.initNewView(fxmlLoader, title);
        stage.setResizable(false);
        return stage;
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
    }

    @FXML
    private void handleSignUpHyperlink(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("sign-up.fxml"));

        Stage signUpStage = initNewView(fxmlLoader, "Sign Up");
        initController(fxmlLoader, signUpStage);

        stage.close();
        signUpStage.show();
    }
}
