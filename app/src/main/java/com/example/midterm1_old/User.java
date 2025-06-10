package com.example.midterm1_old;

public class User {
    private int id;
    private String fullName;
    private String email;
    private int role;

    public User(int id, String fullName, String email, int role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public int getRole() { return role; }
}
