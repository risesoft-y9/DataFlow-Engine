package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataTaskConfigEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface DataTaskConfigRepository extends JpaRepository<DataTaskConfigEntity, String>, JpaSpecificationExecutor<DataTaskConfigEntity> {
	
	DataTaskConfigEntity findByTaskId(String taskId);
	
	@Query("from DataTaskConfigEntity dtf where dtf.sourceTable = ?1 or dtf.targetTable = ?1")
	List<DataTaskConfigEntity> findByTableId(String tableId);
	
}
