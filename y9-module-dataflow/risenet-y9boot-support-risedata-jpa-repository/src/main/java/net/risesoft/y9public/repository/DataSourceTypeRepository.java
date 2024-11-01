package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataSourceTypeEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataSourceTypeRepository extends JpaRepository<DataSourceTypeEntity, String>, JpaSpecificationExecutor<DataSourceTypeEntity> {
	
	DataSourceTypeEntity findByName(String name);
	
	List<DataSourceTypeEntity> findByType(Integer type);
	
	Page<DataSourceTypeEntity> findByNameContaining(String name, Pageable pageable);
	
}
