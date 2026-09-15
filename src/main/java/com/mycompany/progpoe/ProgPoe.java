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

        // Pass parameters in matching order
        Poelogin obj = new Poelogin(name, surname, username, password, cellphone);

        // Execute registration check and print status message
        String registrationMessage = obj.registerUser();
        System.out.println(registrationMessage);

        // --- Login ---
        // Only attempt login if registration actually succeeded
        if (registrationMessage.contains("registered successfully")) {
            System.out.println();
            System.out.println("=== User Login ===");

            System.out.print("Enter your username: ");
            String loginUsername = Input.nextLine();

            System.out.print("Enter your password: ");
            String loginPassword = Input.nextLine();

            // Call returnLoginStatus using 'obj' and pass typed login credentials
            String loginStatusMessage = obj.returnLoginStatus(loginUsername, loginPassword);
            System.out.println(loginStatusMessage);
        }

        Input.close();
    }
       
    
}
