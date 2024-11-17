package repository.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UserRepository extends AbstractInFileRepository<Long, User> {

    /**
     * Constructor for UserRepository
     *
     * @param fileName The file from the users are being loaded
     */
    public UserRepository(String fileName) {
        super(fileName);
    }

    @Override
    public List<User> createEntities() {
        try {
            return new ObjectMapper().readValue(new File(fileName), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
