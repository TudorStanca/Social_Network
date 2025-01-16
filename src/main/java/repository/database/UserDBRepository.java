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
        User user = new User(firstName, lastName, email, password, salt);
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
        String query = "INSERT INTO USERS (first_name, last_name, email, password, salt) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, entity.getFirstName());
        statement.setString(2, entity.getLastName());
        statement.setString(3, entity.getEmail());
        statement.setBytes(4, entity.getPassword());
        statement.setBytes(5, entity.getSalt());
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
        String query = "UPDATE USERS SET first_name = ?, last_name = ?, email = ?, password = ?, salt = ? WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, entity.getFirstName());
        statement.setString(2, entity.getLastName());
        statement.setString(3, entity.getEmail());
        statement.setBytes(4, entity.getPassword());
        statement.setBytes(5, entity.getSalt());
        statement.setLong(6, entity.getId());
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
}
