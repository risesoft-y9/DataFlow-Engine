package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.y9public.entity.DataInterfaceEntity;

public interface DataInterfaceRepository
    extends JpaRepository<DataInterfaceEntity, String>, JpaSpecificationExecutor<DataInterfaceEntity> {

    List<DataInterfaceEntity> findByPatternAndTenantId(Integer pattern, String tenantId);

    List<DataInterfaceEntity> findByTenantId(String tenantId);

}
