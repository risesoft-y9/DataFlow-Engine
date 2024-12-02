package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataSingleTaskConfigEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataSingleTaskConfigRepository extends JpaRepository<DataSingleTaskConfigEntity, String>, JpaSpecificationExecutor<DataSingleTaskConfigEntity> {
	
}
