package com.videotel.voa.repository;

import com.videotel.voa.model.AssessmentItem;
import com.videotel.voa.model.DbAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbAssessmentRepository extends JpaRepository<DbAssessment, Long> {

}
