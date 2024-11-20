package service;

import domain.Friend;
import domain.Graph;
import domain.User;
import domain.exceptions.IdNotFoundException;
import domain.exceptions.ObjectAlreadyInRepositoryException;
import domain.exceptions.UserNotFoundInDatabase;
import domain.exceptions.ValidationException;
import domain.validators.Validator;
import repository.Repository;
import repository.database.UserDBRepository;
import utils.Constants;
import utils.events.Event;
import utils.events.FriendRequestEvent;
import utils.observer.Observable;
import utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

public class Service implements Observable<Event> {

    private final Repository<Long, User> repoUser;
    private final Repository<Long, Friend> repoFriend;

    private final Validator<User> validatorUser;
    private final Validator<Friend> validatorFriend;

    private List<Observer<Event>> observers = new ArrayList<>();

    /**
     * Constructor for Service
     *
     * @param repoUser        The repository that stores users
     * @param repoFriend      The repository that stores friends
     * @param validatorUser   The validator that validates users
     * @param validatorFriend The validator that validates friends
     */
    public Service(Repository<Long, User> repoUser, Repository<Long, Friend> repoFriend, Validator<User> validatorUser, Validator<Friend> validatorFriend) {
        this.repoUser = repoUser;
        this.repoFriend = repoFriend;
        this.validatorUser = validatorUser;
        this.validatorFriend = validatorFriend;
    }

    @Override
    public void addObserver(Observer<Event> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<Event> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Event event) {
        observers.forEach(observer -> observer.update(event));
    }

    /**
     * Gets all the entities from a repository
     *
     * @param strategy The strategy that determines which repository to choose
     * @return All the entities from a repository
     */
    public Iterable<?> getRepositoryContent(RepositoryStrategy strategy) {
        switch (strategy) {
            case USER -> {
                return repoUser.findAll();
            }
            case FRIEND -> {
                return repoFriend.findAll();
            }
            default -> throw new IllegalStateException("Unexpected value: " + strategy);
        }
    }

    /**
     * Calculates the number of entities from an Iterable object
     *
     * @param iterable The entities
     * @return The number of entities
     */
    private int numberOfEntities(Iterable<?> iterable) {
        return (int) StreamSupport.stream(iterable.spliterator(), false)
                .count();
    }

    /**
     * Gets the number of entities from a repository
     *
     * @param strategy The strategy that determines which repository to choose
     * @return The number of entities from the repository
     */
    public int getNumberOfEntitiesInRepository(RepositoryStrategy strategy) {
        switch (strategy) {
            case USER -> {
                return numberOfEntities(repoUser.findAll());
            }
            case FRIEND -> {
                return numberOfEntities(repoFriend.findAll());
            }
            default -> throw new IllegalStateException("Unexpected value: " + strategy);
        }
    }

    public User findUserByEmailPassword(String email, String password) {
        Optional<User> user = ((UserDBRepository) repoUser).findOne(email, password);
        if(user.isEmpty()) {
            throw new UserNotFoundInDatabase(email);
        }
        return user.get();
    }

    public Iterable<User> findUserCandidateFriends(Long userId){
        return ((UserDBRepository) repoUser).findUserCandidateFriends(userId);
    }

    /**
     * Adds a user to the repository
     *
     * @param firstname The first name of the new user
     * @param lastname  The last name of the new user
     * @return The new user added
     * @throws ValidationException                if the new user is not valid
     * @throws ObjectAlreadyInRepositoryException if the user is already in repository
     */
    public User addUser(String firstname, String lastname, String email, String password) {
        User newUser = new User(firstname, lastname, email, password);
        validatorUser.validate(newUser);
        if (repoUser.save(newUser).isPresent()) {
            throw new ObjectAlreadyInRepositoryException(newUser);
        }
        return newUser;
    }

    /**
     * Deletes a user from the repository
     *
     * @param id The id of the user that is being deleted
     * @return The deleted user
     * @throws IdNotFoundException      if the id has not been found in repository
     * @throws IllegalArgumentException if the id is null
     */
    public User deleteUser(Long id) {
        Optional<User> user = repoUser.delete(id);
        if (user.isEmpty()) {
            throw new IdNotFoundException(id);
        }
        return user.get();
    }

