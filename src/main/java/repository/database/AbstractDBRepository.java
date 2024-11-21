package repository.database;

import domain.Entity;
import domain.exceptions.DataBaseUniqueConstraintViolationError;
import domain.exceptions.DatabaseConnectionException;
import domain.exceptions.ForeignKeyViolationException;
import domain.exceptions.MyException;
import repository.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractDBRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    protected final String databaseUser;
    protected final String databasePassword;
    protected final String databaseURL;

    /**
     * Constructor for AbstractDBRepository
     *
     * @param user     the name of the user in the database
     * @param password the user's password in the database
     * @param url      the url of the database
     */
    public AbstractDBRepository(String user, String password, String url) {
        databaseUser = user;
        databasePassword = password;
        databaseURL = url;
    }

    /**
     * Gets the data from the error message
     *
     * @param error the error message
     * @return the wanted data
     */
    private String getForeignKeyFromError(String error) {
        Pattern pattern = Pattern.compile("=\\(.+\\)");
        Matcher matcher = pattern.matcher(error);
        if (matcher.find()) {
            String temp = matcher.group();
            return temp.substring(2, temp.length() - 1);
        }
        return error;
    }

    /**
     * Throws a new exception based upon the type of exceptions that were thrown by the database
     *
     * @param errorType the exception type
     * @param msg       the exception message
     * @throws MyException by default
     */
    private void throwDatabaseExceptions(String errorType, String msg) throws MyException {
        switch (errorType) {
            case "23505" ->
                    throw new DataBaseUniqueConstraintViolationError("The entity " + getForeignKeyFromError(msg) + " already exists");
            case "P0001" -> throw new DataBaseUniqueConstraintViolationError("The entity already exists");
            case "23503" -> throw new ForeignKeyViolationException(getForeignKeyFromError(msg));
            default -> throw new DatabaseConnectionException();
        }
    }

    /**
     * Converts a query output in an entity
     *
     * @param resultSet the output of a query
     * @return the converted entity
     * @throws SQLException if the conversion is not possible
     */
    protected abstract E queryToEntity(ResultSet resultSet) throws SQLException;

    /**
     * Finds an entity in the database after an id
     *
     * @param id the id after the search is being made
     * @return the resulted entity in a ResultSet
     * @throws SQLException if the connection has failed
     */
    protected abstract ResultSet findOneQuery(ID id, Connection conn) throws SQLException;

    /**
     * Gets all the rows in a table from the database
     *
     * @return all the rows in a ResultSet
     * @throws SQLException if the connection failed
     */
    protected abstract ResultSet findAllQuery(Connection conn) throws SQLException;

    /**
     * Creates a query for adding an entity to the database
     *
     * @param entity the entity that will be saved
     * @return the resulted query
     * @throws SQLException if the connection failed
     */
    protected abstract PreparedStatement saveToDatabase(E entity, Connection conn) throws SQLException;

    /**
     * Creates a query for deleting an entity after its id
     *
     * @param id the id of the entity that will be deleted
     * @return the resulted query
     * @throws SQLException if the connection failed
     */
    protected abstract PreparedStatement deleteFromDatabase(ID id, Connection conn) throws SQLException;

    /**
     * Creates a query for updating an entity in the database
     *
     * @param entity the entity that is being updated
     * @return the resulted query
     * @throws SQLException if the connection failed
     */
    protected abstract PreparedStatement updateDatabase(E entity, Connection conn) throws SQLException;

    @Override
    public Optional<E> findOne(ID id) {
        Optional.ofNullable(id).orElseThrow(() -> new IllegalArgumentException("Id cannot be null"));

        try (Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)) {
            ResultSet resultSet = findOneQuery(id, conn);
            if (resultSet.next()) {
                return Optional.of(queryToEntity(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseConnectionException();
        }
    }

    @Override
    public Iterable<E> findAll() {
        List<E> lst = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)) {
            ResultSet rs = findAllQuery(conn);
            while (rs.next()) {
                lst.add(queryToEntity(rs));
            }
            return lst;
        } catch (SQLException e) {
            throw new DatabaseConnectionException();
        }
    }

    @Override
    public Optional<E> save(E entity) {
        Optional.ofNullable(entity).orElseThrow(() -> new IllegalArgumentException("Entity cannot be null"));
        try (Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)) {
            saveToDatabase(entity, conn).execute();
        } catch (SQLException e) {
            throwDatabaseExceptions(e.getSQLState(), e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> entity = findOne(id);
        entity.ifPresent((_) -> {
            try (Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)) {
                deleteFromDatabase(id, conn).execute();
            } catch (SQLException e) {
                throw new DatabaseConnectionException();
            }
        });
        return entity;
    }

    @Override
    public Optional<E> update(E entity) {
        Optional<E> optionalEntity = findOne(entity.getId());
        optionalEntity.ifPresent((_) -> {
            try (Connection conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)) {
                updateDatabase(entity, conn).execute();
            } catch (SQLException e) {
                throwDatabaseExceptions(e.getSQLState(), e.getMessage());
            }
        });
        if (optionalEntity.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(entity);
    }
}
