package com.vermeg.ala17.repository;

import com.vermeg.ala17.entity.Groupp;
import com.vermeg.ala17.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    @Query("SELECT u FROM User u WHERE :group NOT MEMBER OF u.groupsMemberOf")
    List<User> findAllUsersNotMembersOf(@Param("group") Groupp groupp);
}
