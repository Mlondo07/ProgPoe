/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.progpoe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Part 1 unit tests (registration and login), using the test data from the assignment.
 *
 * @author Student
 */
public class ProgPoeTest {

    public static final String GOOD_USER = "kyl_1";
    public static final String GOOD_PASS = "Ch&&sec@ke99!";
    public static final String GOOD_CELL = "+27838968976";

    private Poelogin valid() {
        return new Poelogin("Kyl", "Smith", GOOD_USER, GOOD_PASS, GOOD_CELL);
    }

    // ---------- assertEquals ----------

    @Test
    public void testUsernameCorrectlyFormatted() {
        assertEquals("Username successfully captured.", valid().usernameStatus());
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyle!!!!!!!", GOOD_PASS, GOOD_CELL);
        assertEquals("Username is not correctly formatted; please ensure that your username "
                + "contains an underscore and is no more than five characters in length.",
                login.usernameStatus());
    }

    @Test
    public void testPasswordMeetsComplexity() {
        assertEquals("Password successfully captured.", valid().passwordStatus());
    }

    @Test
    public void testPasswordDoesNotMeetComplexity() {
        Poelogin login = new Poelogin("Kyl", "Smith", GOOD_USER, "password", GOOD_CELL);
        assertEquals("Password is not correctly formatted; please ensure that the password "
                + "contains at least eight characters, a capital letter, a number, and a special character.",
                login.passwordStatus());
    }

    @Test
    public void testCellPhoneCorrectlyFormatted() {
        assertEquals("Cell number successfully captured.", valid().cellPhoneStatus());
    }

    @Test
    public void testCellPhoneIncorrectlyFormatted() {
        Poelogin login = new Poelogin("Kyl", "Smith", GOOD_USER, GOOD_PASS, "08966553");
        assertEquals("Cell number is incorrectly formatted or does not contain an international "
                + "code; please correct the number and try again.", login.cellPhoneStatus());
    }

    @Test
    public void testRegisterUserSuccess() {
        assertEquals("Username successfully captured. Password successfully captured. "
                + "Cell number successfully captured. User has been registered successfully.",
                valid().registerUser());
    }

    @Test
    public void testRegisterUserStopsAtFirstError() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyle!!!!!!!", "password", "08966553");
        assertEquals(Poelogin.USERNAME_BAD, login.registerUser());
    }

    // ---------- assertTrue / assertFalse ----------

    @Test
    public void testLoginSuccessful() {
        assertTrue(valid().loginUser(GOOD_USER, GOOD_PASS));
    }

    @Test
    public void testLoginFailed() {
        assertFalse(valid().loginUser("wrongUser", "wrongPass"));
    }

    @Test
    public void testLoginStatusMessages() {
        assertEquals("Welcome Kyl Smith, it is great to see you again.",
                valid().returnLoginStatus(GOOD_USER, GOOD_PASS));
        assertEquals("Username or password incorrect, please try again.",
                valid().returnLoginStatus("wrongUser", "wrongPass"));
    }

    @Test
    public void testUsernameCorrectlyFormattedTrue() {
        assertTrue(valid().checkUserName());
    }

    @Test
    public void testUsernameIncorrectlyFormattedFalse() {
        assertFalse(new Poelogin("Kyl", "Smith", "kyle!!!!!!!", GOOD_PASS, GOOD_CELL).checkUserName());
    }

    @Test
    public void testPasswordMeetsComplexityTrue() {
        assertTrue(valid().checkPasswordComplexity());
    }

    @Test
    public void testPasswordDoesNotMeetComplexityFalse() {
        assertFalse(new Poelogin("Kyl", "Smith", GOOD_USER, "password", GOOD_CELL).checkPasswordComplexity());
    }

    @Test
    public void testCellPhoneCorrectlyFormattedTrue() {
        assertTrue(valid().checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneIncorrectlyFormattedFalse() {
        assertFalse(new Poelogin("Kyl", "Smith", GOOD_USER, GOOD_PASS, "08966553").checkCellPhoneNumber());
    }
}