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
import javafx.stage.FileChooser;
import ui.MainApplication;
import utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class SignUpController extends AbstractController {

    private String profileImagePath = Constants.DEFAULT_PROFILE_IMAGE;

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
            User user = service.addUser(firstName, lastName, email, password, profileImagePath);
            MessageAlert.showMessage(stage, "Account create confirmation", "The new account: " + user.toString() + " has been successfully created");

            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("sign-in.fxml"));
            changeRoot(fxmlLoader);

            Controller controller = fxmlLoader.getController();
            controller.setupController(new ControllerDTO(service, stage, new UserDTO(email, password)));
        } catch (MyException e) {
            MessageAlert.showError(stage, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfileImageButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a profile image");
        fileChooser.setInitialDirectory(new File("C:\\"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            profileImagePath = service.saveImageLocal(file, profileImagePath).toString();
        }
    }

    @FXML
    private void handleSignInHyperlink(ActionEvent event) {
        service.deleteImage(profileImagePath);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("sign-in.fxml"));
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
        Optional.ofNullable(service).orElseThrow(() -> new SetupControllerException("Service is null in sign up controller"));
        Optional.ofNullable(stage).orElseThrow(() -> new SetupControllerException("Stage is null in sign up controller"));
    }
}
