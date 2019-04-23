package com.vermeg.ala17.document;

import lombok.Data;

import java.util.Date;

@Data
public class AnswerDocument {
    private Long id;
    private Long question_id;
    private Date created_at;
    private String type;
    private Long author_id;
    private String txt;
    private boolean is_solution;
    private Date updated_at;
}
