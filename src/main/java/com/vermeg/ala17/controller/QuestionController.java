package com.vermeg.ala17.controller;

import com.vermeg.ala17.entity.*;
import com.vermeg.ala17.payload.QuestionCreateRequest;
import com.vermeg.ala17.repository.*;
import com.vermeg.ala17.security.CurrentUser;
import com.vermeg.ala17.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


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

    @Autowired
    GroupRepository groupRepository;

    @PostMapping("/question/create")
    public Question createQuestion(@CurrentUser UserPrinciple user, @RequestBody QuestionCreateRequest questionCreateRequest){
        User user1 = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        Question question = new Question();
        question.setAuthor(user1);
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

        //TODO add member checking
        Groupp group = groupRepository.findById(questionCreateRequest.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found!"));
        if(!group.getUsers().contains(user1)) {
            throw  new RuntimeException("user is not member of this group");
        }
        question.setGroup(group);
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
    public Iterable<Question> getAllQuestions(){
        return questionRepository.findAll();
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

    @GetMapping("/question/perCurrentUserTags")
    public Set<Question> getQuestionsPerCurrentUserTags(@CurrentUser UserPrinciple userPrinciple) {
        User user1 = userRepository.findByUsername(userPrinciple.getUsername())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        Set<Question> questionSet = new HashSet<>();
        user1.getSubscribedToTags().forEach( tag ->
                {
                    List<Question> questionList = questionRepository.findByTagsContainsAndGroupIn(tag, user1.getGroupsMemberOf());
                    questionSet.addAll(questionList);
                }
        );
        return questionSet;
    }
}
