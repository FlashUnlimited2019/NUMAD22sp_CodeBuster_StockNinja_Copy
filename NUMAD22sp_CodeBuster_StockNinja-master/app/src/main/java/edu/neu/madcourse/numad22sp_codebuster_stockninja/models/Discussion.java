package edu.neu.madcourse.numad22sp_codebuster_stockninja.models;

public class Discussion {
    String discussionId;
    String title;
    String content;
    String timestamp;
    String username;
    String place;

    public Discussion() {

    }

    public Discussion(String title, String content, String timestamp, String username, String place) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.username = username;
        this.place = place;
    }


    public String getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(String discussionId) {
        this.discussionId = discussionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
