package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataBusinessEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface DataBusinessRepository extends JpaRepository<DataBusinessEntity, String>, JpaSpecificationExecutor<DataBusinessEntity> {
	
	Page<DataBusinessEntity> findByNameContaining(String name, Pageable pageable);
	
	List<DataBusinessEntity> findByParentIdOrderByCreateTime(String parentId);
	
	List<DataBusinessEntity> findByIdIn(List<String> Id);
	
	@Query("select p.id from DataBusinessEntity p where p.parentId = ?1")
	List<String> findByParentId(String parentId);
	
}
