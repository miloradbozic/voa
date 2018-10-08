package com.videotel.voa.repository;

import com.videotel.voa.model.AssessmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentItemRepository extends JpaRepository<AssessmentItem, Long> {

}
