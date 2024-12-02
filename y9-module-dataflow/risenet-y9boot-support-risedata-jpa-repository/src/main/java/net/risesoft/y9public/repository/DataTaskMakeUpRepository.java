package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.y9public.entity.DataTaskMakeUpEntity;

public interface DataTaskMakeUpRepository extends JpaRepository<DataTaskMakeUpEntity, String>, JpaSpecificationExecutor<DataTaskMakeUpEntity> {
	
	List<DataTaskMakeUpEntity> findByTaskId(String taskId);
	
	DataTaskMakeUpEntity findByTaskIdAndTypeNameAndTabIndex(String taskId, String typeName, Integer tabIndex);
	
}
