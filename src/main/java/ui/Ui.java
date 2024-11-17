//package ui;
//
//import domain.exceptions.MyException;
//import service.RepositoryStrategy;
//import service.Service;
//
//import java.util.InputMismatchException;
//import java.util.Scanner;
//
//public class Ui {
//
//    private final Service service;
//    private final Scanner scanner;
//
//    public Ui(Service service) {
//        this.service = service;
//        scanner = new Scanner(System.in);
//    }
//
//    private void printRepository(Iterable<?> elements) {
//        elements.forEach(System.out::println);
//        System.out.println();
//    }
//
//    private void printMenu() {
//        System.out.println("1. Add user\n2. Delete user\n3. Update user");
//        System.out.println("4. Add friend\n5. Delete friend\n6. Update friend");
//        System.out.println("7. Show number of communities\n8. Show the most sociable community");
//        System.out.println("9. Get repository contents");
//        System.out.println("0. Exit");
//    }
//
//    private void addUserUi(){
//        System.out.print("Enter first name: ");
//        String firstName = scanner.next();
//        System.out.print("Enter last name: ");
//        String lastName = scanner.next();
//        System.out.println("\nUser " + service.addUser(firstName, lastName) + " added\n");
//    }
//
//    private void deleteUserUi() {
//        if(service.getNumberOfEntitiesInRepository(RepositoryStrategy.USER) == 0) {
//            System.out.println("The user repository is empty\n");
//            return;
//        }
//        printRepository(service.getRepositoryContent(RepositoryStrategy.USER));
//        System.out.print("Enter id: ");
//        long id = scanner.nextLong();
//        System.out.println("\nUser " + service.deleteUser(id) + " deleted\n");
//    }
//
//    private void updateUserUi() {
//        if(service.getNumberOfEntitiesInRepository(RepositoryStrategy.USER) == 0) {
//            System.out.println("The user repository is empty\n");
//            return;
//        }
//        printRepository(service.getRepositoryContent(RepositoryStrategy.USER));
//        System.out.print("Enter id: ");
//        long id = scanner.nextLong();
//        System.out.print("Enter firstName: ");
//        String firstName = scanner.next();
//        System.out.print("Enter lastName: ");
//        String lastName = scanner.next();
//        System.out.println("\nUser " + service.updateUser(id, firstName, lastName) + " updated\n");
//    }
//
//    private void addFriendUi() {
//        if(service.getNumberOfEntitiesInRepository(RepositoryStrategy.USER) < 2) {
//            System.out.println("There are less than 2 users in the repository\n");
//            return;
//        }
//        printRepository(service.getRepositoryContent(RepositoryStrategy.USER));
//        System.out.print("Select user: ");
//        Long idUser = scanner.nextLong();
//        System.out.print("Select friend: ");
//        Long idFriend = scanner.nextLong();
//        System.out.println("\nFriend " + service.addFriend(idUser, idFriend) + " added\n");
//    }
//
//    private void deleteFriendUi() {
//        if(service.getNumberOfEntitiesInRepository(RepositoryStrategy.FRIEND) == 0) {
//            System.out.println("The friend repository is empty\n");
//            return;
//        }
//        printRepository(service.getRepositoryContent(RepositoryStrategy.FRIEND));
//        System.out.print("Enter id: ");
//        Long idFriend = scanner.nextLong();
//        System.out.println("\nFriend " + service.deleteFriend(idFriend) + " deleted\n");
//    }
//
//    private void updateFriendUi() {
//        if(service.getNumberOfEntitiesInRepository(RepositoryStrategy.FRIEND) == 0) {
//            System.out.println("The friend repository is empty\n");
//            return;
//        }
//        printRepository(service.getRepositoryContent(RepositoryStrategy.FRIEND));
//        System.out.print("Enter id: ");
//        Long idFriend = scanner.nextLong();
//        printRepository(service.getRepositoryContent(RepositoryStrategy.USER));
//        System.out.print("Select friend: ");
//        Long idNewFriend = scanner.nextLong();
//        System.out.println("\nFriend " + service.updateFriend(idFriend, idNewFriend) + " updated\n");
//    }
//
//    private void getCommunitiesUi() {
//        System.out.println("\nNumber of communities: " + service.getNumberOfCommunities() + "\n");
//    }
//
//    private void getBiggestCommunityUi(){
//        System.out.println("\nBiggest community: ");
//        service.biggestCommunity().forEach(System.out::println);
//        System.out.println();
//    }
//
//    private void getRepositoryContentsUi(){
//        System.out.println("1. User repository\n2. Friend repository");
//        System.out.print("\nEnter your choice: ");
//        char command = scanner.next().charAt(0);
//        switch (command) {
//            case '1' -> printRepository(service.getRepositoryContent(RepositoryStrategy.USER));
//            case '2' -> printRepository(service.getRepositoryContent(RepositoryStrategy.FRIEND));
//            default -> System.out.print("Invalid command");
//        }
//    }
//
//    public void run() {
////        while (true) {
////            printMenu();
////            System.out.print("\nEnter your choice: ");
////            char command = scanner.next().charAt(0);
////            try {
////                switch (command) {
////                    case '1' -> addUserUi();
////                    case '2' -> deleteUserUi();
////                    case '3' -> updateUserUi();
////                    case '4' -> addFriendUi();
////                    case '5' -> deleteFriendUi();
////                    case '6' -> updateFriendUi();
////                    case '7' -> getCommunitiesUi();
////                    case '8' -> getBiggestCommunityUi();
////                    case '9' -> getRepositoryContentsUi();
////                    case '0' -> {
////                        return;
////                    }
////                    default -> System.out.println("Invalid choice");
////                }
////            }
////            catch(MyException e) {
////                System.out.println(e.getMessage());
////            }
////            catch(InputMismatchException e) {
////                System.out.println("Invalid format input");
////            }
////        }
//        printRepository(service.getRepositoryContent(RepositoryStrategy.USER));
//    }
//}
