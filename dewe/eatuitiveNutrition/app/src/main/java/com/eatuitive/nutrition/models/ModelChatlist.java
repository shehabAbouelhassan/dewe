package com.eatuitive.nutrition.models;

/* @Created by shehab September --- November 2020*/

public class ModelChatlist {

    String id;// we wil need that id to get chat list, sender/receiver uid

    public ModelChatlist() {
    }

    public ModelChatlist(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
