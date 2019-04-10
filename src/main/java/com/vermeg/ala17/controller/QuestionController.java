package com.vermeg.ala17.controller;

import com.vermeg.ala17.entity.*;
import com.vermeg.ala17.payload.QuestionCreateRequest;
import com.vermeg.ala17.repository.AnswerRepository;
import com.vermeg.ala17.repository.QuestionRepository;
import com.vermeg.ala17.repository.TagRepository;
import com.vermeg.ala17.repository.UserRepository;
import com.vermeg.ala17.security.CurrentUser;
import com.vermeg.ala17.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
public class QuestionController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    AnswerRepository answerRepository;

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
        User user1 = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        answer.setAuthor(user1);
        Question question = questionRepository.findById(qId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Question not found."));
        question.addAnswer(answer);
        return questionRepository.save(question);
    }

    @GetMapping("question/all")
    public List<Question> getAllQuestions(){
        List<Question> questionList = new ArrayList<>();
        questionRepository.findAll().forEach(questionList::add);
        return questionList;
    }

    @PostMapping("/question/answer/{id}")
    public Question addReply(@PathVariable Long id, @CurrentUser UserPrinciple userPrinciple, @RequestBody Reply reply){
        User user1 = userRepository.findByUsername(userPrinciple.getUsername())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        reply.setAuthor(user1);
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Answer not found."));
        reply.setAnswer(answer);
        answer.addReply(reply);
        answerRepository.save(answer);
        return answer.getQuestion();
    }
}
