package repository.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Entity;
import repository.InMemoryRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public abstract class AbstractInFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {

    protected String fileName;

    /**
     * Constructor for AbstractInFileRepository
     *
     * @param fileName The file that stores entities
     */
    public AbstractInFileRepository(String fileName) {
        this.fileName = fileName;
        loadData();
    }

    /**
     * Creates a list of entities from the file
     *
     * @return The list of entities
     */
    public abstract List<E> createEntities();

    /**
     * Loads all the entities from the file in memory
     */
    private void loadData() {
        if (new File(fileName).length() != 0) {
            createEntities().forEach(super::save);
        }
    }

    /**
     * Writes all the entities from memory in file
     */
    private void writeData() {
        try {
            new ObjectMapper().writeValue(new File(fileName), entities.values());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<E> save(E entity) {
        Optional<E> newEntity = super.save(entity);
        if (newEntity.isEmpty())
            writeData();
        return newEntity;
    }

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> deletedEntity = super.delete(id);
        if (deletedEntity.isPresent()) {
            writeData();
        }
        return deletedEntity;
    }

    @Override
    public Optional<E> update(E entity) {
        Optional<E> updatedEntity = super.update(entity);
        if (updatedEntity.isEmpty()) {
            writeData();
        }
        return updatedEntity;
    }
}
