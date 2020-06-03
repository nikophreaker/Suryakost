package com.nikxphreaker.suryakost.Model;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String level;
    private String search;

    public User (String id, String username, String imageURL, String status, String level, String search){
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status   = status;
        this.level   = level;
        this.search   = search;
    }

    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
