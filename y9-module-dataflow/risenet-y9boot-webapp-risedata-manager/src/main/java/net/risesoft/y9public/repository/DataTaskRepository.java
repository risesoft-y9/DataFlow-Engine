package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataTaskEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataTaskRepository extends JpaRepository<DataTaskEntity, String>, JpaSpecificationExecutor<DataTaskEntity> {
	
	List<DataTaskEntity> findByBusinessId(String businessId);
	
}
