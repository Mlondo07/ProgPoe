/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.progpoe;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a single QuickChat message and contains the logic used to
 * validate, hash, send, store and display it.
 *
 * @author Student
 */
public class Message {

    public static final int MAX_MESSAGE_LENGTH = 250;
    public static final int ID_LENGTH = 10;
    public static final Random RANDOM = new Random();

    // Shared across all messages while the program is running
    public static int totalSentMessages = 0;
    public static final List<Message> sentMessages = new ArrayList<>();

    public final String messageID;
    public final int messageNum;
    public final String recipient;
    public final String message;
    public String storageFile = "messages.json";

    /**
     * Creates a message with a randomly generated ten-digit message ID.
     *
     *  messageNum the message number (taken from the loop counter)
     *  recipient  the recipient cell number (international format)
     *  message    the message text
     */
    public Message(int messageNum, String recipient, String message) {
        this(null, messageNum, recipient, message);
    }

    
    public Message(String messageID, int messageNum, String recipient, String message) {
        this.messageNum = messageNum;
        this.recipient = recipient;
        this.message = message;
        this.messageID = (messageID == null) ? generateRandomMessageID() : messageID;
    }

    /**
     * Builds a random ten-digit ID, one digit per loop iteration.
     */
    private String generateRandomMessageID() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < ID_LENGTH; i++) {
            id.append(RANDOM.nextInt(10));
        }
        return id.toString();
    }

    public String getMessageID() {
        return messageID;
    }

    /**
     * Checks that the message ID is not more than ten characters long.
     */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= ID_LENGTH;
    }

    /**
     * Returns the message shown when the ID has been created.
     */
    public String messageIDStatement() {
        return "Message ID generated: " + messageID;
    }

    /**
     * True when the recipient number has an international code (+27)
     * followed by nine digits (12 characters in total).
     */
    public boolean isRecipientValid() {
        return recipient != null && recipient.matches("^\\+27[0-9]{9}$");
    }

    /**
     * Validates the recipient cell number and returns the matching message.
     */
    public String checkRecipientCell() {
        if (isRecipientValid()) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    /**
     * True when the message is not longer than 250 characters.
     */
    public boolean isMessageLengthValid() {
        return message != null && message.length() <= MAX_MESSAGE_LENGTH;
    }

    /**
     * Checks the message length and returns the matching message.
     */
    public String validateMessageLength() {
        if (isMessageLengthValid()) {
            return "Message ready to send.";
        }
        int excess = message.length() - MAX_MESSAGE_LENGTH;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    /**
     * Creates the message hash: first two digits of the message ID, a colon,
     * the message number, a colon, then the first and last words of the
     * message (punctuation removed) - all in capitals. Example: 00:0:HITONIGHT
     */
    public String createMessageHash() {
        if (message == null || message.trim().isEmpty() || messageID == null) {
            return "";
        }
        String[] words = message.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^A-Za-z0-9]", "");
        String lastWord = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        String idPrefix = messageID.substring(0, Math.min(2, messageID.length()));

        return (idPrefix + ":" + messageNum + ":" + firstWord + lastWord).toUpperCase();
    }

    /**
     * Handles the user's choice for this message.
     *
     * @param choice 1 = send, 2 = disregard, 3 = store to send later
     * @return the status message for the chosen option
     */
    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalSentMessages++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid selection.";
        }
    }

    /**
     * Stores this message in a JSON file as part of a valid JSON array.
     *
     * Reference: Oracle (n.d.) Files (Java Platform SE 8). Available at:
     * https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html
     * (Accessed: [add the date you accessed it]).
     * Reference: JSON.org (n.d.) Introducing JSON. Available at:
     * https://www.json.org/json-en.html (Accessed: [add the date you accessed it]).
     */
    public void storeMessage() {
        String entry = "  {\n"
                + "    \"messageID\": \"" + escapeJson(messageID) + "\",\n"
                + "    \"messageNum\": " + messageNum + ",\n"
                + "    \"recipient\": \"" + escapeJson(recipient) + "\",\n"
                + "    \"message\": \"" + escapeJson(message) + "\",\n"
                + "    \"messageHash\": \"" + escapeJson(createMessageHash()) + "\"\n"
                + "  }";

        try {
            Path path = Paths.get(storageFile);
            String existing = Files.exists(path)
                    ? new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim()
                    : "";

            String updated;
            if (existing.startsWith("[") && existing.endsWith("]")) {
                String body = existing.substring(1, existing.length() - 1).trim();
                updated = body.isEmpty()
                        ? "[\n" + entry + "\n]"
                        : "[\n" + body + ",\n" + entry + "\n]";
            } else {
                updated = "[\n" + entry + "\n]";
            }
            Files.write(path, updated.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Error saving JSON file: " + e.getMessage());
        }
    }

    /**
     * Escapes characters that are not allowed raw inside a JSON string.
     */
    private static String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * Full details of this message in the required order:
     * Message ID, Message Hash, Recipient, Message.
     */
    public String messageDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + createMessageHash() + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + message;
    }

    /**
     * Returns the details of every message sent while the program is running.
     */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder all = new StringBuilder();
        for (Message m : sentMessages) {
            all.append(m.messageDetails()).append("\n\n");
        }
        return all.toString().trim();
    }

    /**
     * Returns the total number of messages sent.
     */
    public static int returnTotalMessages() {
        return totalSentMessages;
    }

    /**
     * Clears the sent-message list and counter (used by unit tests).
     */
    static void resetSentMessages() {
        sentMessages.clear();
        totalSentMessages = 0;
    }

    /**
     * Changes the JSON file this message is stored in (used by unit tests).
     */
    void setStorageFile(String storageFile) {
        this.storageFile = storageFile;
    }
}