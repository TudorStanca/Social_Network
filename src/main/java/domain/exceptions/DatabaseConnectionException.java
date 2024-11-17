package domain.exceptions;

public class DatabaseConnectionException extends MyException {

    public DatabaseConnectionException() {
        super("Database connection failed!");
    }
}
