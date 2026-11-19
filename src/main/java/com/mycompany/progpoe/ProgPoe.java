/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.progpoe;

import java.util.Scanner;

/**
 *
 * @author Student
 */
public class ProgPoe {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=== User Registration ===");

        System.out.println("Please enter your name (e.g. Kyl):");
        String name = input.nextLine().trim();

        System.out.println("Please enter your surname (e.g. Smith):");
        String surname = input.nextLine().trim();

        // Each detail is asked again until it is valid, so one typo does not end the program
        String username;
        do {
            System.out.println("Please enter your username (an underscore is required, maximum "
                    + "5 characters, e.g. kyl_1):");
            username = input.nextLine().trim();
            if (!Poelogin.isUsernameValid(username)) {
                System.out.println(Poelogin.USERNAME_BAD);
            }
        } while (!Poelogin.isUsernameValid(username));

        String password;
        do {
            System.out.println("Please enter your password (at least 8 characters with a capital "
                    + "letter, a number and a special character, e.g. Pass@1234):");
            password = input.nextLine().trim();
            if (!Poelogin.isPasswordValid(password)) {
                System.out.println(Poelogin.PASSWORD_BAD);
            }
        } while (!Poelogin.isPasswordValid(password));

        String cellphone;
        do {
            System.out.println("Please enter your cell phone number with the international code "
                    + "(e.g. +27831234567):");
            cellphone = input.nextLine().trim().replace(" ", "");
            if (!Poelogin.isCellPhoneValid(cellphone)) {
                System.out.println(Poelogin.CELL_BAD);
            }
        } while (!Poelogin.isCellPhoneValid(cellphone));

        Poelogin user = new Poelogin(name, surname, username, password, cellphone);
        System.out.println(user.registerUser());

        // Login (the user only gets into QuickChat if login is successful)
        System.out.println();
        System.out.println("=== User Login ===");
        boolean isLoggedIn = false;
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS && !isLoggedIn; attempt++) {
            System.out.print("Enter your username (as registered, e.g. kyl_1): ");
            String loginUsername = input.nextLine().trim();
            System.out.print("Enter your password (as registered): ");
            String loginPassword = input.nextLine().trim();

            System.out.println(user.returnLoginStatus(loginUsername, loginPassword));
            isLoggedIn = user.loginUser(loginUsername, loginPassword);
        }

        if (!isLoggedIn) {
            System.out.println("Too many failed login attempts. Exiting application.");
            input.close();
            return;
        }

        runQuickChat(input, name + " " + surname);
        input.close();
    }

    /**
     * Runs the QuickChat menu until the user chooses to quit.
     */
    private static void runQuickChat(Scanner input, String senderName) {
        System.out.println("\nWelcome to QuickChat.");

        // Part 3: the sender of every new message, and read the stored messages (JSON) into the array
        Message.setCurrentSender(senderName);
        int loaded = Message.loadStoredMessages(Message.DEFAULT_STORAGE_FILE);
        if (loaded > 0) {
            System.out.println(loaded + " stored message(s) loaded from " + Message.DEFAULT_STORAGE_FILE + ".");
        }

        // The user defines the number of messages once, when the application starts
        int numMessages = readInt(input, "How many messages do you wish to enter? ", 1, 1000);
        boolean messagesEntered = false;
        boolean running = true;

        // Application loop: runs until the user selects Quit
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            int option = readInt(input, "Choose an option: ", 1, 4);

            switch (option) {
                case 1:
                    if (messagesEntered) {
                        System.out.println("You have already entered your " + numMessages + " message(s).");
                    } else {
                        enterMessages(input, numMessages);
                        messagesEntered = true;
                    }
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    running = false;
                    System.out.println("Exiting QuickChat. Goodbye!");
                    break;
                case 4:
                    storedMessagesMenu(input);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Lets the user enter exactly numMessages messages using a for loop.
     * The loop counter is used as the message number.
     */
    private static void enterMessages(Scanner input, int numMessages) {
        for (int i = 0; i < numMessages; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

            // Step 1: recipient - checked straight away so the user can correct it
            String recipient;
            while (true) {
                System.out.print("Enter recipient cell number (e.g. +27831234567): ");
                recipient = input.nextLine().replaceAll("[\\s-]", "");
                if (Message.isRecipientNumberValid(recipient)) {
                    break;
                }
                System.out.println("Cell phone number is incorrectly formatted or does not contain an "
                        + "international code. Please correct the number and try again.");
            }

            // Step 2: the message text (maximum 250 characters)
            Message msg;
            while (true) {
                System.out.print("Enter your message (max 250 characters): ");
                String content = input.nextLine();
                msg = new Message(i, recipient, content);

                if (content.trim().isEmpty()) {
                    System.out.println("The message cannot be empty.");
                } else if (!msg.isMessageLengthValid()) {
                    System.out.println("Please enter a message of less than 250 characters.");
                    System.out.println(msg.validateMessageLength());
                } else {
                    break;
                }
            }

            System.out.println(msg.checkRecipientCell());
            System.out.println(msg.validateMessageLength());
            System.out.println(msg.messageIDStatement());

            System.out.println("\nChoose a message action:");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            int choice = readInt(input, "Selection: ", 1, 3);

            System.out.println(msg.SentMessage(choice));

            if (choice == 1) {
                System.out.println("\n--- Sent Message Details ---");
                System.out.println(msg.messageDetails());
            } else if (choice == 2) {
                readInt(input, "> ", 0, 0);   // user must press 0
                System.out.println("Message deleted.");
            }
        }

        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
    }

    /**
     * Part 3: the Stored Messages menu. Uses the arrays held in the Message class.
     */
    private static void storedMessagesMenu(Scanner input) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("1) Display the sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages for a particular recipient");
            System.out.println("5) Delete a message using its message hash");
            System.out.println("6) Display a report of all stored messages");
            System.out.println("7) Back to main menu");
            int choice = readInt(input, "Choose an option: ", 1, 7);

            switch (choice) {
                case 1:
                    System.out.println(Message.displaySenderAndRecipientOfStored());
                    break;
                case 2:
                    System.out.println("Longest stored message:\n" + Message.longestStoredMessageDetails());
                    break;
                case 3:
                    System.out.print("Enter the message ID (e.g. 0012345678): ");
                    System.out.println(Message.searchByMessageIDDetails(input.nextLine()));
                    break;
                case 4:
                    System.out.print("Enter the recipient number (e.g. +27838884567): ");
                    java.util.List<String> found = Message.searchByRecipient(input.nextLine());
                    if (found.isEmpty()) {
                        System.out.println("No messages found for that recipient.");
                    } else {
                        for (String text : found) {
                            System.out.println("- " + text);
                        }
                    }
                    break;
                case 5:
                    System.out.print("Enter the message hash (e.g. 00:0:HITONIGHT): ");
                    System.out.println(Message.deleteMessageByHash(input.nextLine()));
                    break;
                case 6:
                    System.out.println(Message.displayStoredReport());
                    break;
                case 7:
                    back = true;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Reads a whole number between min and max (inclusive). Keeps asking until
     * valid, so letters or blank input never crash the program.
     */
    private static int readInt(Scanner input, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // fall through to the error message below
            }
            System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
        }
    }
}