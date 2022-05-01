package com.nikola.driver.network.model;

public class ChatObject {
    private boolean isMyText;
    private int providerId;
    private int bookingId;
    private String messageTime;
    private String personImage;

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    private String messageText;

    public boolean isMyText() {
        return isMyText;
    }

    public void setMyText(boolean myText) {
        isMyText = myText;
    }

    public ChatObject(){

    }

    public ChatObject(String personImage, String messageTime, String messageText, boolean isMyText) {
        this.personImage = personImage;
        this.messageTime = messageTime;
        this.messageText = messageText;
        this.isMyText = isMyText;
    }

    public String getPersonImage() {

        return personImage;
    }

    public void setPersonImage(String personImage) {
        this.personImage = personImage;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
