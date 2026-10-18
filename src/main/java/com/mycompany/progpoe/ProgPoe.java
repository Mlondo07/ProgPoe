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

    public static void main(String[] args) {

        Scanner Input = new Scanner(System.in);

        System.out.println("=== User Registration ===");

        System.out.println("hello please enter your name");
        String name = Input.nextLine();

        System.out.println("while your at please enter your surname");
        String surname = Input.nextLine();

        System.out.println("hello please enter your username please include uderscore");
        String username = Input.nextLine();

        System.out.println("please enter your password ensure to have atleast a ccapital letter"
                + ",special charatater and one number ");
        String password = Input.nextLine();

        System.out.println("please enter your cellphone number.ensure that you use the correct code");
        String cellphone = Input.nextLine();

        Poelogin obj = new Poelogin(name, surname, username, password, cellphone);

        String registrationMessage = obj.registerUser();
        System.out.println(registrationMessage);

        boolean isLoggedIn = false;

        if (registrationMessage.contains("registered successfully") || registrationMessage.contains("successfully captured") || registrationMessage.contains("successfully added")) {
            System.out.println();
            System.out.println("=== User Login ===");

            System.out.print("Enter your username: ");
            String loginUsername = Input.nextLine();

            System.out.print("Enter your password: ");
            String loginPassword = Input.nextLine();

            String loginStatusMessage = obj.returnLoginStatus(loginUsername, loginPassword);
            System.out.println(loginStatusMessage);

            isLoggedIn = obj.loginUser(loginUsername, loginPassword);
        } else {
            System.out.println("Registration failed. Exiting application.");
        }

        if (isLoggedIn) {
            System.out.println("\nWelcome to QuickChat.");
            boolean running = true;

            // Application while loop
            while (running) {
                System.out.println("\n--- Main Menu ---");
                System.out.println("1) Send Messages");
                System.out.println("2) Show recently sent messages");
                System.out.println("3) Quit");
                System.out.print("Choose an option: ");

                int option = Input.nextInt();
                Input.nextLine();

                switch (option) {
                    case 1:
                        System.out.print("\nHow many messages would you like to send? ");
                        int totalToProcess = Input.nextInt();
                        Input.nextLine();

                        // For loop using loop counter i for auto-incrementing message number
                        for (int i = 0; i < totalToProcess; i++) {
                            System.out.println("\n--- Message " + (i + 1) + " ---");

                            System.out.print("Enter recipient cell number (+27 format): ");
                            String recipient = Input.nextLine();

                            System.out.print("Enter short message (max 250 chars): ");
                            String msgContent = Input.nextLine();

                            Message msg = new Message(i, recipient, msgContent);

                            String cellCheck = msg.checkRecipientCell();
                            if (cellCheck.contains("incorrectly")) {
                                System.out.println(cellCheck);
                                i--;
                                continue;
                            }

                            String lenCheck = msg.validateMessageLength();
                            if (lenCheck.contains("exceeds")) {
                                System.out.println(lenCheck);
                                i--;
                                continue;
                            }

                            System.out.println("\nMessage status: " + lenCheck);
                            System.out.println("Message ID generated: " + msg.getMessageID());

                            System.out.println("\nChoose message action:");
                            System.out.println("1) Send Message");
                            System.out.println("2) Disregard Message");
                            System.out.println("3) Store Message to send later");
                            System.out.print("Selection: ");
                            int actionChoice = Input.nextInt();
                            Input.nextLine();

                            String actionStatus = msg.SentMessage(actionChoice);
                            System.out.println(actionStatus);

                            if (actionChoice == 1) {
                                System.out.println("\n--- Sent Message Details ---");
                                System.out.println(msg.printMessages());
                            }
                        }

                        System.out.println("\nTotal messages sent so far: " + Message.returnTotalMessages());
                        break;

                    case 2:
                        System.out.println("Coming Soon.");
                        break;

                    case 3:
                        running = false;
                        System.out.println("Exiting QuickChat. Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid option selected. Please enter 1, 2, or 3.");
                        break;
                }
            }
        }

        Input.close();
    }
}