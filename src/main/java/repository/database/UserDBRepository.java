package repository.database;

import domain.User;

import java.sql.*;

public class UserDBRepository extends AbstractDBRepository<Long, User> {

    public UserDBRepository(String user, String password, String url) {
        super(user, password, url);
    }

    @Override
    protected User queryToEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        User user = new User(firstName, lastName);
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
        String query = "INSERT INTO USERS (first_name, last_name) VALUES (?, ?)";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, entity.getFirstName());
        statement.setString(2, entity.getLastName());
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
        String query = "UPDATE USERS SET first_name = ?, last_name = ? WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, entity.getFirstName());
        statement.setString(2, entity.getLastName());
        statement.setLong(3, entity.getId());
        return statement;
    }
}
