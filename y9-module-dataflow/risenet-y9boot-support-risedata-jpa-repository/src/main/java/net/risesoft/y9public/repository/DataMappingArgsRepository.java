package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataMappingArgsEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DataMappingArgsRepository extends JpaRepository<DataMappingArgsEntity, String>, JpaSpecificationExecutor<DataMappingArgsEntity> {
	
	DataMappingArgsEntity findByNameAndMappingId(String name, String mappingId);
	
	Page<DataMappingArgsEntity> findByMappingId(String mappingId, Pageable pageable);
	
	@Transactional
	@Modifying
	@Query("delete from DataMappingArgsEntity p where p.mappingId =?1")
	void deleteByMappingId(String mappingId);
	
	List<DataMappingArgsEntity> findByMappingId(String mappingId);
	
	List<DataMappingArgsEntity> findByUpNameAndMappingId(String upName, String mappingId);
	
}
