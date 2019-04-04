package com.vermeg.ala17.repository;

import com.vermeg.ala17.entity.Role;
import com.vermeg.ala17.entity.RoleName;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
