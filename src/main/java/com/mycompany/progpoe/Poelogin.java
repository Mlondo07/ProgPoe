/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.progpoe;

import java.util.regex.Pattern;

/**
 *
 * @author Student
 */
public class Poelogin {
  public String username;
    public String password;
    public String cellphone;
    public String name;
    public String surname;

    // Password: at least 8 chars, 1 capital letter, 1 number, 1 special character
    private static final Pattern CAPITAL_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern NUMBER_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL_PATTERN = Pattern.compile(".*[^a-zA-Z0-9].*");

    // Constructor updated to receive name and surname properly
    public Poelogin(String name, String surname, String username, String password, String cellphone) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.cellphone = cellphone;
    }

    // Method to ensure the username is correct (contains "_" and < 5 chars)
    public boolean CheckUsername() {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    // Method to ensure the password is correct
    public boolean CheckPassword() {
        if (password == null) {
            return false;
        }
        return password.length() >= 8 
                && CAPITAL_PATTERN.matcher(password).matches()
                && NUMBER_PATTERN.matcher(password).matches()
                && SPECIAL_PATTERN.matcher(password).matches();
    }

    // Fixed: +27 international format requires 12 characters (e.g., +27821234567)
    public boolean checkcellphone() {
        return cellphone != null && cellphone.startsWith("+27") && cellphone.length() == 12;
    }

    // Verifies user details during registration
    public String registerUser() {
        if (!CheckUsername()) {
            return "Username is not correctly formatted; please ensure that your username "
                    + "contains an underscore and is no more than five characters in length.";
        }

        if (!CheckPassword()) {
            return "Password is not correctly formatted; please ensure that the password "
                    + "contains at least eight characters, a capital letter, a number, and "
                    + "a special character.";
        }

        if (!checkcellphone()) {
            return "Cell number is incorrectly formatted or does not contain an international "
                    + "code; please correct the number and try again.";
        }

        return "Username successfully captured. Password successfully captured. "
                + "Cell phone number successfully captured. User has been registered successfully.";
    }

    //  accepts login parameters to compare against stored credentials
    public boolean loginUser(String inputUsername, String inputPassword) {
        if (this.username == null || this.password == null) {
            return false;
        }
        return this.username.equals(inputUsername) && this.password.equals(inputPassword);
    }

    //  Passes credentials through to loginUser
    public String returnLoginStatus(String inputUsername, String inputPassword) {
        if (loginUser(inputUsername, inputPassword)) {
            return "Welcome " + name + " " + surname + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}

  
  
  
       
     
  


