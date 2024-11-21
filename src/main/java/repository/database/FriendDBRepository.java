package repository.database;

import domain.Friend;
import domain.User;
import domain.dto.FriendDTO;
import domain.dto.UserDTO;
import domain.exceptions.DatabaseConnectionException;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        boolean status = resultSet.getBoolean("status");
        Timestamp temp = resultSet.getTimestamp("friends_from");
        LocalDateTime friendsFrom = LocalDateTime.ofInstant(Instant.ofEpochMilli(temp.getTime()), ZoneOffset.UTC);
        Friend friend = new Friend(idFriend1, idFriend2, status, friendsFrom);
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
        String query = "INSERT INTO FRIENDS (id_user_1, id_user_2, status, friends_from) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, entity.getFirstFriend());
        statement.setLong(2, entity.getSecondFriend());
        statement.setBoolean(3, entity.getStatus());
        statement.setTimestamp(4, Timestamp.valueOf(entity.getFriendsFrom()));
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
        String query = "UPDATE FRIENDS SET id_user_1 = ?, id_user_2 = ?, status = ?, friends_from = ? WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, entity.getFirstFriend());
        statement.setLong(2, entity.getSecondFriend());
        statement.setBoolean(3, entity.getStatus());
        statement.setTimestamp(4, Timestamp.valueOf(entity.getFriendsFrom()));
        statement.setLong(5, entity.getId());
        return statement;
    }

    public Iterable<FriendDTO> findCandidateFriends(Long id) {
        String query = """
                SELECT U.id, U.first_name, U.last_name
                FROM USERS U
                WHERE U.ID NOT IN (SELECT U.ID
                    FROM USERS U
                    INNER JOIN FRIENDS F on U.id = F.id_user_1 OR U.id = F.id_user_2
                    WHERE (F.id_user_1 = ? OR F.id_user_2 = ?) AND U.id != ?) AND U.ID != ?""";
        Optional.ofNullable(id).orElseThrow(() -> new IllegalArgumentException("Id cannot be null"));

        try (Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setLong(3, id);
            statement.setLong(4, id);
            ResultSet resultSet = statement.executeQuery();
            List<FriendDTO> lst = new ArrayList<>();
            if (resultSet.next()) {
                do {
                    Long idFriend = resultSet.getLong("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    lst.add(new FriendDTO(idFriend, firstName, lastName));
                } while (resultSet.next());
            }
            return lst;
        } catch (SQLException e) {
            throw new DatabaseConnectionException();
        }
    }

    public Iterable<FriendDTO> findPendingFriendRequests(Long userId) {
        String query = """
                SELECT F.id, U.id, U.first_name, U.last_name, F.friends_from
                FROM USERS U
                INNER JOIN FRIENDS F ON U.id = F.id_user_1 AND F.id_user_2 = ?
                WHERE F.status = false""";
        Optional.ofNullable(userId).orElseThrow(() -> new IllegalArgumentException("Id cannot be null"));

        try (Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)){
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            List<FriendDTO> lst = new ArrayList<>();
            if (resultSet.next()) {
                do {
                    Long idFriendship = resultSet.getLong(1);
                    Long idFriend = resultSet.getLong(2);
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    Timestamp temp = resultSet.getTimestamp("friends_from");
                    LocalDateTime friendsFrom = LocalDateTime.ofInstant(Instant.ofEpochMilli(temp.getTime()), ZoneOffset.UTC);
                    lst.add(new FriendDTO(idFriendship, idFriend, firstName, lastName, friendsFrom));
                } while (resultSet.next());
            }
            return lst;
        }
        catch (SQLException e) {
            throw new DatabaseConnectionException();
        }
    }

    public Iterable<FriendDTO> findFriends(Long userId) {
        String query = """
                SELECT F.id, U.id, U.first_name, U.last_name, F.friends_from
                FROM USERS U
                INNER JOIN FRIENDS F on U.id = F.id_user_1 OR U.id = F.id_user_2
                WHERE (F.id_user_1 = ? OR F.id_user_2 = ?) AND U.id != ? AND F.status = TRUE""";
        Optional.ofNullable(userId).orElseThrow(() -> new IllegalArgumentException("Id cannot be null"));

        try (Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)){
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setLong(1, userId);
            statement.setLong(2, userId);
            statement.setLong(3, userId);
            ResultSet resultSet = statement.executeQuery();
            List<FriendDTO> lst = new ArrayList<>();
            if (resultSet.next()) {
                do {
                    Long idFriendship = resultSet.getLong(1);
                    Long idFriend = resultSet.getLong(2);
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    Timestamp temp = resultSet.getTimestamp("friends_from");
                    LocalDateTime friendsFrom = LocalDateTime.ofInstant(Instant.ofEpochMilli(temp.getTime()), ZoneOffset.UTC);
                    lst.add(new FriendDTO(idFriendship, idFriend, firstName, lastName, friendsFrom));
                } while (resultSet.next());
            }
            return lst;
        }
        catch (SQLException e) {
            throw new DatabaseConnectionException();
        }
    }
}
