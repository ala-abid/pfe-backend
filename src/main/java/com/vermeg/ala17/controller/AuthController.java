package com.vermeg.ala17.controller;

import com.vermeg.ala17.entity.Groupp;
import com.vermeg.ala17.entity.Role;
import com.vermeg.ala17.entity.RoleName;
import com.vermeg.ala17.entity.User;
import com.vermeg.ala17.payload.JwtResponse;
import com.vermeg.ala17.payload.LoginRequest;
import com.vermeg.ala17.payload.SignUpRequest;
import com.vermeg.ala17.repository.GroupRepository;
import com.vermeg.ala17.repository.RoleRepository;
import com.vermeg.ala17.repository.UserRepository;
import com.vermeg.ala17.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
 
    @Autowired
    AuthenticationManager authenticationManager;
 
    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;
 
    @Autowired
    RoleRepository roleRepository;
 
    @Autowired
    PasswordEncoder encoder;
 
    @Autowired
    JwtProvider jwtProvider;
 
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
 
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
 
        SecurityContextHolder.getContext().setAuthentication(authentication);
 
        String jwt = jwtProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt, loginRequest.getUsername()));
    }
 
    @PostMapping("/signup")
    public User registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        /*if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<String>("Fail -> Username is already taken!",
                    HttpStatus.BAD_REQUEST);
        }
 
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<String>("Fail -> Email is already in use!",
                    HttpStatus.BAD_REQUEST);
        }*/
 
        // Creating user's account
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));
 
        //Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
 
        /*strRoles.forEach(role -> {
          switch(role) {
          case "admin":
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                  .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not found."));
            roles.add(adminRole);
            
            break;
          case "pm":
                Role pmRole = roleRepository.findByName(RoleName.ROLE_PM)
                  .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not found."));
                roles.add(pmRole);
                
            break;
          default:
              Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                  .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not found."));
              roles.add(userRole);              
          }
        });*/
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not found."));
        roles.add(userRole);

        Groupp groupp = groupRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Group not found."));
        groupp.adduser(user);
        user.setRoles(roles);
        groupRepository.save(groupp);

 
        return user;
    }
}