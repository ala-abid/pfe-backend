package com.vermeg.ala17.repository;

import com.vermeg.ala17.entity.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TagRepository extends CrudRepository<Tag, Long> {
    Optional<Tag> findByName(String tagName);
}
