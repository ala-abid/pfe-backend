package com.vermeg.ala17.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
//@Entity
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    /*@ManyToMany
    private List<User> users;

    // may be omitted
    @ManyToMany
    private List<User> admins;*/

    // if asked not withing a group, will be added to the HomeQuestions group
    // at logstash, we select * where group = HomeQuestions
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Question> questions;
}
