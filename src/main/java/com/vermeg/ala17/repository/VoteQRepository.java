package com.vermeg.ala17.repository;

import com.vermeg.ala17.entity.VoteQ;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface VoteQRepository extends CrudRepository<VoteQ, Long> {

    Optional<VoteQ> findByUser_idAndQuestion_id(Long uId, Long aId);
    List<VoteQ> findByQuestion_idAndUpDown(Long aId, String upDown);
    @Transactional
    void deleteByQuestion_id(Long qId);

}
