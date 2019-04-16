package com.vermeg.ala17.controller;

import com.vermeg.ala17.entity.Groupp;
import com.vermeg.ala17.entity.Tag;
import com.vermeg.ala17.entity.User;
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
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    TagRepository tagRepository;

    @GetMapping("user/subscribeToTag/{tagId}")
    public User subscribeToTag(@CurrentUser UserPrinciple userPrinciple, @PathVariable Long tagId){
        Optional<User> userOptional = userRepository.findByUsername(userPrinciple.getUsername());
        User user = userOptional
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Tag not found."));
        user.addTag(tag);
        return userRepository.save(user);
    }

    @GetMapping("user/all")
    public Iterable<User> getall(){
        return userRepository.findAll();
    }

    @PostMapping("/user/sub")
    public User subToTag(@CurrentUser UserPrinciple userPrinciple, @RequestBody Iterable<Long> tags){
        User user1 = userRepository.findByUsername(userPrinciple.getUsername())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        user1.setSubscribedToTags(new ArrayList<>());
        tagRepository.findAllById(tags).forEach(tag -> user1.addTag(tag));
        return userRepository.save(user1);
    }

    @GetMapping("/user/getGroupsAdmin")
    public List<Groupp> getGroupsAdmin(@CurrentUser UserPrinciple userPrinciple){
        User user1 = userRepository.findByUsername(userPrinciple.getUsername())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        return user1.getGroupsAdminOf();
    }
    @GetMapping("/user/getGroupsMember")
    public List<Groupp> getGroupsMember(@CurrentUser UserPrinciple userPrinciple){
        User user1 = userRepository.findByUsername(userPrinciple.getUsername())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        return user1.getGroupsMemberOf();
    }
}
