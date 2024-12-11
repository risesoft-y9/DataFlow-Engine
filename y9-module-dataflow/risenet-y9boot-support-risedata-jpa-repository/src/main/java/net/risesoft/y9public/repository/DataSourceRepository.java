package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataSourceEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface DataSourceRepository extends JpaRepository<DataSourceEntity, String>, JpaSpecificationExecutor<DataSourceEntity> {
	
	DataSourceEntity findByBaseNameAndTenantId(String baseName, String tenantId);
	
	List<DataSourceEntity> findByBaseNameContainingAndBaseTypeAndTenantId(String baseName, String baseType, String tenantId);
	
	Page<DataSourceEntity> findByBaseNameContainingAndTenantId(String baseName, String tenantId, Pageable pageable);
	
	List<DataSourceEntity> findByTypeAndTenantId(Integer type, String tenantId);
	
	List<DataSourceEntity> findByBaseTypeAndTenantIdOrderByCreateTime(String baseType, String tenantId);
	
	long countByBaseType(String baseType);
	
	@Query("select p.id from DataSourceEntity p where p.tenantId = ?1")
	List<String> findIdByTenantId(String tenantId);
	
	List<DataSourceEntity> findByTenantId(String tenantId);
	
}
