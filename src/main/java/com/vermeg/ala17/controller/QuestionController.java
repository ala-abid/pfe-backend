package com.vermeg.ala17.controller;

import com.vermeg.ala17.entity.Answer;
import com.vermeg.ala17.entity.Question;
import com.vermeg.ala17.entity.Tag;
import com.vermeg.ala17.entity.User;
import com.vermeg.ala17.payload.QuestionCreateRequest;
import com.vermeg.ala17.repository.QuestionRepository;
import com.vermeg.ala17.repository.TagRepository;
import com.vermeg.ala17.repository.UserRepository;
import com.vermeg.ala17.security.CurrentUser;
import com.vermeg.ala17.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
public class QuestionController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    TagRepository tagRepository;

    @PostMapping("/question/create")
    public Question createQuestion(@CurrentUser UserPrinciple user, @RequestBody QuestionCreateRequest questionCreateRequest){
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        Question question = new Question();
        if (userOptional.isPresent()){
            question.setAuthor(userOptional.get());
        }
        question.setTitle(questionCreateRequest.getTitle());
        question.setTxt(questionCreateRequest.getTxt());
        question.setUpVotes(0);
        question.setDownVotes(0);
        question.setSolved(false);
        questionCreateRequest.getTags().forEach( tagStr -> {
            Tag tag = tagRepository.findByName(tagStr)
                    .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Tag not found."));
            question.addTag(tag);
        });
        return questionRepository.save(question);
    }

    @GetMapping("/question/{id}")
    public Question getQuestion(@PathVariable Long id){
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Question not found."));
    }

    @PostMapping("/question/{qId}/addAnswer")
    public Question addAnswer(@PathVariable Long qId, @CurrentUser UserPrinciple user, @RequestBody Answer answer){
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        User user1 = userOptional
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        answer.setAuthor(user1);
        Question question = questionRepository.findById(qId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Question not found."));
        question.addAnswer(answer);
        return questionRepository.save(question);
    }
}
