package com.vermeg.ala17.repository;

import com.vermeg.ala17.entity.Groupp;
import com.vermeg.ala17.entity.Question;
import com.vermeg.ala17.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QuestionRepository extends CrudRepository<Question, Long> {
    //@Query("SELECT q FROM Question q WHERE :tags MEMBER OF q.tags")
    List<Question> findByTagsContainsAndGroupIn(Tag tag, List<Groupp> groupps);
}
