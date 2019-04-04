package com.vermeg.ala17.controller;

import com.vermeg.ala17.entity.Tag;
import com.vermeg.ala17.entity.User;
import com.vermeg.ala17.repository.TagRepository;
import com.vermeg.ala17.repository.UserRepository;
import com.vermeg.ala17.security.CurrentUser;
import com.vermeg.ala17.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class TagController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TagRepository tagRepository;

    @PostMapping("/tag/create")
    public Tag createTag(@CurrentUser UserPrinciple user, @RequestBody Tag tag){
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        User user1 = userOptional
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        tag.setCreatedBy(user1);
        return tagRepository.save(tag);
    }
}
