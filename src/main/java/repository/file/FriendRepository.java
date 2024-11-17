package repository.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Friend;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FriendRepository extends AbstractInFileRepository<Long, Friend> {

    /**
     * Constructor for FriendRepository
     *
     * @param fileName The name of the file that stores friends
     */
    public FriendRepository(String fileName) {
        super(fileName);
    }

    @Override
    public List<Friend> createEntities() {
        try {
            return new ObjectMapper().readValue(new File(fileName), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
