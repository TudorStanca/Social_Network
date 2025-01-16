package controller;

import domain.User;
import domain.dto.ControllerDTO;
import domain.dto.UserDTO;
import domain.exceptions.MyException;
import domain.exceptions.SetupControllerException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ui.MainApplication;

import java.io.IOException;
import java.util.Optional;

public class SignInController extends AbstractController {

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

            Controller controller = fxmlLoader.getController();
            controller.setupController(new ControllerDTO(service, userInterfaceStage, new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getImagePath())));

            //stage.close();
            userInterfaceStage.show();
        } catch (MyException e) {
            MessageAlert.showError(stage, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignUpHyperlink(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("sign-up.fxml"));
            changeRoot(fxmlLoader);

            Controller controller = fxmlLoader.getController();
            controller.setupController(new ControllerDTO(service, stage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupController(ControllerDTO controllerDTO) {
        service = controllerDTO.getService();
        stage = controllerDTO.getStage();
        Optional.ofNullable(service).orElseThrow(() -> new SetupControllerException("Service is null in sign in controller"));
        Optional.ofNullable(stage).orElseThrow(() -> new SetupControllerException("Stage is null in sign in controller"));
        String email = "", password = "";
        if (controllerDTO.isConnectedUser()) {
            email = controllerDTO.getConnectedUserDTOEmail();
            password = controllerDTO.getConnectedUserDTOPassword();
        }
        setTextFields(email, password);
    }
}
