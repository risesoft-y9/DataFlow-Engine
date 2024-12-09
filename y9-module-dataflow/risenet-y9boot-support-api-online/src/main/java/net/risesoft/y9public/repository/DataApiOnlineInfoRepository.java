package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataApiOnlineInfoEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataApiOnlineInfoRepository extends JpaRepository<DataApiOnlineInfoEntity, String>, JpaSpecificationExecutor<DataApiOnlineInfoEntity> {
	
	
}
