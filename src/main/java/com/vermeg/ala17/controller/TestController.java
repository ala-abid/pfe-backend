package com.vermeg.ala17.controller;

import com.vermeg.ala17.document.AnswerDocument;
import com.vermeg.ala17.document.QuestionDocument;
import com.vermeg.ala17.entity.*;
import com.vermeg.ala17.payload.JiraIssueResponse;
import com.vermeg.ala17.repository.*;
import com.vermeg.ala17.security.CurrentUser;
import com.vermeg.ala17.security.UserPrinciple;
import com.vermeg.ala17.services.ESQuestionService;
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
  @Autowired
  ESQuestionService esQuestionService;
  @Autowired
  VoteQRepository voteQRepository;
  @Autowired
  UserRepository userRepository;

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

  //by title
  @GetMapping("/api/test/search/{query}")
  public List<QuestionDocument> search(@PathVariable String query) throws Exception {
    return esQuestionService.findByQuestion(query, "title");
  }

  @GetMapping("/api/test/searchContent/{query}")
  public List<QuestionDocument> searchContent(@PathVariable String query) throws Exception {
    return esQuestionService.findByQuestion(query, "txt");
  }

  @GetMapping("/api/test/searchAnswers/{query}")
  public List<AnswerDocument> searchAnswers(@PathVariable String query) throws Exception {
    return esQuestionService.findByAnswer(query);
  }

  @GetMapping("/moreLikeThis/{id}")
  public List<QuestionDocument> moreLikeThis(@PathVariable Long id, @CurrentUser UserPrinciple userPrinciple) throws Exception {
    List<QuestionDocument> questionDocumentList =  esQuestionService.getRelated(String.valueOf(id));
    if(questionDocumentList.size()>2) return questionDocumentList;
    Question question = questionRepository.findById(id).orElse(null);
    User user1 = userRepository.findByUsername(userPrinciple.getUsername())
            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
    try {
      List<Question> questions = questionRepository
              .findByTagsContainsAndGroupInOrderByIdDesc(question.getTags().get(0), user1.getGroupsMemberOf());
      for (int i = 0; i < questions.size() && questionDocumentList.size() < 3 ; i++) {
        boolean idExists = false;
        if(id.equals(questions.get(i).getId())) {
          idExists = true;
        }
        for (int j = 0; j < questionDocumentList.size(); j++) {
          if(questionDocumentList.get(j).getId().equals(questions.get(i).getId())) {
            idExists = true;
            break;
          }
        }
        if (!idExists) {
          QuestionDocument questionDocument1 = new QuestionDocument();
          questionDocument1.setTxt(questions.get(i).getTxt());
          questionDocument1.setTitle(questions.get(i).getTitle());
          questionDocument1.setId(questions.get(i).getId());
          questionDocumentList.add(questionDocument1);
        }
      }
    }catch (Exception e){
      e.printStackTrace();
    }
    return questionDocumentList;
  }

}