package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataProcessEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DataProcessRepository extends JpaRepository<DataProcessEntity, String>, JpaSpecificationExecutor<DataProcessEntity> {
	
	List<DataProcessEntity> findByArrangeIdOrderByStep(String arrangeId);
	
	@Transactional
	@Modifying
	@Query("delete from DataProcessEntity p where p.arrangeId = ?1")
	void deleteByArrangeId(String arrangeId);
	
}
