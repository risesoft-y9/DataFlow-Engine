package net.risesoft.y9public.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.y9public.entity.DataLogEntity;

public interface DataLogRepository
    extends JpaRepository<DataLogEntity, String>, JpaSpecificationExecutor<DataLogEntity> {

    Page<DataLogEntity> findByJobId(String jobId, Pageable pageable);

}
