package com.vermeg.ala17.repository;

import com.vermeg.ala17.entity.Question;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Long> {
}
