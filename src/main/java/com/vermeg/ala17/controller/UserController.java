package com.vermeg.ala17.controller;

import com.vermeg.ala17.entity.Tag;
import com.vermeg.ala17.entity.User;
import com.vermeg.ala17.repository.TagRepository;
import com.vermeg.ala17.repository.UserRepository;
import com.vermeg.ala17.security.CurrentUser;
import com.vermeg.ala17.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
