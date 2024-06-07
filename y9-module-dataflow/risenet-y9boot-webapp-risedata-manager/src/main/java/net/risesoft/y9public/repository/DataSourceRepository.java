package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataSourceEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataSourceRepository extends JpaRepository<DataSourceEntity, String>, JpaSpecificationExecutor<DataSourceEntity> {
	
	DataSourceEntity findByBaseName(String baseName);
	
	List<DataSourceEntity> findByBaseNameContainingAndBaseType(String baseName, String baseType);
	
	Page<DataSourceEntity> findByBaseNameContaining(String baseName, Pageable pageable);
	
	List<DataSourceEntity> findByType(Integer type);
	
	List<DataSourceEntity> findByBaseTypeOrderByCreateTime(String baseType);
	
	long countByBaseType(String baseType);
	
}
