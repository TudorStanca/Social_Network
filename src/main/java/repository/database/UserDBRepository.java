package repository.database;

import domain.User;
import domain.exceptions.DatabaseConnectionException;
import utils.PasswordHashing;
import utils.paging.Page;
import utils.paging.Pageable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.sql.DriverManager;

public class UserDBRepository extends AbstractDBRepository<Long, User> {

    public UserDBRepository(String user, String password, String url) {
        super(user, password, url);
    }

    @Override
    protected User queryToEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        String email = resultSet.getString("email");
        byte[] password = resultSet.getBytes("password");
        byte[] salt = resultSet.getBytes("salt");
        String imagePath = resultSet.getString("image_path");
        User user = new User(firstName, lastName, email, password, salt, imagePath);
        user.setId(id);
        return user;
    }

    @Override
    protected ResultSet findOneQuery(Long id, Connection conn) throws SQLException {
        String query = "SELECT * FROM USERS U WHERE U.id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, id);
        return statement.executeQuery();
    }

    @Override
    protected ResultSet findAllQuery(Connection conn) throws SQLException {
        String query = "SELECT * FROM USERS";
        Statement statement = conn.createStatement();
        return statement.executeQuery(query);
    }

    @Override
    protected PreparedStatement saveToDatabase(User entity, Connection conn) throws SQLException {
        String query = "INSERT INTO USERS (first_name, last_name, email, password, salt, image_path) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, entity.getFirstName());
        statement.setString(2, entity.getLastName());
        statement.setString(3, entity.getEmail());
        statement.setBytes(4, entity.getPassword());
        statement.setBytes(5, entity.getSalt());
        statement.setString(6, entity.getImagePath());
        return statement;
    }

    @Override
    protected PreparedStatement deleteFromDatabase(Long id, Connection conn) throws SQLException {
        String query = "DELETE FROM USERS WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, id);
        return statement;
    }

    @Override
    protected PreparedStatement updateDatabase(User entity, Connection conn) throws SQLException {
        String query = "UPDATE USERS SET first_name = ?, last_name = ?, email = ?, password = ?, salt = ?, image_path = ? WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, entity.getFirstName());
        statement.setString(2, entity.getLastName());
        statement.setString(3, entity.getEmail());
        statement.setBytes(4, entity.getPassword());
        statement.setBytes(5, entity.getSalt());
        statement.setString(6, entity.getImagePath());
        statement.setLong(7, entity.getId());
        return statement;
    }

    public Optional<User> findOne(String email) {
        Optional.ofNullable(email).orElseThrow(() -> new IllegalArgumentException("Email cannot be null"));

        try (Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)) {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM USERS WHERE email = ?");
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(queryToEntity(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseConnectionException();
        }
    }

    public Optional<User> updateProfileImage(Long id, String imagePath) {
        Optional.ofNullable(id).orElseThrow(() -> new IllegalArgumentException("Id cannot be null"));
        Optional.ofNullable(imagePath).orElseThrow(() -> new IllegalArgumentException("ImagePath cannot be null"));

        try (Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)) {
            Optional<User> user = findOne(id);
            if (user.isEmpty()) {
                return Optional.empty();
            }
            PreparedStatement statement = conn.prepareStatement("UPDATE USERS SET image_path = ? WHERE id = ?");
            statement.setString(1, imagePath);
            statement.setLong(2, id);
            statement.execute();
            user.get().setImagePath(imagePath);
            return user;
        } catch (SQLException e) {
            throw new DatabaseConnectionException();
        }
    }
}
