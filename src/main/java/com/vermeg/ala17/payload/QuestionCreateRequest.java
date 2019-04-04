package com.vermeg.ala17.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class QuestionCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String txt;

    List<String> tags;
}
