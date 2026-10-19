/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.progpoe;

import java.util.regex.Pattern;

/**
 * Handles user registration and login for QuickChat.
 *
 * @author Student
 */
public class Poelogin {

    // Messages required by the assignment
    public static final String USERNAME_OK = "Username successfully captured.";
    public static final String USERNAME_BAD = "Username is not correctly formatted; please ensure "
            + "that your username contains an underscore and is no more than five characters in length.";
    public static final String PASSWORD_OK = "Password successfully captured.";
    public static final String PASSWORD_BAD = "Password is not correctly formatted; please ensure "
            + "that the password contains at least eight characters, a capital letter, a number, "
            + "and a special character.";
    public static final String CELL_OK = "Cell number successfully captured.";
    public static final String CELL_BAD = "Cell number is incorrectly formatted or does not contain "
            + "an international code; please correct the number and try again.";

    private static final Pattern CAPITAL = Pattern.compile(".*[A-Z].*");
    private static final Pattern NUMBER = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL = Pattern.compile(".*[^a-zA-Z0-9].*");
    // International code +27 followed by a number of no more than ten digits
    private static final Pattern CELL = Pattern.compile("^\\+27[0-9]{9,10}$");

    private final String name;
    private final String surname;
    private final String username;
    private final String password;
    private final String cellphone;

    public Poelogin(String name, String surname, String username, String password, String cellphone) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.cellphone = cellphone;
    }

    // ----- Static validators (also used to re-ask a single field during registration) -----

    /** Username must contain an underscore and be no more than five characters long. */
    public static boolean isUsernameValid(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    /** Password: at least eight characters, a capital letter, a number and a special character. */
    public static boolean isPasswordValid(String password) {
        return password != null
                && password.length() >= 8
                && CAPITAL.matcher(password).matches()
                && NUMBER.matcher(password).matches()
                && SPECIAL.matcher(password).matches();
    }

    /** Cell number must start with the +27 international code and have no more than ten digits after it. */
    public static boolean isCellPhoneValid(String cellphone) {
        return cellphone != null && CELL.matcher(cellphone).matches();
    }

    // ----- Methods required by the assignment -----

    public boolean checkUserName() {
        return isUsernameValid(username);
    }

    public boolean checkPasswordComplexity() {
        return isPasswordValid(password);
    }

    public boolean checkCellPhoneNumber() {
        return isCellPhoneValid(cellphone);
    }

    /** Message for the username check (used for assertEquals tests). */
    public String usernameStatus() {
        return checkUserName() ? USERNAME_OK : USERNAME_BAD;
    }

    /** Message for the password check. */
    public String passwordStatus() {
        return checkPasswordComplexity() ? PASSWORD_OK : PASSWORD_BAD;
    }

    /** Message for the cell phone check. */
    public String cellPhoneStatus() {
        return checkCellPhoneNumber() ? CELL_OK : CELL_BAD;
    }

    /**
     * Checks the registration details in order and returns the matching message.
     */
    public String registerUser() {
        if (!checkUserName()) {
            return USERNAME_BAD;
        }
        if (!checkPasswordComplexity()) {
            return PASSWORD_BAD;
        }
        if (!checkCellPhoneNumber()) {
            return CELL_BAD;
        }
        return USERNAME_OK + " " + PASSWORD_OK + " " + CELL_OK
                + " User has been registered successfully.";
    }

    /**
     * Verifies that the login details match the details stored at registration.
     */
    public boolean loginUser(String inputUsername, String inputPassword) {
        if (username == null || password == null) {
            return false;
        }
        return username.equals(inputUsername) && password.equals(inputPassword);
    }

    /**
     * Returns the message for a successful or failed login.
     */
    public String returnLoginStatus(String inputUsername, String inputPassword) {
        if (loginUser(inputUsername, inputPassword)) {
            return "Welcome " + name + " " + surname + ", it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}