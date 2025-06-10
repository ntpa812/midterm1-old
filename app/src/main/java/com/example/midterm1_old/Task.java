package com.example.midterm1_old;

public class Task {
    private int id;
    private String name;
    private String description;
    private String status;
    private int assigneeId;
    private int projectId;

    public Task(int id, String name, String description, String status, int assigneeId, int projectId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.assigneeId = assigneeId;
        this.projectId = projectId;
    }

    // Getter và Setter
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
