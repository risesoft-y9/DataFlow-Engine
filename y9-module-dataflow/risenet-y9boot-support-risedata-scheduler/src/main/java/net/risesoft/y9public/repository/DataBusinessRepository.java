package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataBusinessEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataBusinessRepository extends JpaRepository<DataBusinessEntity, String>, JpaSpecificationExecutor<DataBusinessEntity> {
	
	Page<DataBusinessEntity> findByNameContaining(String name, Pageable pageable);
	
	List<DataBusinessEntity> findByParentIdOrderByCreateTime(String parentId);
	
}
