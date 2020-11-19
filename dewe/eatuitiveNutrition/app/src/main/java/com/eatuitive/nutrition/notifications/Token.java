package com.eatuitive.nutrition.notifications;

/* @Created by shehab September --- November 2020*/

public class Token {
    /* An FCM Token, or much commonly known as a registrationToken.
    An ID is issued by the GCM
    connection servers to the client app that allows it receive message  */

    String token;

    public Token() {
    }

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
