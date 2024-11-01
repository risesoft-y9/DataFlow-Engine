package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataArrangeEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataArrangeRepository extends JpaRepository<DataArrangeEntity, String>, JpaSpecificationExecutor<DataArrangeEntity> {
	
	List<DataArrangeEntity> findByPattern(Integer pattern);
	
}
