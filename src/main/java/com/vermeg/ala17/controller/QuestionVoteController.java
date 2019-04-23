package com.vermeg.ala17.controller;

import com.vermeg.ala17.entity.Question;
import com.vermeg.ala17.entity.User;
import com.vermeg.ala17.entity.VoteQ;
import com.vermeg.ala17.repository.QuestionRepository;
import com.vermeg.ala17.repository.UserRepository;
import com.vermeg.ala17.repository.VoteQRepository;
import com.vermeg.ala17.security.CurrentUser;
import com.vermeg.ala17.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
public class QuestionVoteController {
    @Autowired
    VoteQRepository voteQRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuestionRepository questionRepository;

    @GetMapping("/vote/question/{questionId}/{up_or_down}")
    public VoteQ vote(@PathVariable Long questionId, @PathVariable String up_or_down, @CurrentUser UserPrinciple userPrinciple){
        User user = userRepository.findByUsername(userPrinciple.getUsername())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Question not found."));
        Optional<VoteQ> voteOptional= voteQRepository.findByUser_idAndQuestion_id(user.getId(), questionId);
        if(voteOptional.isPresent()){
            if (voteOptional.get().getUpDown().equals(up_or_down)){
                voteOptional.get().setUpDown("naan");
            }
            else voteOptional.get().setUpDown(up_or_down);
            return voteQRepository.save(voteOptional.get());
        }
        VoteQ voteQ = new VoteQ();
        voteQ.setUser(user);
        voteQ.setQuestion(question);
        voteQ.setUpDown(up_or_down);
        return voteQRepository.save(voteQ);
    }

    @GetMapping("/vote/findByUserAndQuestion/{questionId}")
    public VoteQ getVoteByUserAndQuestion(@PathVariable Long questionId, @CurrentUser UserPrinciple userPrinciple) {
        User user = userRepository.findById(userPrinciple.getId())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        return voteQRepository.findByUser_idAndQuestion_id(user.getId(), questionId)
                .orElse(new VoteQ());
    }

    @GetMapping("/vote/getTotal/{questionId}")
    public int getTotalVotes(@PathVariable Long questionId){
        return voteQRepository.findByQuestion_idAndUpDown(questionId, "up").size()
                - voteQRepository.findByQuestion_idAndUpDown(questionId, "down").size();
    }
}
