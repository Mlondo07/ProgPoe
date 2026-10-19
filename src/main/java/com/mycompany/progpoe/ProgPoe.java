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

    public static final int MAX_LOGIN_ATTEMPTS = 3;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=== User Registration ===");

        System.out.println("Please enter your name:");
        String name = input.nextLine();

        System.out.println("Please enter your surname:");
        String surname = input.nextLine();

        System.out.println("Please enter your username (it must include an underscore):");
        String username = input.nextLine();

        System.out.println("Please enter your password (at least 8 characters, a capital letter, "
                + "a number and a special character):");
        String password = input.nextLine();

        System.out.println("Please enter your cellphone number (with the international code, e.g. +27...):");
        String cellphone = input.nextLine();

        Poelogin user = new Poelogin(name, surname, username, password, cellphone);

        String registrationMessage = user.registerUser();
        System.out.println(registrationMessage);

        if (!registrationMessage.contains("registered successfully")) {
            System.out.println("Registration failed. Exiting application.");
            input.close();
            return;
        }

        // Login (the user only gets into QuickChat if login is successful)
        System.out.println();
        System.out.println("=== User Login ===");
        boolean isLoggedIn = false;
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS && !isLoggedIn; attempt++) {
            System.out.print("Enter your username: ");
            String loginUsername = input.nextLine();
            System.out.print("Enter your password: ");
            String loginPassword = input.nextLine();

            System.out.println(user.returnLoginStatus(loginUsername, loginPassword));
            isLoggedIn = user.loginUser(loginUsername, loginPassword);
        }

        if (!isLoggedIn) {
            System.out.println("Too many failed login attempts. Exiting application.");
            input.close();
            return;
        }

        runQuickChat(input);
        input.close();
    }

    /**
     * Runs the QuickChat menu until the user chooses to quit.
     */
    private static void runQuickChat(Scanner input) {
        System.out.println("\nWelcome to QuickChat.");

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
            int option = readInt(input, "Choose an option: ", 1, 3);

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

            Message msg;
            while (true) {
                System.out.print("Enter recipient cell number (+27 format): ");
                String recipient = input.nextLine().trim();
                System.out.print("Enter your message (max 250 characters): ");
                String content = input.nextLine();

                msg = new Message(i, recipient, content);

                if (!msg.isRecipientValid()) {
                    System.out.println(msg.checkRecipientCell());
                    continue;
                }
                if (!msg.isMessageLengthValid()) {
                    System.out.println("Please enter a message of less than 250 characters.");
                    System.out.println(msg.validateMessageLength());
                    continue;
                }
                break;
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