package com.vermeg.ala17.controller;

import com.vermeg.ala17.entity.Groupp;
import com.vermeg.ala17.entity.Question;
import com.vermeg.ala17.entity.User;
import com.vermeg.ala17.repository.GroupRepository;
import com.vermeg.ala17.repository.UserRepository;
import com.vermeg.ala17.security.CurrentUser;
import com.vermeg.ala17.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GroupController {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/group/create/{name}/{description}")
    public Groupp createGrp(@CurrentUser UserPrinciple userPrinciple, @PathVariable String name, @PathVariable String description){
        User user1 = userRepository.findByUsername(userPrinciple.getUsername())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        Groupp group = new Groupp();
        group.setName(name);
        group.setDescription(description);
        group.addAdmin(user1);
        group.adduser(user1);
        return groupRepository.save(group);
    }

    @GetMapping("/group/{groupId}/addUser/{userId}")
    public Groupp addUserToGrp(@PathVariable Long groupId, @PathVariable Long userId){
        User user1 = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        Groupp groupp = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Group not found."));
        groupp.adduser(user1);
        return groupRepository.save(groupp);
    }

    @GetMapping("/group/{groupId}/addAdmin/{adminId}")
    public Groupp addAdminToGrp(@PathVariable Long groupId, @PathVariable Long adminId, @CurrentUser UserPrinciple userPrinciple){
        User currentUser = userRepository.findById(userPrinciple.getId())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        Groupp groupp = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Group not found."));
        if (groupp.getUsers().contains(admin) && (groupp.getAdmins().size() == 0 || groupp.getAdmins().contains(currentUser))) {
            groupp.addAdmin(admin);
        }
        return groupRepository.save(groupp);
    }

    @DeleteMapping("/group/{groupId}/deleteUser/{userId}")
    public Groupp removeUser(@CurrentUser UserPrinciple userPrinciple, @PathVariable Long groupId, @PathVariable Long userId) {
        User currentUser = userRepository.findById(userPrinciple.getId())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        Groupp groupp = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Group not found."));
        if(groupp.getAdmins().contains(currentUser)) {
            groupp.getUsers().remove(userToRemove);
        }
        return groupRepository.save(groupp);
    }

    @DeleteMapping("/group/{groupId}/deleteAdmin/{userId}")
    public Groupp removeAdmin(@CurrentUser UserPrinciple userPrinciple, @PathVariable Long groupId, @PathVariable Long userId) {
        User currentUser = userRepository.findById(userPrinciple.getId())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));
        Groupp groupp = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Group not found."));
        if(groupp.getAdmins().contains(currentUser)) {
            groupp.getAdmins().remove(userToRemove);
        }
        return groupRepository.save(groupp);
    }

    @GetMapping("/group/{groupId}/getQuestions")
    public List<Question> getAllQs(@PathVariable Long groupId){
        Groupp groupp = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Group not found."));
        return groupp.getQuestions();
    }

    @GetMapping("/group/{id}")
    public Groupp getGroup(@PathVariable Long id){
        return groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Group not found."));
    }

    @GetMapping("/group/{id}/getUsers")
    public List<User> getGroupUsers(@PathVariable Long id){
        Groupp groupp =  groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Group not found."));
        return groupp.getUsers();
    }
}
