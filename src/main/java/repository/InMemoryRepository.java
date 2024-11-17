package repository;

import domain.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    protected Map<ID, E> entities;

    /**
     * Constructor for InMemoryRepository
     */
    public InMemoryRepository() {
        entities = new HashMap<>();
    }

    @Override
    public Optional<E> findOne(ID id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity));
    }

    @Override
    public Optional<E> delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        if (entities.containsKey(entity.getId())) {
            entities.put(entity.getId(), entity);
            return Optional.empty();
        }
        return Optional.of(entity);
    }
}
