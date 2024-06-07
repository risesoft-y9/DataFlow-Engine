package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataMappingEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface DataMappingRepository extends JpaRepository<DataMappingEntity, String>, JpaSpecificationExecutor<DataMappingEntity> {
	
	Page<DataMappingEntity> findByClassNameContaining(String className, Pageable pageable);
	
	DataMappingEntity findByTypeNameAndClassNameAndFuncType(String typeName, String className, String funcType);
	
	List<DataMappingEntity> findByTypeName(String typeName);
	
	List<DataMappingEntity> findByTypeNameAndClassNameContaining(String typeName, String className);
	
	List<DataMappingEntity> findByTypeNameAndFuncType(String typeName, String funcType);
	
	@Query("select distinct(p.funcType) from DataMappingEntity p where p.typeName = ?1")
	List<String> getByTypeName(String typeName);
	
}
