package com.vermeg.ala17.controller;

import com.vermeg.ala17.entity.Answer;
import com.vermeg.ala17.entity.Question;
import com.vermeg.ala17.entity.Tag;
import com.vermeg.ala17.payload.JiraIssueResponse;
import com.vermeg.ala17.repository.AnswerRepository;
import com.vermeg.ala17.repository.QuestionRepository;
import com.vermeg.ala17.repository.TagRepository;
import com.vermeg.ala17.services.JiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class TestController {

  @Autowired
  QuestionRepository questionRepository;
  @Autowired
  AnswerRepository answerRepository;
  @Autowired
  TagRepository tagRepository;
  @Autowired
  JiraService jiraService;

  @GetMapping("/api/test/user")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  public String userAccess() {
    return ">>> User Contents!";
  }
  
  @GetMapping("/api/test/pm")
  @PreAuthorize("hasRole('PM') or hasRole('ADMIN')")
  public String projectManagementAccess() {
    return ">>> Board Management Project";
  }
  
  @GetMapping("/api/test/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return ">>> Admin Contents";
  }

  @GetMapping("/api/test/db")
  @PreAuthorize("hasRole('USER')")
  public List<Answer> testdb() {
    Question q = new Question();
    q.setTitle("tryme44");
    q.setTxt("ttttttrrrrryyyyb");

    Answer a = new Answer();
    a.setTxt("answerrrrr222b");

    q.addAnswer(a);

    questionRepository.save(q);

    return questionRepository.findById(1L).get().getAnswers();
  }

  @GetMapping("/api/test/db2")
  @PreAuthorize("hasRole('USER')")
  public void init(){
    Tag tag = tagRepository.findById(1L).get();
    Question q = new Question();
    q.setTitle("tryme44");
    q.setTxt("ttttttrrrrryyyyb");
    tag.addQuestion(q);
    tagRepository.save(tag);
  }

  @GetMapping("/api/test/getSearchResults/{str}")
  public ArrayList<String[]> k(@PathVariable String str){
    return jiraService.getIssuesAsArray(str);
  }

  @GetMapping("/api/test/getJiraIssue/{str}")
  public JiraIssueResponse kk(@PathVariable String str){
    return jiraService.getJiraIssueResponse(str);
  }

}