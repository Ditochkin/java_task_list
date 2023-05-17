package com.example.tasklist;

public class Task {
    private final Integer id;
    private final String task;
    private final String date;
    private final String score;
    private final String compDate;
    private final String comment;
    private final String setScore;

    public Integer getId() {
        return id;
    }
    public String getTask() {
        return task;
    }

    public String getDate() {
        return date;
    }

    public String getScore() {
        return score;
    }

    public String getCompDate() {
        return compDate;
    }

    public String getComment() {
        return comment;
    }

    public String getSetScore() {
        return setScore;
    }

    public Task(Integer id, String task, String date, String score, String compDate, String comment, String setScore) {
        this.id = id;
        this.task = task;
        this.date = date;
        this.score = score;
        this.compDate = compDate;
        this.comment = comment;
        this.setScore = setScore;
    }
}
