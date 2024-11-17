package repository.database;

import domain.Friend;

import java.sql.*;

public class FriendDBRepository extends AbstractDBRepository<Long, Friend> {

    public FriendDBRepository(String user, String password, String url) {
        super(user, password, url);
    }

    @Override
    protected Friend queryToEntity(ResultSet resultSet) throws SQLException {
        if (resultSet.isBeforeFirst()) {
            return null;
        }
        Long id = resultSet.getLong("id");
        Long idFriend1 = resultSet.getLong("id_user_1");
        Long idFriend2 = resultSet.getLong("id_user_2");
        Friend friend = new Friend(idFriend1, idFriend2);
        friend.setId(id);
        return friend;
    }

    @Override
    protected ResultSet findOneQuery(Long id, Connection conn) throws SQLException {
        String query = "SELECT * FROM FRIENDS WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, id);
        return statement.executeQuery();
    }

    @Override
    protected ResultSet findAllQuery(Connection conn) throws SQLException {
        String query = "SELECT * FROM FRIENDS";
        Statement statement = conn.createStatement();
        return statement.executeQuery(query);
    }

    @Override
    protected PreparedStatement saveToDatabase(Friend entity, Connection conn) throws SQLException {
        String query = "INSERT INTO FRIENDS (id_user_1, id_user_2) VALUES (?, ?)";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, entity.getFirstFriend());
        statement.setLong(2, entity.getSecondFriend());
        return statement;
    }

    @Override
    protected PreparedStatement deleteFromDatabase(Long id, Connection conn) throws SQLException {
        String query = "DELETE FROM FRIENDS WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, id);
        return statement;
    }

    @Override
    protected PreparedStatement updateDatabase(Friend entity, Connection conn) throws SQLException {
        String query = "UPDATE FRIENDS SET id_user_1 = ?, id_user_2 = ? WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, entity.getFirstFriend());
        statement.setLong(2, entity.getSecondFriend());
        statement.setLong(3, entity.getId());
        return statement;
    }
}
