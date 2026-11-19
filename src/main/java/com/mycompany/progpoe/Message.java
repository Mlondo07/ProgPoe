package com.mycompany.progpoe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents a single QuickChat message and contains the logic used to
 * validate, hash, send, store, search, delete and report on messages.
 *
 * @author Student
 */
public class Message {

    private static final int MAX_MESSAGE_LENGTH = 250;
    private static final int ID_LENGTH = 10;
    private static final Random RANDOM = new Random();

    /** Default file that stored messages are saved to. */
    public static final String DEFAULT_STORAGE_FILE = "messages.json";

    // Shared across all messages while the program is running
    private static int totalSentMessages = 0;

    // Part 3 arrays (populated while the program runs - no hard-coding)
    private static final List<Message> sentMessages = new ArrayList<>();         // Sent Message array
    private static final List<Message> disregardedMessages = new ArrayList<>();  // Disregarded Messages array
    private static final List<Message> storedMessages = new ArrayList<>();       // Stored Messages array
    private static final List<String> messageHashes = new ArrayList<>();         // Message Hash array
    private static final List<String> messageIDs = new ArrayList<>();            // Message ID array

    /** The logged-in user, used as the sender of every new message. */
    private static String currentSender = "";

    private final String messageID;
    private final int messageNum;
    private final String recipient;
    private final String message;
    private String sender;
    private String storageFile = DEFAULT_STORAGE_FILE;

    /**
     * Creates a message with a randomly generated ten-digit message ID.
     *
     * @param messageNum the message number (taken from the loop counter)
     * @param recipient  the recipient cell number (international format)
     * @param message    the message text
     */
    public Message(int messageNum, String recipient, String message) {
        this(null, messageNum, recipient, message);
    }

    /**
     * Creates a message with a supplied message ID. Used for unit testing,
     * because a random ID can never be asserted against a fixed expected hash.
     * If the ID is null, a random one is generated.
     */
    public Message(String messageID, int messageNum, String recipient, String message) {
        this.messageNum = messageNum;
        this.recipient = recipient;
        this.message = message;
        this.sender = currentSender;
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
     * True when the recipient number has the +27 international code
     * followed by no more than ten digits.
     */
    public boolean isRecipientValid() {
        return isRecipientNumberValid(recipient);
    }

    /**
     * Static version of the recipient check, so the number can be validated
     * before a Message object is created.
     */
    public static boolean isRecipientNumberValid(String number) {
        return number != null && number.matches("^\\+27[0-9]{9,10}$");
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
     * Handles the user's choice for this message and fills the matching array.
     *
     * @param choice 1 = send, 2 = disregard, 3 = store to send later
     * @return the status message for the chosen option
     */
    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalSentMessages++;
                registerIdentifiers();
                return "Message successfully sent.";
            case 2:
                disregardedMessages.add(this);
                registerIdentifiers();
                return "Press 0 to delete the message.";
            case 3:
                storeMessage();
                storedMessages.add(this);
                registerIdentifiers();
                return "Message successfully stored.";
            default:
                return "Invalid selection.";
        }
    }

