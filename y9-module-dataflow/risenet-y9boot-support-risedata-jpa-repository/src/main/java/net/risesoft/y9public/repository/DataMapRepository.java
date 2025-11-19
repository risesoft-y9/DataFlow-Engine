package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataMapEntity;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface DataMapRepository extends JpaRepository<DataMapEntity, String>, JpaSpecificationExecutor<DataMapEntity> {
	
	@Query("select p.originalData as originalData, p.targetData as targetData, p.fieldName as fieldName from DataMapEntity p "
			+ "where p.sourceId = ?1 and p.sourceTable = ?2 "
			+ "and p.targetId = ?3 and p.targetTable = ?4 and p.dataType = ?5")
	List<Map<String, Object>> findBySourceIdAndSourceTableAndTargetIdAndTargetTableAndDataType(String sourceId, String sourceTable, 
			String targetId, String targetTable, String dataType);
	
}
