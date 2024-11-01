package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataArrangeLogEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataArrangeLogRepository extends JpaRepository<DataArrangeLogEntity, String>, JpaSpecificationExecutor<DataArrangeLogEntity> {
	
	DataArrangeLogEntity findByArrangeIdAndProcessId(String arrangeId, String processId);
	
}