    /**
     * Adds this message's ID and hash to the Message ID and Message Hash arrays.
     */
    private void registerIdentifiers() {
        if (!messageIDs.contains(messageID)) {
            messageIDs.add(messageID);
            messageHashes.add(createMessageHash());
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
        String entry = toJson();

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
     * Builds one JSON object (with a leading indent) for this message.
     */
    private String toJson() {
        return "  {\n"
                + "    \"messageID\": \"" + escapeJson(messageID) + "\",\n"
                + "    \"messageNum\": " + messageNum + ",\n"
                + "    \"sender\": \"" + escapeJson(sender) + "\",\n"
                + "    \"recipient\": \"" + escapeJson(recipient) + "\",\n"
                + "    \"message\": \"" + escapeJson(message) + "\",\n"
                + "    \"messageHash\": \"" + escapeJson(createMessageHash()) + "\"\n"
                + "  }";
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
     * Clears every array and the counter (used by unit tests).
     */
    static void resetSentMessages() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        totalSentMessages = 0;
    }

    /**
     * Changes the JSON file this message is stored in (used by unit tests).
     */
    void setStorageFile(String storageFile) {
        this.storageFile = storageFile;
    }

    // =====================================================================
    //  PART 3 - arrays, stored messages, searching, deleting and reports
    // =====================================================================

    /** Sets the logged-in user's name, used as the sender of new messages. */
    public static void setCurrentSender(String senderName) {
        currentSender = (senderName == null) ? "" : senderName;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    // ----- Array getters (copies, so the arrays cannot be changed from outside) -----

    private static List<String> messageTexts(List<Message> list) {
        List<String> texts = new ArrayList<>();
        for (Message m : list) {
            texts.add(m.message);
        }
        return texts;
    }

    /** Contents of the Sent Message array (message texts). */
    public static List<String> getSentMessageTexts() {
        return messageTexts(sentMessages);
    }

    /** Contents of the Disregarded Messages array (message texts). */
    public static List<String> getDisregardedMessageTexts() {
        return messageTexts(disregardedMessages);
    }

    /** Contents of the Stored Messages array (message texts). */
    public static List<String> getStoredMessageTexts() {
        return messageTexts(storedMessages);
    }

    /** Contents of the Message Hash array. */
    public static List<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    /** Contents of the Message ID array. */
    public static List<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    // ----- 2a. Display the sender and recipient of all stored messages -----

    public static String displaySenderAndRecipientOfStored() {
        if (storedMessages.isEmpty()) {
            return "There are no stored messages.";
        }
        StringBuilder out = new StringBuilder();
        for (Message m : storedMessages) {
            out.append("Sender: ").append(m.sender)
               .append(" | Recipient: ").append(m.recipient).append("\n");
        }
        return out.toString().trim();
    }

    // ----- 2b. Display the longest stored message -----

    public static String longestStoredMessage() {
        Message longest = findLongestStored();
        return (longest == null) ? "There are no stored messages." : longest.message;
    }

    /** Longest stored message with its details (shown in the menu). */
    public static String longestStoredMessageDetails() {
        Message longest = findLongestStored();
        if (longest == null) {
            return "There are no stored messages.";
        }
        return "Message: " + longest.message + "\n"
                + "Recipient: " + longest.recipient + "\n"
                + "Message ID: " + longest.messageID + "\n"
                + "Message Hash: " + longest.createMessageHash();
    }

    private static Message findLongestStored() {
        Message longest = null;
        for (Message m : storedMessages) {
            if (longest == null || m.message.length() > longest.message.length()) {
                longest = m;
            }
        }
        return longest;
    }

    // ----- 2c. Search for a message ID -----

    /** Finds a message by ID in the sent, stored and disregarded arrays (null if not found). */
    private static Message findByID(String id) {
        List<List<Message>> all = new ArrayList<>();
        all.add(sentMessages);
        all.add(storedMessages);
        all.add(disregardedMessages);
        for (List<Message> list : all) {
            for (Message m : list) {
                if (m.messageID.equals(id)) {
                    return m;
                }
            }
        }
        return null;
    }

    /** Returns the message text for a message ID. */
    public static String searchByMessageID(String id) {
        Message found = findByID(id == null ? "" : id.trim());
        return (found == null) ? "No message found with ID: " + id : found.message;
    }

    /** Returns the recipient and message for a message ID (shown in the menu). */
    public static String searchByMessageIDDetails(String id) {
        Message found = findByID(id == null ? "" : id.trim());
        if (found == null) {
            return "No message found with ID: " + id;
        }
        return "Recipient: " + found.recipient + "\nMessage: " + found.message;
    }

    // ----- 2d. Search for all messages (sent or stored) for a recipient -----

    public static List<String> searchByRecipient(String recipientNumber) {
        List<String> results = new ArrayList<>();
        String wanted = (recipientNumber == null) ? "" : recipientNumber.trim();
        for (Message m : sentMessages) {
            if (m.recipient.equals(wanted)) {
                results.add(m.message);
            }
        }
        for (Message m : storedMessages) {
            if (m.recipient.equals(wanted)) {
                results.add(m.message);
            }
        }
        return results;
    }

    // ----- 2e. Delete a stored message using its message hash -----

    /** Deletes a stored message by hash and updates the default JSON file. */
    public static String deleteMessageByHash(String hash) {
        return deleteMessageByHash(hash, DEFAULT_STORAGE_FILE);
    }

    /** Deletes a stored message by hash and updates the given JSON file. */
    public static String deleteMessageByHash(String hash, String file) {
        String wanted = (hash == null) ? "" : hash.trim();
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            if (m.createMessageHash().equalsIgnoreCase(wanted)) {
                storedMessages.remove(i);
                messageIDs.remove(m.messageID);               // keep the parallel arrays in step
                messageHashes.remove(m.createMessageHash());
                saveStoredMessages(file);
                return "Message: \"" + m.message + "\" successfully deleted.";
            }
        }
        return "No stored message found with hash: " + hash;
    }

    // ----- 2f. Reports -----

    /** Report of all sent messages: hash, recipient and message. */
    public static String displaySentReport() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder report = new StringBuilder();
        int number = 1;
        for (Message m : sentMessages) {
            report.append("Message ").append(number++).append("\n")
                  .append("Message Hash: ").append(m.createMessageHash()).append("\n")
                  .append("Recipient: ").append(m.recipient).append("\n")
                  .append("Message: ").append(m.message).append("\n\n");
        }
        return report.toString().trim();
    }

    /** Report with the full details of all stored messages. */
    public static String displayStoredReport() {
        if (storedMessages.isEmpty()) {
            return "There are no stored messages.";
        }
        StringBuilder report = new StringBuilder();
        int number = 1;
        for (Message m : storedMessages) {
            report.append("Stored Message ").append(number++).append("\n")
                  .append("Message ID: ").append(m.messageID).append("\n")
                  .append("Message Hash: ").append(m.createMessageHash()).append("\n")
                  .append("Sender: ").append(m.sender).append("\n")
                  .append("Recipient: ").append(m.recipient).append("\n")
                  .append("Message: ").append(m.message).append("\n\n");
        }
        return report.toString().trim();
    }

    // ----- Reading and writing the JSON file -----

    /** Rewrites the JSON file so it contains exactly the stored messages array. */
    public static void saveStoredMessages(String file) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < storedMessages.size(); i++) {
            json.append(i == 0 ? "\n" : ",\n").append(storedMessages.get(i).toJson());
        }
        json.append(storedMessages.isEmpty() ? "]" : "\n]");
        try {
            Files.write(Paths.get(file), json.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Error saving JSON file: " + e.getMessage());
        }
    }

