package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataProcessEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataProcessRepository extends JpaRepository<DataProcessEntity, String>, JpaSpecificationExecutor<DataProcessEntity> {
	
	List<DataProcessEntity> findByArrangeIdOrderByStep(String arrangeId);
	
}
