package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataArrangeLogEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface DataArrangeLogRepository extends JpaRepository<DataArrangeLogEntity, String>, JpaSpecificationExecutor<DataArrangeLogEntity> {
	
	DataArrangeLogEntity findByArrangeIdAndProcessIdAndJobIdAndJobLogId(String arrangeId, String processId, Integer jobId, String jobLogId);
	
	List<DataArrangeLogEntity> findByArrangeIdOrderByCreateTime(String arrangeId);
	
    Page<DataArrangeLogEntity> findByArrangeId(String arrangeId, Pageable pageable);
    
    @Query("select max(identifier) from DataArrangeLogEntity p where p.arrangeId=?1")
	Integer getMaxIdentifier(String arrangeId);
	
}
