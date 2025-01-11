package ui;

import domain.Friendship;
import domain.User;
import domain.validators.ValidationException;
import service.SocialCommunity;
import service.SocialNetwork;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Console {

    private SocialNetwork socialNetwork;
    private SocialCommunity socialCommunities;

    public Console(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
        this.socialCommunities = new SocialCommunity(socialNetwork);
    }

    void printMenu() {
        System.out.println("\t\t\tMenu\t\t\t");
        System.out.println("1. Add user");
        System.out.println("2. Remove user");
        System.out.println("3. Add friendship");
        System.out.println("4. Remove friendship");
        System.out.println("5. Print users");
        System.out.println("6. Print friendships");
        System.out.println("7. Number of communities");
        System.out.println("8. Most sociable community");
        System.out.println("0. Exit");
        System.out.println("Choose one option:");
    }

    public void run() {
        Scanner scan = new Scanner(System.in);
        boolean ok = true;
        while (ok) {
            printMenu();
            String input = scan.nextLine();
            switch (input) {
                case "1" -> addUser();
                case "2" -> removeUser();
                case "3" -> addFriendship();
                case "4" -> removeFriendship();
                case "5" -> printUsers();
                case "6" -> printFriendships();
                case "7" -> printConnectedCommunities();
                case "8" -> printMostSocialCommunity();
                case "0" -> {
                    ok = false;
                    System.out.println("Bye bye!");
                }
                default -> System.out.println("Invalid input!");
            }
        }
    }


    void printUsers() {
        System.out.println("\t\t\tUsers\t\t\t");
        for (User u : socialNetwork.getUsers()) {
            System.out.println("ID:" + u.getId() + " Username:" + u.getUsername() + " Email:" + u.getEmail());
        }
    }


    void addUser() {
        System.out.println("Add user");
        Scanner scan = new Scanner(System.in);
        System.out.println("Username: ");
        String username = scan.nextLine();
        System.out.println("Password: ");
        String password = scan.nextLine();
        System.out.println("Email: ");
        String email = scan.nextLine();
        String salt = scan.nextLine();
        byte[] defaultProfilePicture = socialNetwork.getDefaultProfilePicture();
        try {
            socialNetwork.addUser(new User(username, password, email, salt, defaultProfilePicture));
        } catch (ValidationException e) {
            System.out.println("Invalid user!");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid argument!");
        }

    }


    void removeUser() {
        printUsers();
        System.out.println("Remove user");
        Scanner scan = new Scanner(System.in);
        System.out.println("Id: ");
        String var = scan.nextLine();
        try {
            Long id = Long.parseLong(var);
            User user = socialNetwork.removeUser(id);
            System.out.println("User: " + user.getId() + " " + user.getUsername() + " was removed.");
        } catch (IllegalArgumentException e) {
            System.out.println("ID must be a number! ");
        }
    }


    void printFriendships() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Friendship f : socialNetwork.getFriendships()) {
            String formattedDate = f.getFriendsFrom().format(formatter);
            System.out.println("ID Friendship:" + f.getId() + " ID Friend 1:" + f.getIdUser1() + " ID Friend 2:" + f.getIdUser2() + " Friends from:" + formattedDate);
        }
    }


    void addFriendship() {
        Scanner scan = new Scanner(System.in);
        System.out.println("ID of the first user: ");
        String var1 = scan.nextLine();
        System.out.println("ID of the second user: ");
        String var2 = scan.nextLine();
        try {
            Long id1 = 0L, id2 = 0L;
            try {
                id1 = Long.parseLong(var1);
                id2 = Long.parseLong(var2);
            } catch (IllegalArgumentException e) {
                System.out.println("ID must be a number!");
            }
            socialNetwork.addFriendship(new Friendship(id1, id2, LocalDateTime.now()));
        } catch (ValidationException e) {
            System.out.println("Friendship is invalid! ");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid arguments! ");
        }
    }


    private void removeFriendship() {
        Scanner scan = new Scanner(System.in);
        System.out.println("ID of the first user: ");
        String var1 = scan.nextLine();
        System.out.println("ID of the second user: ");
        String var2 = scan.nextLine();
        try {
            Long id1 = 0L, id2 = 0L;
            try {
                id1 = Long.parseLong(var1);
                id2 = Long.parseLong(var2);
            } catch (IllegalArgumentException e) {
                System.out.println("ID must be a number!");
            }
            socialNetwork.removeFriendship(id1, id2);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid arguments! ");
        }
    }


    private void printConnectedCommunities() {
        System.out.println("Social Communities:\n");
        int nrOfCommunities = socialCommunities.connectedCommunities();
        System.out.println("Number of social communities: " + nrOfCommunities);
    }

    private void printMostSocialCommunity() {
        System.out.println("Most social community: ");
        List<User> mostSocialCommunity = socialCommunities.mostSocialCommunity();
        mostSocialCommunity.forEach(user ->
                System.out.println("ID: " + user.getId() + " Username: " + user.getUsername())
        );
    }

}