    /**
     * Reads the JSON file into the Stored Messages array (call once when the program starts).
     *
     * @return the number of stored messages loaded
     */
    public static int loadStoredMessages(String file) {
        storedMessages.clear();
        Path path = Paths.get(file);
        if (!Files.exists(path)) {
            return 0;
        }
        try {
            String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            for (Map<String, String> obj : parseJsonObjects(json)) {
                int num = 0;
                try {
                    num = Integer.parseInt(obj.getOrDefault("messageNum", "0"));
                } catch (NumberFormatException e) {
                    // keep 0 if the number is not valid
                }
                Message m = new Message(obj.get("messageID"), num,
                        obj.getOrDefault("recipient", ""), obj.getOrDefault("message", ""));
                m.sender = obj.getOrDefault("sender", "");
                m.storageFile = file;
                storedMessages.add(m);
                m.registerIdentifiers();
            }
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        }
        return storedMessages.size();
    }

    /**
     * Minimal parser for the JSON this program writes: an array of flat objects
     * whose values are strings or numbers.
     */
    private static List<Map<String, String>> parseJsonObjects(String json) {
        List<Map<String, String>> result = new ArrayList<>();
        int i = json.indexOf('[');
        if (i < 0) {
            return result;
        }
        i++;
        int n = json.length();
        while (i < n) {
            char c = json.charAt(i);
            if (c == ']') {
                break;
            }
            if (c != '{') {
                i++;
                continue;
            }
            Map<String, String> obj = new HashMap<>();
            i++;
            while (i < n && json.charAt(i) != '}') {
                if (json.charAt(i) != '"') {
                    i++;
                    continue;
                }
                int[] pos = {i};
                String key = readJsonString(json, pos);
                i = pos[0];
                while (i < n && json.charAt(i) != ':') {
                    i++;
                }
                i++;
                while (i < n && Character.isWhitespace(json.charAt(i))) {
                    i++;
                }
                String value;
                if (i < n && json.charAt(i) == '"') {
                    pos[0] = i;
                    value = readJsonString(json, pos);
                    i = pos[0];
                } else {
                    int start = i;
                    while (i < n && json.charAt(i) != ',' && json.charAt(i) != '}') {
                        i++;
                    }
                    value = json.substring(start, i).trim();
                }
                obj.put(key, value);
            }
            result.add(obj);
            i++;
        }
        return result;
    }

    /** Reads a JSON string starting at the opening quote; pos[0] ends after the closing quote. */
    private static String readJsonString(String s, int[] pos) {
        StringBuilder sb = new StringBuilder();
        int i = pos[0] + 1;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '"') {
                pos[0] = i + 1;
                return sb.toString();
            }
            if (c == '\\' && i + 1 < s.length()) {
                char e = s.charAt(++i);
                switch (e) {
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u':
                        if (i + 4 < s.length()) {
                            sb.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16));
                            i += 4;
                        }
                        break;
                    default: sb.append(e);
                }
            } else {
                sb.append(c);
            }
            i++;
        }
        pos[0] = s.length();
        return sb.toString();
    }
}