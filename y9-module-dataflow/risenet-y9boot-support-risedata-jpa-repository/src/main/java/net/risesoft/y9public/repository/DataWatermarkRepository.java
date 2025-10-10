package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataWatermarkEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataWatermarkRepository extends JpaRepository<DataWatermarkEntity, String>, JpaSpecificationExecutor<DataWatermarkEntity> {
	
}
