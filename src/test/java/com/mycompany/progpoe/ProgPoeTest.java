/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.progpoe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Student
 */
public class ProgPoeTest {
   @Test
    public void testUsernameCorrectlyFormatted() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyl_1", "Ch&&sec&ke99", "+27838968978");
        String expected = "Username successfully captured. Password successfully captured. Cell phone number successfully captured. User has been registered successfully.";
        String actual = login.registerUser();
        assertEquals(expected, actual);
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyle!!!!!!!", "Ch&&sec&ke99", "+27838968978");
        String expected = "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        String actual = login.registerUser();
        assertEquals(expected, actual);
    }

    @Test
    public void testPasswordMeetsComplexityRequirements() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyl_1", "Ch&&sec&ke99", "+27838968978");
        assertTrue(login.CheckPassword());
    }

    @Test
    public void testPasswordDoesNotMeetComplexityRequirements() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyl_1", "password", "+27838968978");
        String expected = "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        String actual = login.registerUser();
        assertEquals(expected, actual);
    }

    @Test
    public void testCellPhoneNumberCorrectlyFormatted() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyl_1", "Ch&&sec&ke99", "+27838968978");
        assertTrue(login.checkcellphone());
    }

    @Test
    public void testCellPhoneNumberIncorrectlyFormatted() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyl_1", "Ch&&sec&ke99", "0838968978");
        String expected = "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        String actual = login.registerUser();
        assertEquals(expected, actual);
    }

    // --- assertTrue / assertFalse Tests ---

    @Test
    public void testLoginSuccessful() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyl_1", "Ch&&sec&ke99", "+27838968978");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec&ke99"));
    }

    @Test
    public void testLoginFailed() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyl_1", "Ch&&sec&ke99", "+27838968978");
        assertFalse(login.loginUser("wrongUser", "wrongPass"));
    }

    @Test
    public void testUsernameCheckTrue() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyl_1", "Ch&&sec&ke99", "+27838968978");
        assertTrue(login.CheckUsername());
    }

    @Test
    public void testUsernameCheckFalse() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyle!!!!!!!", "Ch&&sec&ke99", "+27838968978");
        assertFalse(login.CheckUsername());
    }

    @Test
    public void testPasswordComplexityTrue() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyl_1", "Ch&&sec&ke99", "+27838968978");
        assertTrue(login.CheckPassword());
    }

    @Test
    public void testPasswordComplexityFalse() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyl_1", "password", "+27838968978");
        assertFalse(login.CheckPassword());
    }

    @Test
    public void testCellPhoneNumberTrue() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyl_1", "Ch&&sec&ke99", "+27838968978");
        assertTrue(login.checkcellphone());
    }

    @Test
    public void testCellPhoneNumberFalse() {
        Poelogin login = new Poelogin("Kyl", "Smith", "kyl_1", "Ch&&sec&ke99", "0838968978");
        assertFalse(login.checkcellphone());
    }
}
