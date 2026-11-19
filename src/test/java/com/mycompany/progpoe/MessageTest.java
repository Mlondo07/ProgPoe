/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.progpoe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * 
 *
 * @author Student
 */
public class MessageTest {

    public static final String FIXED_ID = "0012345678";
    public static final String MSG_1 = "Hi Mike, can you join us for dinner tonight?";
    public static final String MSG_2 = "Hi Keegan, did you receive the payment?";

    // Test data 1 and 2 from the assignment (fixed ID so the hash can be asserted)
    public Message message1() {
        return new Message(FIXED_ID, 0, "+27718693002", MSG_1);
    }

    public Message message2() {
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
    
    /**
 * Part 3 unit tests: arrays, searching, deleting, reports and reading the JSON file.
 * Uses the five test messages from the assignment.
 *
 * @author Student
 */
public class StoredMessagesTest {

    private static final int SEND = 1;
    private static final int DISREGARD = 2;
    private static final int STORE = 3;

    private static final String MSG_1 = "Did you get the cake?";
    private static final String MSG_2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String MSG_3 = "Yohoooo, I am at your gate.";
    private static final String MSG_4 = "It is dinner time!";
    private static final String MSG_5 = "Ok, I am leaving without you.";

    private static final String ID_1 = "0000000001";
    private static final String ID_2 = "0000000002";
    private static final String ID_3 = "0000000003";
    private static final String ID_4 = "0000000004";
    private static final String ID_5 = "0000000005";

    private Path jsonFile;

    /** Creates the five test messages and gives each its flag (Sent / Stored / Disregard). */
    @BeforeEach
    public void populateTestData() throws IOException {
        Message.resetSentMessages();
        Message.setCurrentSender("Kyl Smith");
        jsonFile = Files.createTempFile("stored", ".json");
        Files.delete(jsonFile);

        flag(new Message(ID_1, 0, "+27834557896", MSG_1), SEND);
        flag(new Message(ID_2, 1, "+27838884567", MSG_2), STORE);
        flag(new Message(ID_3, 2, "+27834484567", MSG_3), DISREGARD);
        flag(new Message(ID_4, 3, "0838884567", MSG_4), SEND);
        flag(new Message(ID_5, 4, "+27838884567", MSG_5), STORE);
    }

    private void flag(Message message, int choice) {
        message.setStorageFile(jsonFile.toString());
        message.SentMessage(choice);
    }

    // ---------- Arrays correctly populated ----------

    @Test
    public void testSentMessagesArrayPopulated() {
        assertEquals(Arrays.asList("Did you get the cake?", "It is dinner time!"),
                Message.getSentMessageTexts());
    }

    @Test
    public void testDisregardedMessagesArrayPopulated() {
        assertEquals(Collections.singletonList(MSG_3), Message.getDisregardedMessageTexts());
    }

    @Test
    public void testStoredMessagesArrayPopulated() {
        assertEquals(Arrays.asList(MSG_2, MSG_5), Message.getStoredMessageTexts());
    }

    @Test
    public void testMessageIDArrayPopulated() {
        assertEquals(Arrays.asList(ID_1, ID_2, ID_3, ID_4, ID_5), Message.getMessageIDs());
    }

    @Test
    public void testMessageHashArrayPopulated() {
        assertEquals(Arrays.asList("00:0:DIDCAKE", "00:1:WHERETIME", "00:2:YOHOOOOGATE",
                "00:3:ITTIME", "00:4:OKYOU"), Message.getMessageHashes());
    }

    // ---------- Longest message ----------

    @Test
    public void testLongestStoredMessage() {
        assertEquals(MSG_2, Message.longestStoredMessage());
    }

    @Test
    public void testLongestStoredMessageDetails() {
        String details = Message.longestStoredMessageDetails();
        assertTrue(details.contains("Message: " + MSG_2));
        assertTrue(details.contains("Recipient: +27838884567"));
        assertTrue(details.contains("Message Hash: 00:1:WHERETIME"));
    }

    // ---------- Search for a message ID ----------

    @Test
    public void testSearchForMessageID() {
        assertEquals("It is dinner time!", Message.searchByMessageID(ID_4));
    }

    @Test
    public void testSearchForMessageIDShowsRecipientAndMessage() {
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!",
                Message.searchByMessageIDDetails(ID_4));
    }

    @Test
    public void testSearchForMessageIDNotFound() {
        assertEquals("No message found with ID: 9999999999", Message.searchByMessageID("9999999999"));
    }

    // ---------- Search for a recipient ----------

    @Test
    public void testSearchMessagesForRecipient() {
        assertEquals(Arrays.asList(MSG_2, MSG_5), Message.searchByRecipient("+27838884567"));
    }

    @Test
    public void testSearchMessagesForUnknownRecipient() {
        assertTrue(Message.searchByRecipient("+27000000000").isEmpty());
    }

    // ---------- Delete using the message hash ----------

    @Test
    public void testDeleteMessageByHash() throws IOException {
        String result = Message.deleteMessageByHash("00:1:WHERETIME", jsonFile.toString());

        assertEquals("Message: \"" + MSG_2 + "\" successfully deleted.", result);
        assertEquals(Collections.singletonList(MSG_5), Message.getStoredMessageTexts());
        assertFalse(Message.getMessageIDs().contains(ID_2));
        assertFalse(Message.getMessageHashes().contains("00:1:WHERETIME"));

        String json = new String(Files.readAllBytes(jsonFile), StandardCharsets.UTF_8);
        assertFalse(json.contains(ID_2));
        assertTrue(json.contains(ID_5));
    }

    @Test
    public void testDeleteMessageByHashNotFound() {
        assertEquals("No stored message found with hash: 99:9:NONE",
                Message.deleteMessageByHash("99:9:NONE", jsonFile.toString()));
        assertEquals(2, Message.getStoredMessageTexts().size());
    }

    // ---------- Reports ----------

    @Test
    public void testSentReportShowsHashRecipientAndMessage() {
        String report = Message.displaySentReport();

        assertTrue(report.contains("Message Hash: 00:0:DIDCAKE"));
        assertTrue(report.contains("Recipient: +27834557896"));
        assertTrue(report.contains("Message: " + MSG_1));
        assertTrue(report.contains("Message Hash: 00:3:ITTIME"));
        assertFalse(report.contains(MSG_2));
    }

    @Test
    public void testStoredReportShowsFullDetails() {
        String report = Message.displayStoredReport();

        assertTrue(report.contains("Message ID: " + ID_2));
        assertTrue(report.contains("Message Hash: 00:1:WHERETIME"));
        assertTrue(report.contains("Sender: Kyl Smith"));
        assertTrue(report.contains("Recipient: +27838884567"));
        assertTrue(report.contains("Message: " + MSG_5));
    }

    @Test
    public void testSenderAndRecipientOfStoredMessages() {
        assertEquals("Sender: Kyl Smith | Recipient: +27838884567\n"
                + "Sender: Kyl Smith | Recipient: +27838884567",
                Message.displaySenderAndRecipientOfStored());
    }

    // ---------- Reading the JSON file into the array ----------

    @Test
    public void testReadJsonFileIntoStoredArray() {
        Message.resetSentMessages();
        int loaded = Message.loadStoredMessages(jsonFile.toString());

        assertEquals(2, loaded);
        assertEquals(Arrays.asList(MSG_2, MSG_5), Message.getStoredMessageTexts());
        assertEquals(Arrays.asList(ID_2, ID_5), Message.getMessageIDs());
        assertTrue(Message.displayStoredReport().contains("Sender: Kyl Smith"));
    }

    @Test
    public void testReadMissingJsonFile() {
        Message.resetSentMessages();
        assertEquals(0, Message.loadStoredMessages(jsonFile + ".missing"));
        assertTrue(Message.getStoredMessageTexts().isEmpty());
    }
}
}