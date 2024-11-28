package repository.database;

import domain.Message;
import domain.User;
import domain.dto.MessageDTO;
import domain.exceptions.DatabaseConnectionException;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDBRepository extends AbstractDBRepository<Long, Message> {

    public MessageDBRepository(String user, String password, String url) {
        super(user, password, url);
    }

    @Override
    protected Message queryToEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long id_from = resultSet.getLong("id_from");
        String text = resultSet.getString("message");
        Timestamp temp = resultSet.getTimestamp("date");
        Long id_reply = resultSet.getLong("id_reply");
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(temp.getTime()), ZoneOffset.UTC);
        Message message = new Message(id_from, text, date);
        message.setId(id);
        message.setReply(id_reply);
        return message;
    }

    @Override
    protected ResultSet findOneQuery(Long id, Connection conn) throws SQLException {
        String query = "SELECT * FROM MESSAGES U WHERE U.id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, id);
        return statement.executeQuery();
    }

    @Override
    protected ResultSet findAllQuery(Connection conn) throws SQLException {
        String query = "SELECT * FROM MESSAGES";
        Statement statement = conn.createStatement();
        return statement.executeQuery(query);
    }

    @Override
    protected PreparedStatement saveToDatabase(Message entity, Connection conn) throws SQLException {
        String query = "INSERT INTO MESSAGES (id_from, message, date, id_reply) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, entity.getFrom());
        statement.setString(2, entity.getMessage());
        statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
        statement.setLong(4, entity.getReply());
        return statement;
    }

    @Override
    protected PreparedStatement deleteFromDatabase(Long id, Connection conn) throws SQLException {
        String query = "DELETE FROM MESSAGES WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, id);
        return statement;
    }

    @Override
    protected PreparedStatement updateDatabase(Message entity, Connection conn) throws SQLException {
        String query = "UPDATE MESSAGES SET id_from = ?, message = ?, date = ?, id_reply = ? WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, entity.getFrom());
        statement.setString(2, entity.getMessage());
        statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
        statement.setLong(4, entity.getReply());
        statement.setLong(5, entity.getId());
        return statement;
    }

    private List<Long> getToIdsFromDatabase(Long id, Connection conn) throws SQLException {
        String query = "SELECT id_to FROM MESSAGES_TO_USERS WHERE id_message = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        List<Long> lst = new ArrayList<>();
        while (resultSet.next()) {
            lst.add(resultSet.getLong("id_to"));
        }
        return lst;
    }

    private void saveToIds(Message entity, Connection conn) throws SQLException {
        String query = "INSERT INTO MESSAGES_TO_USERS (id_message, id_to) VALUES (?, ?)";
        for(Long id : entity.getTo()) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setLong(1, entity.getId());
            statement.setLong(2, id);
            statement.execute();
        }
    }

    @Override
    public Optional<Message> findOne(Long id){
        Optional.ofNullable(id).orElseThrow(() -> new IllegalArgumentException("Id cannot be null"));

        try (Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)) {
            ResultSet resultSet = findOneQuery(id, conn);
            if (resultSet.next()) {
                Message msg = queryToEntity(resultSet);
                msg.setTo(getToIdsFromDatabase(id, conn));
                return Optional.of(msg);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseConnectionException();
        }
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> lst = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)) {
            ResultSet rs = findAllQuery(conn);
            while (rs.next()) {
                Message msg = queryToEntity(rs);
                msg.setTo(getToIdsFromDatabase(msg.getId(), conn));
                lst.add(msg);
            }
            return lst;
        } catch (SQLException e) {
            throw new DatabaseConnectionException();
        }
    }

    @Override
    public Optional<Message> save(Message entity) {
        Optional.ofNullable(entity).orElseThrow(() -> new IllegalArgumentException("Entity cannot be null"));

        try (Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)) {
            saveToDatabase(entity, conn).execute();
            saveToIds(entity, conn);
        } catch (SQLException e) {
            throwDatabaseExceptions(e.getSQLState(), e.getMessage());
        }
        return Optional.empty();
    }

    private ResultSet getReplyMessageResultSet(Long idReply, Long idFrom, Long idTo, Connection conn) throws SQLException {
        String query = """
                SELECT M.id, M.id_from, M.message, M.date
                FROM MESSAGES M
                INNER JOIN messages MD ON M.id = MD.id_reply
                INNER JOIN messages_to_users MU ON M.id = MU.id_message AND (MU.id_to = ? OR MU.id_to = ?)
                WHERE M.id = ?""";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setLong(1, idFrom);
        statement.setLong(2, idTo);
        statement.setLong(3, idReply);
        return statement.executeQuery();
    }

    public Iterable<MessageDTO> getMessages(Long idFrom, Long idTo){
        String query = """
            SELECT M.*, MU.id_to
            FROM MESSAGES M
            INNER JOIN MESSAGES_TO_USERS MU ON M.id = MU.id_message AND ((M.id_from = ? OR M.id_from = ?) AND (MU.id_to = ? OR MU.id_to = ?))""";
        Optional.ofNullable(idFrom).orElseThrow(() -> new IllegalArgumentException("IdFrom cannot be null"));
        Optional.ofNullable(idTo).orElseThrow(() -> new IllegalArgumentException("IdTo cannot be null"));

        try(Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)){
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setLong(1, idFrom);
            statement.setLong(2, idTo);
            statement.setLong(3, idFrom);
            statement.setLong(4, idTo);
            ResultSet resultSet = statement.executeQuery();
            List<MessageDTO> lst = new ArrayList<>();

            while (resultSet.next()) {
                Long idMessage = resultSet.getLong("id");
                Long idFromUser = resultSet.getLong("id_from");
                Long idToUser = resultSet.getLong("id_to");
                String text = resultSet.getString("message");
                Timestamp temp = resultSet.getTimestamp("date");
                LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(temp.getTime()), ZoneOffset.UTC);
                Long idReply = resultSet.getLong("id_reply");

                if(idReply == null){
                    ResultSet resultSetReply = getReplyMessageResultSet(idReply, idFrom, idTo, conn);
                    Long idMessageReply = resultSetReply.getLong("id");
                    String textReply = resultSetReply.getString("message");
                    Timestamp tempReply = resultSet.getTimestamp("date");
                    LocalDateTime dateReply = LocalDateTime.ofInstant(Instant.ofEpochMilli(tempReply.getTime()), ZoneOffset.UTC);
                    lst.add(new MessageDTO(idMessage, idFromUser, idToUser, text, date, idMessageReply, textReply, dateReply));
                }
                else{
                    lst.add(new MessageDTO(idMessage, idFromUser, idToUser, text, date));
                }
            }

            return lst;
        } catch (SQLException e) {
            throw new DatabaseConnectionException();
        }
    }
}
