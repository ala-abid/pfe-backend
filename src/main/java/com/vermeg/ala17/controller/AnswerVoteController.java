package com.vermeg.ala17.controller;

import com.vermeg.ala17.entity.*;
import com.vermeg.ala17.repository.AnswerRepository;
import com.vermeg.ala17.repository.UserRepository;
import com.vermeg.ala17.repository.VoteARepository;
import com.vermeg.ala17.security.CurrentUser;
import com.vermeg.ala17.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
@RestController
public class AnswerVoteController {
    @Autowired
    VoteARepository voteARepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AnswerRepository answerRepository;


    @GetMapping("/vote/answer/{answerId}/{up_or_down}")
    public VoteA vote(@PathVariable Long answerId, @PathVariable String up_or_down, @CurrentUser UserPrinciple userPrinciple){
        User user = userRepository.findByUsername(userPrinciple.getUsername())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Answer not found."));
        Optional<VoteA> voteOptional= voteARepository.findByUser_idAndAnswer_id(user.getId(), answerId);
        if(voteOptional.isPresent()){
            if (voteOptional.get().getUpDown().equals(up_or_down)){
                voteOptional.get().setUpDown("naan");
            }
            else voteOptional.get().setUpDown(up_or_down);
            return voteARepository.save(voteOptional.get());
        }
        VoteA voteA = new VoteA();
        voteA.setUser(user);
        voteA.setAnswer(answer);
        voteA.setUpDown(up_or_down);
        return voteARepository.save(voteA);
    }

    @GetMapping("/vote/findByUserAndAnswer/{answerId}")
    public VoteA getVoteByUserAndQuestion(@PathVariable Long answerId, @CurrentUser UserPrinciple userPrinciple) {
        User user = userRepository.findById(userPrinciple.getId())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        return voteARepository.findByUser_idAndAnswer_id(user.getId(), answerId)
                .orElse(new VoteA());
    }

    @GetMapping("/vote/getTotalAnswer/{answerId}")
    public int getTotalVotes(@PathVariable Long answerId){
        return voteARepository.findByAnswer_idAndUpDown(answerId, "up").size()
                - voteARepository.findByAnswer_idAndUpDown(answerId, "down").size();
    }
}
