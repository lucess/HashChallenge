package com.hashchallenge.model;

import java.util.List;

public class HashResponse {

    private List<User> users;
    private List<Post> posts;

    public HashResponse(List<User> users, List<Post> posts) {
        this.users = users;
        this.posts = posts;
    }

    public List<User> getUsers() {
        return users;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Post> getPosts() {
        return posts;
    }
    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

}
