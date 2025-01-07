package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataTaskCoreEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataTaskCoreRepository extends JpaRepository<DataTaskCoreEntity, String>, JpaSpecificationExecutor<DataTaskCoreEntity> {
	
	List<DataTaskCoreEntity> findByTaskId(String taskId);
	
	List<DataTaskCoreEntity> findByTaskIdAndTypeNameAndDataTypeAndKeyName(String taskId, String typeName, String dataType, String keyName);
	
	DataTaskCoreEntity findByTaskIdAndTypeNameAndDataTypeAndKeyNameAndSequence(String taskId, String typeName, 
			String dataType, String keyName, Integer sequence);
	
	List<DataTaskCoreEntity> findByTaskIdAndTypeNameAndDataTypeAndSequence(String taskId, String typeName, String dataType, Integer sequence);
	
	DataTaskCoreEntity findByTaskIdAndArgsIdAndSequence(String taskId, String argsId, Integer sequence);
	
	long countByArgsId(String argsId);
	
	List<DataTaskCoreEntity> findByTaskIdAndTypeNameAndKeyNameAndArgsIdIsNotNull(String taskId, String typeName, String keyName);
	
}
