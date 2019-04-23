package com.vermeg.ala17.repository;

import com.vermeg.ala17.entity.VoteA;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface VoteARepository extends CrudRepository<VoteA, Long> {
    List<VoteA> findByAnswer_idAndUpDown(Long qId, String upDown);
    Optional<VoteA> findByUser_idAndAnswer_id(Long uId, Long qId);
}
