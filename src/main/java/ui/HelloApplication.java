package ui;

import domain.Friend;
import domain.User;
import domain.validators.FriendValidator;
import domain.validators.UserValidator;
import domain.validators.Validator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.Repository;
import repository.database.FriendDBRepository;
import repository.database.UserDBRepository;
import service.Service;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        String url = "jdbc:postgresql://192.168.0.206:5432/SOCIAL_NETWORK_MAP";
        String user = "postgres";
        String password = "1234";

        Repository<Long, User> repoUser = new UserDBRepository(user, password, url);
        Repository<Long, Friend> repoFriend = new FriendDBRepository(user, password, url);

        Validator<User> validator = new UserValidator();
        Validator<Friend> validatorFriend = new FriendValidator();

        Service service = new Service(repoUser, repoFriend, validator, validatorFriend);
        Ui ui = new Ui(service);

        ui.run();
        launch();
    }
}