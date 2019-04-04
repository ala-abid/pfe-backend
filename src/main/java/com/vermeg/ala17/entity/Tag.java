package com.vermeg.ala17.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @OneToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @JsonIgnore
    @ManyToMany(mappedBy = "subscribedToTags")
    private List<User> subscribedUsers;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private List<Question> questions;

    public void addQuestion(Question question){
        if(questions == null){
            questions = new ArrayList<>();
        }
        questions.add(question);
    }
}
