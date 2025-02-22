package com.vermeg.ala17.entity;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Question {

    //TODO add relationship with user to disallow lore than one vote

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    @Lob
    private String txt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    //TODO make bi
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private int upVotes;

    private int downVotes;

    private boolean isSolved;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "question_tag",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Groupp group;

    public void addAnswer(Answer answer){
        if (answers == null){
            answers = new ArrayList<>();
        }
        answer.setQuestion(this);
        answers.add(answer);
    }

    public void addTag(Tag tag){
        if(tags == null){
            tags = new ArrayList<>();
        }
        tags.add(tag);
    }
}
