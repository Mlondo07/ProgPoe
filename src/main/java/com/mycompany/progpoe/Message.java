/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.progpoe;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author Student
 */
public class Message {
    public String messageID;
    public int messageNum;
    public String recipient;
    public String message;
    public static int totalSentMessages = 0;
    
    
    public Message(int messageNum, String recipient, String message) {
        this.messageNum = messageNum;
        this.recipient = recipient;
        this.message = message;
        this.messageID = generateRandomMessageID();
    }

public String generateRandomMessageID() {
        Random rand = new Random();
        long id = 1000000000L + (long)(rand.nextDouble() * 9000000000L);
        return String.valueOf(id);
    }
 public String getMessageID() {
        return messageID;
    }

 public boolean checkMessageID() {
        return this.messageID != null && this.messageID.length() <= 10;
    }
public String checkRecipientCell() {
        if (this.recipient != null && this.recipient.matches("^\\+27[0-9]{9}$")) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }
 public String validateMessageLength() {
        if (this.message.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = this.message.length() - 250;
            return "Message exceeds 250 characters by " + excess+" characters, " + " please reduce the size.";
        }
    }
 public String createMessageHash() {
        if (this.message == null || this.message.trim().isEmpty() || this.messageID == null) {
            return "";
        }

        String[] words = this.message.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        String idPrefix = this.messageID.length() >= 2 ? this.messageID.substring(0, 2) : this.messageID;
        String rawHash = idPrefix + ":" + this.messageNum + ":" + firstWord + lastWord;
        return rawHash.toUpperCase();
    }
public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                totalSentMessages++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storeMessage();
                return "Message successfully stored";
            default:
                return "Invalid selection.";
        }
    }
 public void storeMessage() {
        String filename = "messages.json";
        String jsonEntry = String.format(
            "{\n  \"messageID\": \"%s\",\n  \"messageNum\": %d,\n  \"recipient\": \"%s\",\n  \"message\": \"%s\",\n  \"messageHash\": \"%s\"\n}",
            messageID, messageNum, recipient, message.replace("\"", "\\\""), createMessageHash()
        );

        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write(jsonEntry + "\n");
        } catch (IOException e) {
            System.out.println("Error saving JSON file: " + e.getMessage());
        }
    }
  public String printMessages() {
        return "Message ID: " + messageID + "\n"
             + "Message Hash: " + createMessageHash() + "\n"
             + "Recipient: " + recipient + "\n"
             + "Message: " + message;
    }

    public static int returnTotalMessages() {
        return totalSentMessages;
    }
}