    /**
     * Updates a user in the repository
     *
     * @param id        The id of the user that is being updated
     * @param firstname The new first name of the user
     * @param lastname  The new last name of the user
     * @return The new updated user
     * @throws ValidationException      if the new user is not valid
     * @throws IdNotFoundException      if the id has not been found in repository
     * @throws IllegalArgumentException if the id is null
     */
    public User updateUser(Long id, String firstname, String lastname, String email, String password) {
        User newUser = new User(firstname, lastname, email, password);
        newUser.setId(id);
        validatorUser.validate(newUser);
        if (repoUser.update(newUser).isPresent()) {
            throw new IdNotFoundException(id);
        }
        return newUser;
    }

    /**
     * Adds a friendship in the repository
     *
     * @param id1 The id of the first user
     * @param id2 The id of the second user
     * @return The new friendship added
     * @throws ValidationException                if the friendship is not valid
     * @throws ObjectAlreadyInRepositoryException if the friendship already exists
     * @throws IdNotFoundException                if the given id1 or id2 don't have a user in the user repository
     * @throws IllegalArgumentException           if the given ids are null
     */
    public Friend addFriend(Long id1, Long id2) {
        Friend newFriend = new Friend(id1, id2, false, LocalDateTime.now());
        validatorFriend.validate(newFriend);

        if (repoFriend.save(newFriend).isPresent()) {
            throw new ObjectAlreadyInRepositoryException(newFriend);
        }

        notifyObservers(new FriendRequestEvent(id2));

        return newFriend;
    }

    /**
     * Deletes a friend from the repository
     *
     * @param id The id of the friendship that is being deleted
     * @return The deleted friend
     * @throws IdNotFoundException      if the id has no corresponding friendship in repository
     * @throws IllegalArgumentException if the id is null
     */
    public Friend deleteFriend(Long id) {
        Optional<Friend> friend = repoFriend.delete(id);
        if (friend.isEmpty()) {
            throw new IdNotFoundException(id);
        }
        return friend.get();
    }

    /**
     * Updates a friendship from the repository
     *
     * @param id          The id of the friendship that is being updated
     * @param idNewFriend The new id of the second friend
     * @return The new Friend
     * @throws IdNotFoundException                if the idNewFriend doesn't have a corresponding user
     * @throws ObjectAlreadyInRepositoryException if the new friendship already exists
     * @throws ValidationException                if the new friendship is not valid
     * @throws IllegalArgumentException           if id is null
     */
    public Friend updateFriend(Long id, Long idNewFriend, boolean newStatus, LocalDateTime newTime) {
        Optional<Friend> friend = repoFriend.findOne(id);
        friend.orElseThrow(() -> new IdNotFoundException(id));

        Friend newFriend = new Friend(friend.get().getFirstFriend(), idNewFriend, newStatus, newTime);
        newFriend.setId(id);
        validatorFriend.validate(newFriend);

        if (repoFriend.update(newFriend).isPresent()) {
            throw new IdNotFoundException(id);
        }
        return newFriend;
    }

    /**
     * Creates a hashMap that stores the id of users and the corresponding vertex in a graph
     *
     * @return The new hashMap created
     */
    private Map<Long, Integer> getUserHashMap() {
        return StreamSupport.stream(repoUser.findAll().spliterator(), false)
                .collect(HashMap::new, (map, user) -> map.put(user.getId(), map.size()), HashMap::putAll);
    }

    /**
     * Creates a hashMap that stores a vertex and the corresponding id of a user
     *
     * @return The new hashMap created
     */
    private Map<Integer, Long> getReverseUserHashMap() {
        return StreamSupport.stream(repoUser.findAll().spliterator(), false)
                .collect(HashMap::new, (map, user) -> map.put(map.size(), user.getId()), HashMap::putAll);
    }

    /**
     * Creates a graph with the users as vertexes and friends as edges
     *
     * @return The new graph
     */
    private Graph createGraph() {
        Map<Long, Integer> userHashMap = getUserHashMap();
        Graph graph = new Graph(userHashMap.size());
        repoFriend.findAll().forEach(friend -> graph.addEdge(userHashMap.get(friend.getFirstFriend()), userHashMap.get(friend.getSecondFriend())));
        return graph;
    }

    /**
     * Gets the number of social communities in the graph
     *
     * @return The number of social communities in the graph
     */
    public int getNumberOfCommunities() {
        Graph graph = createGraph();
        return graph.getNumberOfConnectedComponents();
    }

    /**
     * Calculates the biggest community in the graph (after the longest path in that community)
     *
     * @return The list of users that form the community
     */
    public List<User> biggestCommunity() {
        Graph graph = createGraph();
        Map<Integer, Long> userHashMap = getReverseUserHashMap();
        return graph.biggestComponent().stream()
                .map(x -> repoUser.findOne(userHashMap.get(x)).get())
                .toList();
    }
}
