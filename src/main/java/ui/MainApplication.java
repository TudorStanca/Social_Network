package ui;

import config.Config;
import controller.Controller;
import domain.Friend;
import domain.Message;
import domain.User;
import domain.dto.ControllerDTO;
import domain.dto.UserDTO;
import domain.validators.FriendValidator;
import domain.validators.MessageValidator;
import domain.validators.UserValidator;
import domain.validators.Validator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import repository.Repository;
import repository.database.FriendDBRepository;
import repository.database.MessageDBRepository;
import repository.database.UserDBRepository;
import service.Service;

import java.io.IOException;

public class MainApplication extends Application {

    private Service service;

    @Override
    public void start(Stage primaryStage) throws IOException {

        String url = Config.getProperties().getProperty("db.url");
        String user = Config.getProperties().getProperty("db.username");
        String password = Config.getProperties().getProperty("db.password");

        Repository<Long, User> repoUser = new UserDBRepository(user, password, url);
        Repository<Long, Friend> repoFriend = new FriendDBRepository(user, password, url);
        Repository<Long, Message> repoMessage = new MessageDBRepository(user, password, url);

        Validator<User> validator = new UserValidator();
        Validator<Friend> validatorFriend = new FriendValidator();
        Validator<Message> validatorMessage = new MessageValidator();

        service = new Service(repoUser, repoFriend, repoMessage, validator, validatorFriend, validatorMessage);

        initView(primaryStage);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("sign-in.fxml"));

        Pane root = fxmlLoader.load();
        primaryStage.setScene(new Scene(root));

        root.requestFocus();
        primaryStage.setResizable(false);
        primaryStage.setTitle("Sign In");

        Controller controller = fxmlLoader.getController();
        controller.setupController(new ControllerDTO(service, primaryStage, new UserDTO("john.doe@gmail.com", "1234")));
    }

    public static void main(String[] args) {
        launch();
    }
}