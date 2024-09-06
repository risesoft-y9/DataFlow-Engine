package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataInterfaceEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataInterfaceRepository extends JpaRepository<DataInterfaceEntity, String>, JpaSpecificationExecutor<DataInterfaceEntity> {
	
	List<DataInterfaceEntity> findByPattern(Integer pattern);
	
}
