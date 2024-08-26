package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataInterfaceParamsEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DataInterfaceParamsRepository extends JpaRepository<DataInterfaceParamsEntity, String>, JpaSpecificationExecutor<DataInterfaceParamsEntity> {
	
	List<DataInterfaceParamsEntity> findByParentIdAndDataType(String parentId, Integer dataType);
	
	@Transactional
	@Modifying
	@Query("delete from DataInterfaceParamsEntity dfp where dfp.parentId = ?1 and dfp.dataType = ?2")
	void deleteByParentIdAndDataType(String parentId, Integer dataType);
	
	DataInterfaceParamsEntity findByParentIdAndDataTypeAndParamName(String parentId, Integer dataType, String paramName);
	
}
