package com.vermeg.ala17.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Groupp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "admin_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> admins;

    // may be omitted
    /*@ManyToMany
    private List<User> admins;*/

    // if asked not withing a group, will be added to the HomeQuestions group
    // at logstash, we select * where group = publicGroup
    @JsonIgnore
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Question> questions;

    public void addQuestion(Question question) {
        if(questions == null) {
            questions = new ArrayList<>();
        }
        questions.add(question);
    }
    public void addAdmin(User admin) {
        if(admins == null) {
            admins = new ArrayList<>();
        }
        admins.add(admin);
    }
    public void adduser(User user) {
        if(users == null) {
            users = new ArrayList<>();
        }
        users.add(user);
    }


}
