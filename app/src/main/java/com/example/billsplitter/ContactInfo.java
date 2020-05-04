package com.example.billsplitter;

import java.util.List;

public class ContactInfo {
    public int id;
    public String joke;
    public List<String> categories;

    public int getId() {
        return id;
    }

    public ContactInfo(int id, String joke, List<String> categories) {
        this.id = id;
        this.joke = joke;
        this.categories = categories;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
