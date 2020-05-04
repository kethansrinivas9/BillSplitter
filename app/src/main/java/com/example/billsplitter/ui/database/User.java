package com.example.billsplitter.ui.database;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
//getting User's attributes
@IgnoreExtraProperties
public class User {

    public String name;
    public String email;
    public String userID;
    public static HashMap<String, String> map;
    public User(){}

    public User(String name, String email, String userID, HashMap<String, String> map){
        this.name = name;
        this.email = email;
        this.userID = userID;
        this.map = new HashMap<>();
        this.map = map;
    }



}
