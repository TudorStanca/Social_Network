package service;

import domain.Friend;
import domain.Graph;
import domain.Message;
import domain.User;
import domain.dto.FriendDTO;
import domain.dto.MessageDTO;
import domain.exceptions.*;
import domain.validators.Validator;
import repository.Repository;
import repository.database.FriendDBRepository;
import repository.database.MessageDBRepository;
import repository.database.UserDBRepository;
import utils.Constants;
import utils.PasswordHashing;
import utils.events.*;
import utils.observer.Observable;
import utils.observer.Observer;
import utils.paging.Page;
import utils.paging.Pageable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

public class Service implements Observable<Event> {

    private final Repository<Long, User> repoUser;
    private final Repository<Long, Friend> repoFriend;
    private final Repository<Long, Message> repoMessage;

    private final Validator<User> validatorUser;
    private final Validator<Friend> validatorFriend;
    private final Validator<Message> validatorMessage;

    private List<Observer<Event>> observers = new ArrayList<>();

    /**
     * Constructor for Service
     *
     * @param repoUser        The repository that stores users
     * @param repoFriend      The repository that stores friends
     * @param validatorUser   The validator that validates users
     * @param validatorFriend The validator that validates friends
     */
    public Service(Repository<Long, User> repoUser, Repository<Long, Friend> repoFriend, Repository<Long, Message> repoMessage, Validator<User> validatorUser, Validator<Friend> validatorFriend, Validator<Message> validatorMessage) {
        this.repoUser = repoUser;
        this.repoFriend = repoFriend;
        this.repoMessage = repoMessage;
        this.validatorUser = validatorUser;
        this.validatorFriend = validatorFriend;
        this.validatorMessage = validatorMessage;
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

    public User findOneUser(Long idUser) {
        Optional<User> user = repoUser.findOne(idUser);
        return user.orElse(null);
    }

    public User findUserByEmailPassword(String email, String password) {
        Optional<User> user = ((UserDBRepository) repoUser).findOne(email);
        if (user.isEmpty()) {
            throw new UserNotFoundInDatabase(email);
        }
        if(!PasswordHashing.isExpectedPassword(password, user.get().getPassword(), user.get().getSalt())) {
            throw new InvalidPassword();
        }
        return user.get();
    }

    public Iterable<FriendDTO> findUserCandidateFriends(Long userId) {
        return ((FriendDBRepository) repoFriend).findCandidateFriends(userId);
    }

    public Iterable<FriendDTO> findPendingRecievingFriendRequests(Long userId) {
        return ((FriendDBRepository) repoFriend).findPendingFriendRequests(userId);
    }

    public Iterable<FriendDTO> findUserFriends(Long userId) {
        return ((FriendDBRepository) repoFriend).findFriends(userId);
    }

    public Page<FriendDTO> findUserFriends(Pageable pageable, Long userId) {
        return ((FriendDBRepository) repoFriend).findAllFriendsOnPage(pageable, userId);
    }

    public Iterable<MessageDTO> findMessages(Long userId, Long friendId) {
        return ((MessageDBRepository) repoMessage).getMessages(userId, friendId);
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
    public User addUser(String firstname, String lastname, String email, String password, String imagePath) {
        byte[] salt = PasswordHashing.generateSalt();
        byte[] hash = PasswordHashing.generateHash(password, salt);
        User newUser = new User(firstname, lastname, email, hash, salt, imagePath);
        validatorUser.validate(newUser);
        if (repoUser.save(newUser).isPresent()) {
            throw new ObjectAlreadyInRepositoryException(newUser);
        }

        notifyObservers(new UserChangeEvent(EventType.ACCEPT_REQUEST));

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
        deleteImage(user.get().getImagePath());
        notifyObservers(new UserChangeEvent(EventType.DELETE_REQUEST));
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
    public User updateUser(Long id, String firstname, String lastname, String email, String password, String imagePath) {
        byte[] salt = PasswordHashing.generateSalt();
        byte[] hash = PasswordHashing.generateHash(password, salt);
        User newUser = new User(firstname, lastname, email, hash, salt, imagePath);
        newUser.setId(id);
        validatorUser.validate(newUser);
        if (repoUser.update(newUser).isPresent()) {
            throw new IdNotFoundException(id);
        }
        notifyObservers(new UserChangeEvent(EventType.UPDATE_USER, id, imagePath));
        return newUser;
    }

    public void deleteImage(String imagePath) {
        if(!Objects.equals(imagePath, Constants.DEFAULT_PROFILE_IMAGE)) {
            new File(imagePath).delete();
        }
    }

    public Path saveImageLocal(File file, String imagePathToDelete) {
        try {
            Path copied;
            int number = 0;

            do {
                int pos = file.getName().lastIndexOf("."); // the . from the extension
                String fileName = file.getName().substring(0, pos) + ((number == 0) ? "" : "(" + number + ")") + file.getName().substring(pos);
                copied = Paths.get(Constants.DEFAULT_PROFILE_IMAGE_FOLDER).resolve(fileName);
                number++;
            } while (Files.exists(copied));

            Files.copy(file.toPath(), copied);

            deleteImage(imagePathToDelete);
            return copied;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateUserProfileImage(Long id, File file) {
        Optional<User> user = repoUser.findOne(id);
        user.orElseThrow(() -> new IdNotFoundException(id));

        Path newImagePath = saveImageLocal(file, user.get().getImagePath());
        ((UserDBRepository) repoUser).updateProfileImage(id, newImagePath.toString());

        notifyObservers(new UserChangeEvent(EventType.UPDATE_USER, id, newImagePath.toString()));
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

        notifyObservers(new FriendChangeEvent(id2, EventType.CREATE_REQUEST));

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

        notifyObservers(new FriendChangeEvent(id, friend.get(), EventType.DELETE_REQUEST));

        return friend.get();
    }

    /**
     * Updates a friendship from the repository
     *
     * @param id The id of the friendship that is being updated
     * @return The new Friend
     * @throws IdNotFoundException                if the idNewFriend doesn't have a corresponding user
     * @throws ObjectAlreadyInRepositoryException if the new friendship already exists
     * @throws ValidationException                if the new friendship is not valid
     * @throws IllegalArgumentException           if id is null
     */
    public Friend updateFriend(Long id, boolean newStatus, LocalDateTime newTime) {
        Optional<Friend> friend = repoFriend.findOne(id);
        friend.orElseThrow(() -> new IdNotFoundException(id));

        Friend newFriend = friend.get();
        newFriend.setStatus(newStatus);
        newFriend.setFriendsFrom(newTime);

        if (repoFriend.update(newFriend).isPresent()) {
            throw new IdNotFoundException(id);
        }

        notifyObservers(new FriendChangeEvent(EventType.ACCEPT_REQUEST));

        return newFriend;
    }

    public int countFriends(Long userId) {
        return ((FriendDBRepository) repoFriend).countFriends(userId, true);
    }

    public int countFriendRequests(Long userId) {
        return ((FriendDBRepository) repoFriend).countFriends(userId, false);
    }

    public Message addMessage(Long id_from, List<Long> id_to, String message, LocalDateTime date, Long id_reply) {
        Message newMessage = new Message(id_from, id_to, message, date);
        newMessage.setReply(id_reply);
        validatorMessage.validate(newMessage);
        if (repoMessage.save(newMessage).isPresent()) {
            throw new ObjectAlreadyInRepositoryException(newMessage);
        }

        notifyObservers(new MessageChangeEvent(EventType.CREATE_MESSAGE, id_from, id_to));

        return newMessage;
    }

    public Message deleteMessage(Long id) {
        Optional<Message> message = repoMessage.delete(id);
        if (message.isEmpty()) {
            throw new IdNotFoundException(id);
        }

        return message.get();
    }

    public Message updateMessage(Long id, String newText, LocalDateTime date) {
        Optional<Message> message = repoMessage.findOne(id);
        message.orElseThrow(() -> new IdNotFoundException(id));

        Message newMessage = message.get();
        newMessage.setMessage(newText);
        newMessage.setDate(date);

        if (repoMessage.update(newMessage).isPresent()) {
            throw new IdNotFoundException(id);
        }

        return newMessage;
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
