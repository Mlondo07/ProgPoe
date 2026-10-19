/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.progpoe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Message class, using the test data from the assignment.
 *
 * @author Student
 */
public class MessageTest {

    private static final String FIXED_ID = "0012345678";
    private static final String MSG_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String MSG_2 = "Hi Keegan, did you receive the payment?";

    // Test data 1 and 2 from the assignment (fixed ID so the hash can be asserted)
    private Message message1() {
        return new Message(FIXED_ID, 0, "+27718693002", MSG_1);
    }

    private Message message2() {
        return new Message(FIXED_ID, 1, "08575975889", MSG_2);
    }

    @BeforeEach
    public void resetCounters() {
        Message.resetSentMessages();
    }

    // ---------- Message length (success and failure) ----------

    @Test
    public void testMessageLengthSuccess() {
        assertEquals("Message ready to send.", message1().validateMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        Message msg = new Message(FIXED_ID, 0, "+27718693002", "A".repeat(255));
        assertEquals("Message exceeds 250 characters by 5; please reduce the size.",
                msg.validateMessageLength());
    }

    // ---------- Recipient number ----------

    @Test
    public void testRecipientCellSuccess() {
        assertEquals("Cell phone number successfully captured.", message1().checkRecipientCell());
    }

    @Test
    public void testRecipientCellFailure() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                message2().checkRecipientCell());
    }

    // ---------- Message hash ----------

    @Test
    public void testMessageHashTestCase1() {
        assertEquals("00:0:HITONIGHT", message1().createMessageHash());
    }

    @Test
    public void testMessageHashLoopAllMessages() {
        String[] messages = {MSG_1, MSG_2};
        String[] expected = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};

        for (int i = 0; i < messages.length; i++) {
            Message msg = new Message(FIXED_ID, i, "+27718693002", messages[i]);
            assertEquals(expected[i], msg.createMessageHash());
        }
    }

    // ---------- Message ID ----------

    @Test
    public void testMessageIDCreatedStatement() {
        assertEquals("Message ID generated: " + FIXED_ID, message1().messageIDStatement());
    }

    @Test
    public void testRandomMessageIDIsTenDigits() {
        Message msg = new Message(0, "+27718693002", MSG_1);
        assertTrue(msg.checkMessageID());
        assertEquals(10, msg.getMessageID().length());
        assertTrue(msg.getMessageID().matches("[0-9]{10}"));
    }

    // ---------- Send / disregard / store ----------

    @Test
    public void testSentMessageSend() {
        assertEquals("Message successfully sent.", message1().SentMessage(1));
    }

    @Test
    public void testSentMessageDisregard() {
        assertEquals("Press 0 to delete the message.", message2().SentMessage(2));
    }

    @Test
    public void testSentMessageStore() throws IOException {
        Path file = Files.createTempFile("messages", ".json");
        Files.delete(file);
        Message msg = message1();
        msg.setStorageFile(file.toString());

        assertEquals("Message successfully stored.", msg.SentMessage(3));
        assertTrue(Files.exists(file));
        Files.deleteIfExists(file);
    }

    // ---------- Total messages ----------

    @Test
    public void testTotalMessagesSentAndDisregarded() {
        message1().SentMessage(1);   // Send
        message2().SentMessage(2);   // Disregard
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void testPrintMessagesShowsOnlySentMessages() {
        message1().SentMessage(1);
        message2().SentMessage(2);
        String printed = Message.printMessages();

        assertTrue(printed.contains(MSG_1));
        assertFalse(printed.contains(MSG_2));
    }

    // ---------- JSON storage ----------

    @Test
    public void testStoredMessagesAreValidJsonArray() throws IOException {
        Path file = Files.createTempFile("messages", ".json");
        Files.delete(file);

        Message first = message1();
        Message second = new Message("0099999999", 1, "+27718693002", "Say \"hi\" now");
        first.setStorageFile(file.toString());
        second.setStorageFile(file.toString());
        first.storeMessage();
        second.storeMessage();

        String json = new String(Files.readAllBytes(file), StandardCharsets.UTF_8).trim();
        assertTrue(json.startsWith("[") && json.endsWith("]"));
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
        assertTrue(json.contains("Say \\\"hi\\\" now"));
        assertEquals(2, json.split("\"messageID\"", -1).length - 1);
        Files.deleteIfExists(file);
    }
}