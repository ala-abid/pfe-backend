package com.vermeg.ala17.payload;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class JiraIssueResponse {
    private String summary;
    private String description;
    private List<Comment> comments;
    private String assignee;
    private Date creationDate;
    private String projectName;
    public void addComment(String author, Date creationDate, String txt){
        if(comments == null) comments = new ArrayList<Comment>();
        comments.add(new Comment(author, creationDate, txt));
    }
}

@Data
class Comment{
    private String author;
    private Date creationDate;
    private String txt;

    Comment(String author, Date creationDate, String txt) {
        this.author = author;
        this.creationDate = creationDate;
        this.txt = txt;
    }
}
