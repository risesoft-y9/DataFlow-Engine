package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataTaskEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface DataTaskRepository extends JpaRepository<DataTaskEntity, String>, JpaSpecificationExecutor<DataTaskEntity> {
	
	List<DataTaskEntity> findByBusinessId(String businessId);
	
	long countByBusinessId(String businessId);
	
	long countByBusinessIdIn(List<String> businessId);
	
	@Query("select p.id from DataTaskEntity p where p.systemName = ?1 and p.tenantId = ?2")
	List<String> findBySystemNameAndTenantId(String systemName, String tenantId);
	
}
